package fudan.se.hardlibrary.utils;

import fudan.se.hardlibrary.TestConfig;
import fudan.se.hardlibrary.exception.BadRequestException;
import fudan.se.hardlibrary.exception.NotFoundException;
import fudan.se.hardlibrary.service.BookService;
import org.junit.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class ValidateUtilTest extends TestConfig {

    @Autowired
    ValidateUtil validateUtil;

    @Autowired
    BookService bookService;

    @Test
    public void usernameValidate1Normal() {
        validateUtil.usernameValidate("12345678901");
    }

    @Test
    public void usernameValidate2Null() {
        assertThrows(BadRequestException.class, () -> validateUtil.usernameValidate(""));
    }

    @Test
    public void usernameValidate3WrongPatternI() {
        assertThrows(BadRequestException.class, () -> validateUtil.usernameValidate("thisis11lng"));
    }

    @Test
    public void usernameValidate3WrongPatternII() {
        assertThrows(BadRequestException.class, () -> validateUtil.usernameValidate("1234567890"));
    }


    @Test
    public void passwordValidate1Normal() {
        validateUtil.passwordValidate("username1", "paSs-wd_123");
    }

    @Test
    public void passwordValidate2NullUsername() {
        assertThrows(BadRequestException.class, () -> validateUtil.passwordValidate("", "passwd123"));
    }

    @Test
    public void passwordValidate2NullPasswd() {
        assertThrows(BadRequestException.class, () -> validateUtil.passwordValidate("username1", ""));
    }

    @Test
    public void passwordValidate3AllNumbers() {
        assertThrows(BadRequestException.class, () -> validateUtil.passwordValidate("username1", "123456"));
    }

    @Test
    public void passwordValidate4AllLetters() {
        assertThrows(BadRequestException.class, () -> validateUtil.passwordValidate("username1", "aBCdef"));
    }

    @Test
    public void passwordValidate5AllSpecial() {
        assertThrows(BadRequestException.class, () -> validateUtil.passwordValidate("username1", "_-_-_-"));
    }

    @Test
    public void passwordValidate6CharsUnmentioned() {
        assertThrows(BadRequestException.class, () -> validateUtil.passwordValidate("username1", "!@#$%^&*()_/?"));
    }

    @Test
    public void passwordValidate7TooShort() {
        assertThrows(BadRequestException.class, () -> validateUtil.passwordValidate("username1", "ab23-"));
    }

    @Test
    public void passwordValidate8TooLong() {
        assertThrows(BadRequestException.class, () -> validateUtil.passwordValidate("username1", "aaaaa11111bbbbb22222-----cc_c_c_c"));
    }

    @Test
    public void passwordValidate9ContainsUsername() {
        assertThrows(BadRequestException.class, () -> validateUtil.passwordValidate("username1", "passUusername1word"));
    }


    @Test
    public void emailValidate() {
        // Dispensable now as email auto-syncs with username
    }


    @Test
    public void authorityValidate1Normal() {
        validateUtil.authorityValidate("admin", "Admin, Librarian");
    }

    @Test
    public void authorityValidate2NoUser() {
        assertThrows(UsernameNotFoundException.class, () -> validateUtil.authorityValidate("nousername", "Student"));
    }

    @Test
    public void authorityValidate3NoAuth() {
        assertThrows(BadRequestException.class, () -> validateUtil.authorityValidate("admin", "Teacher, Student"));
    }

    @Test
    public void borrowLimitValidate1Normal() {
        validateUtil.borrowLimitValidate("19302010050", 1);
    }

    @Test
    public void borrowLimitValidate2TooMuch() {
        assertThrows(BadRequestException.class, () -> validateUtil.borrowLimitValidate("19302010050", 6));
    }

    @Test
    public void borrowLimitValidate3Negative() {
        assertThrows(Exception.class, () -> validateUtil.borrowLimitValidate("19302010050", -1));
    }

    @Test
    public void copyStatusValidate1Normal() {
        System.out.println(bookService.findCopyByIsbn("123-155-002"));
        validateUtil.copyStatusValidate("123-155-002", "可借阅", "江湾图书馆");
    }

    @Test
    public void copyStatusValidate2Null() {
        assertThrows(NotFoundException.class, () -> validateUtil.copyStatusValidate("", "", ""));
    }

    @Test
    public void copyStatusValidate3WrongISBN() {
        assertThrows(NotFoundException.class, () -> validateUtil.copyStatusValidate("666-666", "在库", "张江图书馆"));
    }

    @Test
    public void copyStatusValidate4WrongStatus() {
        assertThrows(BadRequestException.class, () -> validateUtil.copyStatusValidate("123-155-002", "不在库", "张江图书馆"));
    }

    @Test
    public void copyStatusValidate5WrongLocation() {
        assertThrows(BadRequestException.class, () -> validateUtil.copyStatusValidate("123-155-002", "在库", "生姜图书馆"));
    }

}