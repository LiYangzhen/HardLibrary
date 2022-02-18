package fudan.se.hardlibrary.service;

import fudan.se.hardlibrary.domain.Authority;
import fudan.se.hardlibrary.exception.BadRequestException;
import fudan.se.hardlibrary.domain.User;
import fudan.se.hardlibrary.repository.AuthorityRepository;
import fudan.se.hardlibrary.repository.UserRepository;
import fudan.se.hardlibrary.controller.request.RegisterRequest;
import fudan.se.hardlibrary.utils.JwtTokenUtil;
import fudan.se.hardlibrary.utils.MapUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * Class for user information management: login, register, changePassword and so on
 *
 * Possible exception thrown by this class：
 * @NotFoundException: http status: 404, reason: username not found.
 * @BadRequestException: http status: 400, reason: login password wrong, username has existed and so on.
 */
@Service
public class AuthService {
    private UserRepository userRepository;
    private AuthorityRepository authorityRepository;
    private PasswordEncoder encoder;
    private JwtUserDetailsService userDetailsService;

    @Resource
    private JwtTokenUtil jwtTokenUtil;

    @Resource
    private MapUtil mapUtil;

    @Autowired
    public AuthService(UserRepository userRepository, AuthorityRepository authorityRepository, PasswordEncoder encoder, JwtUserDetailsService userDetailsService) {
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
        this.encoder = encoder;
        this.userDetailsService = userDetailsService;
    }

    /**
     * 注册，注意：注册信息的格式以及验证码不会在这里被检测，他在controller里已经被检测了
     *
     * @param request   这个类的成员：username, password, email(null for admin register), authorities(Set<Sting>)
     * @return          成功时返回成功注册的User，失败时抛异常
     */
    public User register(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()) != null)
            throw new BadRequestException("User name has been registered");

        Set<Authority> authorities = new HashSet<>();
        if (request.getAuthorities() == null)
            throw new BadRequestException("Authority is empty");
        for (String str: request.getAuthorities())
            authorities.add(authorityRepository.findByAuthority(str));
        User user = new User(request.getUsername(), encoder.encode(request.getPassword()), request.getEmail(), authorities);
        return userRepository.save(user);
    }

    /**
     * 登录, 成功时返回的map所含信息：token, username, authorities, 都是String类型
     */
    public Map<String, String> login(String username, String password) {
        User user = (User) userDetailsService.loadUserByUsername(username);
        if (!encoder.matches(password, user.getPassword()))
            throw new BadRequestException("Wrong password");

        String token = jwtTokenUtil.generateToken(user);
        Map<String, String > response = new HashMap<>();
        response.put("token", token);
        response.put("username", user.getUsername());
        StringBuilder authorities = new StringBuilder();
        for (GrantedAuthority authority: user.getAuthorities())
            authorities.append(authority.getAuthority()).append(", ");
        response.put("authorities", authorities.substring(0, authorities.length() - 2));

        return response;
    }

    /**
     * 修改密码, 成功时返回{"message":"success"}, 失败时抛异常
     */
    public Map<String, String> changePass(String username, String oldPass, String newPass) {
        User user = (User) userDetailsService.loadUserByUsername(username);
        if (!encoder.matches(oldPass, user.getPassword()))
            throw new BadRequestException("Wrong oldPassword");
        if (oldPass.equals(newPass))
            throw new BadRequestException("The new password is as same as old password");
        user.setPassword(encoder.encode(newPass));
        userRepository.save(user);
        return mapUtil.getMessageMap("success");
    }

    /**
     * 支付罚款，功能暂且是清空用户的罚款
     */
    public Map<String, String> payFine(String username) {
        User user = (User) userDetailsService.loadUserByUsername(username);
        user.setFine(0F);
        userRepository.save(user);
        return mapUtil.getMessageMap("success");
    }

    /**
     * 获取用户所欠罚款
     */
    public float getFine(String username) {
        User user = (User) userDetailsService.loadUserByUsername(username);
        return user.getFine();
    }


}
