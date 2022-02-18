package fudan.se.hardlibrary.service;

import fudan.se.hardlibrary.domain.User;
import fudan.se.hardlibrary.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Important class for load user which used in everywhere
 *
 * Possible exception thrown by this class：
 * @NotFoundException: http status: 404, reason: username not found.
 */
@Service
public class JwtUserDetailsService implements UserDetailsService {

    private UserRepository userRepository;

    @Autowired
    public JwtUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 传入username，返回User对象（User extends UserDetails）
     */
    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null)
            throw new UsernameNotFoundException("Username: " + username + " not found.");
        return user;
    }
}
