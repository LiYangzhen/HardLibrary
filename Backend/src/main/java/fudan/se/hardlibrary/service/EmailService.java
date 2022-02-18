package fudan.se.hardlibrary.service;

import fudan.se.hardlibrary.domain.*;
import fudan.se.hardlibrary.exception.BadRequestException;
import fudan.se.hardlibrary.repository.*;
import fudan.se.hardlibrary.utils.DateUtil;
import fudan.se.hardlibrary.utils.MapUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;


@Service
public class EmailService {

    JavaMailSender mailSender;
    VerificationCodeRepository verificationCodeRepository;
    BorrowService borrowService;
    BookService bookService;
    DateUtil dateUtil;

    @Resource
    BookCopiesRepository bookCopiesRepository;

    @Resource
    AuthorityRepository authorityRepository;

    @Resource
    UserRepository userRepository;

    @Resource
    BorrowRecordsRepository borrowRecordsRepository;

    @Resource
    MapUtil mapUtil;

    @Value("${spring.mail.username}")
    String from;

    @Autowired
    public EmailService(JavaMailSender mailSender, VerificationCodeRepository verificationCodeRepository, BorrowService borrowService, BookService bookService, DateUtil dateUtil) {
        this.mailSender = mailSender;
        this.verificationCodeRepository = verificationCodeRepository;
        this.borrowService = borrowService;
        this.bookService = bookService;
        this.dateUtil = dateUtil;
    }

    /**
     * 发送验证码，to: 接收邮箱，邮箱格式在controller里已经检测了
     */
    public Map<String, String> sendVerificationCode(String to) {
        String code = verificationCode();
        String content = "【HardLibrary】您正在进行注册验证，验证码 " + code + " ，有效时长为 3 分钟，请勿泄露给他人，若非本人操作请忽略。";
        send(to, "HardLibrary 注册验证", content);
        verificationCodeRepository.save(new Verification_code(to, code, dateUtil.toDateString(new Date(), "yyyy-MM-dd HH:mm:ss")));
        return mapUtil.getMessageMap("success");
    }

    /**
     * 验证验证码是否正确，只验证最新生成的，有效期三分钟
     */
    public void verificationCodeValidate(String email, String code) {
        Verification_code verification_code = verificationCodeRepository.findFirstByEmailOrderByIdDesc(email);
        if (verification_code == null || !verification_code.getCode().equals(code))
            throw new BadRequestException("Verification code wrong");
        String dateString = verification_code.getTime();
        if (dateString == null || (new Date().getTime() - dateUtil.stringToDate(dateString, "yyyy-MM-dd HH:mm:ss").getTime()) > 3 * 60 * 1000)
            throw new BadRequestException("Verification code has expired");
    }

    /**
     * 内部方法，发送邮件的封装方法
     */
    private void send(String to, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);

        mailSender.send(message);
    }

    /**
     * 群发垃圾邮件提醒
     */
    public Map<String, String> sendAlert() {
        String[] authorities = new String[] {"Reader"};
        for (String authority: authorities) {
            List<User> users = userRepository.findAllByAuthoritiesContaining(authorityRepository.findByAuthority(authority));
            for (User user: users)
                sendAlert(user);
        }
        return mapUtil.getMessageMap("success");
    }

    /**
     * 内部方法，给某一用户发垃圾邮件提醒
     */
    private void sendAlert(User user) {
        StringBuilder content = new StringBuilder();
        int credit = 0;

        if (user.getFine() > 0)
            content.append("您存在尚未缴纳的罚款：\n数值为：￥").append(user.getFine()).append("，请及时缴纳.\n\n");

        List<Book_copies> overdueList = appendAlert(content, user.getUsername(), "预约");
        for (Book_copies copy: overdueList) {
            borrowService.setStatusAndNull(copy, "可借阅");
            bookCopiesRepository.save(copy);
            Borrow_records borrow_records = borrowService.findRecord(user.getUsername(), copy.getIsbn());
            borrow_records.setStatus("已过期");
            borrow_records.setCredit(10);
            borrowRecordsRepository.save(borrow_records);
            credit += 10;
        }
        content.append("\n");

        appendAlert(content, user.getUsername(), "借阅");

        if (credit > 0)
            borrowService.creditDeduct(user, credit);

        if (content.length() > 5)
            send(user.getEmail(), "HardLibrary违规提醒", content.toString());

    }

    /**
     * 内部方法，生成提醒邮件的内容
     */
    private List<Book_copies> appendAlert(StringBuilder content, String username, String mode) {
        String status = "已" + mode;
        List<Book_copies> pending = new ArrayList<>();
        List<Book_copies> book_copies = bookCopiesRepository.findAllByBorrowerAndStatus(username, status);

        for (Book_copies copy: book_copies)
            if (borrowService.ifOverdue(copy))
                pending.add(copy);

        if (pending.size() > 0) {
            content.append("以下图书的").append(mode).append("已过期：\n-----\n");
            for (Book_copies copy: pending) {
                Book book = bookService.findByIsbn(copy.getIsbn().substring(0, copy.getIsbn().length() - 4));
                content.append("书名：").append(book.getName())
                        .append("\n副本标识：").append(copy.getIsbn())
                        .append("\n").append(mode).append("时间：").append(copy.getTime())
                        .append("\n").append(mode).append("时限：").append((copy.getTime_limit() / 3600)).append("h").append(((copy.getTime_limit() % 3600) / 60)).append("m");
                if (mode.equals("借阅")) {
                    double fine = (int)(100 * book.getPrice() / 4) / 100.0;
                    content.append("\n预计罚款：￥").append(fine);
                }
                content.append("\n-----\n");
            }
        }
        return pending;
    }

    /**
     * 内部方法，生成六位验证码
     */
    private String verificationCode() {
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 6; i ++)
            code.append(new Random().nextInt(10));
        return code.toString();
    }
}
