package fudan.se.hardlibrary.service;

import fudan.se.hardlibrary.TestConfig;
import fudan.se.hardlibrary.domain.*;
import fudan.se.hardlibrary.exception.BadRequestException;
import fudan.se.hardlibrary.repository.AuthorityRepository;
import fudan.se.hardlibrary.repository.BorrowRecordsRepository;
import fudan.se.hardlibrary.repository.UserRepository;
import fudan.se.hardlibrary.repository.VerificationCodeRepository;
import fudan.se.hardlibrary.utils.DateUtil;
import org.junit.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Random;


public class EmailServiceTest extends TestConfig {

    @Autowired
    EmailService emailService;

    @Autowired
    VerificationCodeRepository verificationCodeRepository;

    @Value("${spring.mail.username}")
    String from;

    @Resource
    JavaMailSender mailSender;

    @Resource
    DateUtil dateUtil;


    @Test
    public void sendVerificationCode1Normal() {
        emailService.sendVerificationCode("19302010050@fudan.edu.cn");
    }

    @Test
    public void verificationCodeValidate1Normal() {
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 6; i ++)
            code.append(new Random().nextInt(10));

        String content = "【HardLibrary】您正在进行注册验证，验证码 " + code + " ，有效时长为 3 分钟，请勿泄露给他人，若非本人操作请忽略。";

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        String to = "19302010050@fudan.edu.cn";
        message.setTo(to);
        message.setSubject("HardLibrary 注册验证");
        message.setText(content);
        mailSender.send(message);
        verificationCodeRepository.save(new Verification_code(to, code.toString(), dateUtil.toDateString(new Date(), "yyyy-MM-dd HH:mm:ss")));

        emailService.verificationCodeValidate(to, code.toString());
    }

    @Test
    public void verificationCodeValidate2Overtime() {
        assertThrows(BadRequestException.class, () -> emailService.verificationCodeValidate("19302010050@fudan.edu.cn", "825187"));
    }

    @Test
    public void verificationCodeValidate3Null() {
        assertThrows(BadRequestException.class, () -> emailService.verificationCodeValidate("", "825187"));
        assertThrows(BadRequestException.class, () -> emailService.verificationCodeValidate("19302010050@fudan.edu.cn", ""));
    }



    @Test
    public void sendAlertTest() {
        emailService.sendAlert();
    }

    @Test
    public void repositoryTest() {
        System.out.println(verificationCodeRepository.findFirstByEmailOrderByIdDesc("19302010050@fudan.edu.cn"));
    }


}