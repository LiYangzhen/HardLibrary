package fudan.se.hardlibrary.service;

import fudan.se.hardlibrary.domain.Reset_records;
import fudan.se.hardlibrary.domain.User;
import fudan.se.hardlibrary.exception.BadRequestException;
import fudan.se.hardlibrary.repository.BorrowRecordsRepository;
import fudan.se.hardlibrary.repository.ResetRecordsRepository;
import fudan.se.hardlibrary.repository.UserRepository;
import fudan.se.hardlibrary.utils.DateUtil;
import fudan.se.hardlibrary.utils.MapUtil;
import fudan.se.hardlibrary.utils.ValidateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class CreditService {

    JwtUserDetailsService userDetailsService;
    BorrowRecordsRepository borrowRecordsRepository;
    ResetRecordsRepository resetRecordsRepository;
    UserRepository userRepository;
    BorrowService borrowService;
    ValidateUtil validateUtil;
    DateUtil dateUtil;
    MapUtil mapUtil;

    static String format = "yyyy-MM-dd HH:mm:ss";

    @Autowired
    public CreditService(JwtUserDetailsService userDetailsService, BorrowRecordsRepository borrowRecordsRepository, ResetRecordsRepository resetRecordsRepository, UserRepository userRepository, BorrowService borrowService, ValidateUtil validateUtil, DateUtil dateUtil, MapUtil mapUtil) {
        this.userDetailsService = userDetailsService;
        this.borrowRecordsRepository = borrowRecordsRepository;
        this.resetRecordsRepository = resetRecordsRepository;
        this.userRepository = userRepository;
        this.borrowService = borrowService;
        this.validateUtil = validateUtil;
        this.dateUtil = dateUtil;
        this.mapUtil = mapUtil;
    }

    /**
     * 获取用户信用分，用户名不存在或者不为读者则报错
     */
    public Integer getCredit(String username) {
        validateUtil.authorityValidate(username, "Reader");
        User user = (User) userDetailsService.loadUserByUsername(username);
        return user.getCredit();
    }

    /**
     * 重置用户信用，有未缴纳罚款，逾期未还的书将报错，信用已为 100 不予重置
     */
    public Map<String, String> resetCredit(String username) {
        validateUtil.authorityValidate(username, "Reader");
        User user = (User) userDetailsService.loadUserByUsername(username);
        if (user.getCredit() >= 100)
            throw new BadRequestException("The user's credit is not less than 100");
        if (user.getFine() > 0) {
            resetRecordsRepository.save(new Reset_records(username, dateUtil.toDateString(new Date(), format), false));
            throw new BadRequestException("The user has unpaid fines");
        }
        borrowService.updateRecordsStatus(username);
        if (borrowRecordsRepository.findAllByBorrowerAndStatus(username, "已逾期").size() > 0) {
            resetRecordsRepository.save(new Reset_records(username, dateUtil.toDateString(new Date(), format), false));
            throw new BadRequestException("The user have to return all the overdue books");
        }
        resetRecordsRepository.save(new Reset_records(username, dateUtil.toDateString(new Date(), format), true));
        user.setCredit(100);
        userRepository.save(user);
        return mapUtil.getMessageMap("success");
    }


    /**
     * 获取用户信用重置记录，用于信用流水的显示
     */
    public List<Reset_records> getResetRecords(String username) {
        validateUtil.authorityValidate(username, "Reader");
        return resetRecordsRepository.findAllByUsernameOrderByIdDesc(username);
    }

}
