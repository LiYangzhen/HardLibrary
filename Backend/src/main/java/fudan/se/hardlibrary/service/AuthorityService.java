package fudan.se.hardlibrary.service;

import fudan.se.hardlibrary.domain.Authority;
import fudan.se.hardlibrary.exception.BadRequestException;
import fudan.se.hardlibrary.exception.NotFoundException;
import fudan.se.hardlibrary.repository.AuthorityRepository;
import fudan.se.hardlibrary.utils.MapUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

@Service
public class AuthorityService {

    @Resource
    AuthorityRepository authorityRepository;

    @Resource
    MapUtil mapUtil;

    /**
     * 设置最大借阅数 不能为负，可以为null，不能设置管理员的
     */
    public Map<String, String> setMaxCopies(Integer maxCopies, String authority) {
        if (authority.equals("Admin") || authority.equals("Librarian"))
            throw new BadRequestException("Authority can't be admin or librarian");
        if (maxCopies != null && maxCopies < 0)
            throw new BadRequestException("Negative maxCopies");
        Authority auth = findAuthority(authority);
        auth.setMaxCopies(maxCopies);
        authorityRepository.save(auth);
        return mapUtil.getMessageMap("success");
    }

    /**
     * 设置最大预约时长 不能为负，可以为null，不能设置管理员的
     */
    public Map<String, String> setMaxReserveTime(Integer maxReserveTime, String authority) {
        if (authority.equals("Admin") || authority.equals("Librarian"))
            throw new BadRequestException("Authority can't be admin or librarian");
        if (maxReserveTime != null && maxReserveTime < 0)
            throw new BadRequestException("Negative maxReserveTime");
        Authority auth = findAuthority(authority);
        auth.setMaxReserveTime(maxReserveTime);
        authorityRepository.save(auth);
        return mapUtil.getMessageMap("success");
    }

    /**
     * 设置最大借阅时长 不能为负，可以为null，不能设置管理员的
     */
    public Map<String, String> setMaxBorrowTime(Integer maxBorrowTime, String authority) {
        if (authority.equals("Admin") || authority.equals("Librarian"))
            throw new BadRequestException("Authority can't be admin or librarian");
        if (maxBorrowTime != null && maxBorrowTime < 0)
            throw new BadRequestException("Negative maxBorrowTime");
        Authority auth = findAuthority(authority);
        auth.setMaxBorrowTime(maxBorrowTime);
        authorityRepository.save(auth);
        return mapUtil.getMessageMap("success");
    }

    /**
     * 获取某一权限允许的最大借阅数
     */
    public Integer getMaxCopies(String authority) {
        Authority auth = findAuthority(authority);
        return auth.getMaxCopies();
    }

    /**
     * 获取某一权限允许的最大预约时长
     */
    public Integer getMaxReserveTime(String authority) {
        Authority auth = findAuthority(authority);
        return auth.getMaxReserveTime();
    }

    /**
     * 获取某一权限允许的最大借阅时长
     */
    public Integer getMaxBorrowTime(String authority) {
        Authority auth = findAuthority(authority);
        return auth.getMaxBorrowTime();
    }

    /**
     * 封装异常的权限查找
     */
    private Authority findAuthority(String authority) {
        Authority auth = authorityRepository.findByAuthority(authority);
        if (auth == null)
            throw new NotFoundException("Authority: " + authority + " not found");
        return auth;
    }

}
