package fudan.se.hardlibrary.service;

import fudan.se.hardlibrary.TestConfig;

import fudan.se.hardlibrary.controller.request.RegisterRequest;
import fudan.se.hardlibrary.domain.Authority;
import fudan.se.hardlibrary.domain.User;
import fudan.se.hardlibrary.exception.BadRequestException;
import fudan.se.hardlibrary.repository.AuthorityRepository;
import fudan.se.hardlibrary.repository.BookRepository;
import fudan.se.hardlibrary.repository.UserRepository;
import org.junit.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AuthServiceTest extends TestConfig {

    @Autowired
    AuthService authService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    BookService bookService;

    @Autowired
    BookRepository bookRepository;

    @Autowired
    AuthorityRepository authorityRepository;

    @Test
    public void register1Normal() {
        Set<String> set = new HashSet<>();
        set.add("Librarian");
        String username = "a" + System.currentTimeMillis() % 10000;
        User user = authService.register(new RegisterRequest(username, "b", "c", "123232", set));
        assertNotNull(user);
        userRepository.delete(user);
    }

    @Test
    public void register2NullAuthority() {
        String username = "b" + System.currentTimeMillis() % 10000;
        assertThrows(BadRequestException.class, () -> authService.register(new RegisterRequest(username, "", "", "1312", null)));
    }

    @Test
    public void register3DuplicateUsername() {
        Set<String> set = new HashSet<>();
        set.add("Librarian");
        assertThrows(BadRequestException.class, () -> authService.register(new RegisterRequest("admin", "b", "c", "2131", set)));
    }

    @Test
    public void login1Normal() {
        Map<String, String> map = authService.login("admin", "qwe123");
        assertNotNull(map);
        assertNotNull(map.get("token"));
        assertEquals("admin", map.get("username"));
        assertNotNull(map.get("authorities"));
    }

    @Test
    public void login2Null() {
        assertThrows(UsernameNotFoundException.class, () -> authService.login("", ""));
    }

    @Test
    public void login3NoUser() {
        assertThrows(UsernameNotFoundException.class, () -> authService.login("notauser", "password123"));
    }

    @Test
    public void login4WrongPWD() {
        assertThrows(BadRequestException.class, () -> authService.login("admin", "notapassword"));
    }

    @Test
    public void changePass1Normal() {
        Map<String, String> map = authService.changePass("admin", "qwe123", "123");
        assertEquals("success", map.get("message"));
        authService.changePass("admin", "123", "qwe123");
    }

    @Test
    public void changePass2WrongOld() {
        assertThrows(BadRequestException.class, () -> authService.changePass("admin", "notadmin", "123"));
    }

    @Test
    public void changePass3SameNew() {
        assertThrows(BadRequestException.class, () -> authService.changePass("admin", "qwe123", "qwe123"));
    }

    @Test
    public void payFineTest() {
        assertThrows(UsernameNotFoundException.class, () -> authService.payFine("nouser"));
    }

    @Test
    public void getFineTest() {
        assertTrue(authService.getFine("19302010050") > -1);
        assertThrows(UsernameNotFoundException.class, () -> authService.getFine("nouser"));
    }
}