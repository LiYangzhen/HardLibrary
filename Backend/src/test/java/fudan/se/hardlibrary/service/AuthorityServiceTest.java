package fudan.se.hardlibrary.service;

import fudan.se.hardlibrary.TestConfig;
import fudan.se.hardlibrary.domain.*;
import fudan.se.hardlibrary.exception.BadRequestException;
import fudan.se.hardlibrary.exception.NotFoundException;
import fudan.se.hardlibrary.repository.*;
import org.junit.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class AuthorityServiceTest extends TestConfig {

    @Resource
    AuthorityService authorityService;

    @Resource
    AuthorityRepository authorityRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    BookService bookService;

    @Autowired
    BookRepository bookRepository;


    @Test
    public void setMaxCopies1Normal(){
        Authority graduate = authorityRepository.findByAuthority("Undergraduate");
        Authority postgraduate = authorityRepository.findByAuthority("Postgraduate");
        Authority teacher = authorityRepository.findByAuthority("Teacher");
        Integer oldG = graduate.getMaxCopies();
        Integer oldP = postgraduate.getMaxCopies();
        Integer oldT = teacher.getMaxCopies();
        graduate.setMaxCopies(0);
        postgraduate.setMaxCopies(10);
        teacher.setMaxCopies(19287318);
        assertEquals(0, graduate.getMaxCopies());
        assertEquals(10, postgraduate.getMaxCopies());
        assertEquals(19287318, teacher.getMaxCopies());
        graduate.setMaxCopies(oldG);
        postgraduate.setMaxCopies(oldP);
        teacher.setMaxCopies(oldT);
    }

    @Test
    public void setMaxCopies2NegativeCopies(){
        Authority graduate = authorityRepository.findByAuthority("Undergraduate");
        Integer oldG = graduate.getMaxCopies();
        assertThrows(BadRequestException.class, () -> authorityService.setMaxCopies(-1, "Undergraduate"));
        graduate.setMaxCopies(oldG);
    }

    @Test
    public void setMaxCopies3NullAuth(){
        assertThrows(NotFoundException.class, () -> authorityService.setMaxCopies(5, ""));
    }

    @Test
    public void setMaxCopies4FalseAuth(){
        assertThrows(NotFoundException.class, () -> authorityService.setMaxCopies(5, " "));
        assertThrows(NotFoundException.class, () -> authorityService.setMaxCopies(5, "Librari"));
    }


    @Test
    public void setMaxReserveTime1Normal(){
        Authority graduate = authorityRepository.findByAuthority("Undergraduate");
        Authority postgraduate = authorityRepository.findByAuthority("Postgraduate");
        Authority teacher = authorityRepository.findByAuthority("Teacher");
        Integer oldG = graduate.getMaxReserveTime();
        Integer oldP = postgraduate.getMaxReserveTime();
        Integer oldT = teacher.getMaxReserveTime();
        graduate.setMaxReserveTime(0);
        postgraduate.setMaxReserveTime(10);
        teacher.setMaxReserveTime(19287318);
        assertEquals(0, graduate.getMaxReserveTime());
        assertEquals(10, postgraduate.getMaxReserveTime());
        assertEquals(19287318, teacher.getMaxReserveTime());
        graduate.setMaxReserveTime(oldG);
        postgraduate.setMaxReserveTime(oldP);
        teacher.setMaxReserveTime(oldT);
    }

    @Test
    public void setMaxReserveTime2NegativeTime(){
        Authority graduate = authorityRepository.findByAuthority("Undergraduate");
        Integer oldG = graduate.getMaxReserveTime();
        assertThrows(BadRequestException.class, () -> authorityService.setMaxReserveTime(-1, "Undergraduate"));
        graduate.setMaxReserveTime(oldG);
    }

    @Test
    public void setMaxReserveTime3NullAuth(){
        assertThrows(NotFoundException.class, () -> authorityService.setMaxReserveTime(86400, ""));
    }

    @Test
    public void setMaxReserveTime4FalseAuth(){
        assertThrows(NotFoundException.class, () -> authorityService.setMaxReserveTime(86400, " "));
        assertThrows(NotFoundException.class, () -> authorityService.setMaxReserveTime(86400, "Libra"));
    }


    @Test
    public void setMaxBorrowTime1Normal(){
        Authority graduate = authorityRepository.findByAuthority("Undergraduate");
        Authority postgraduate = authorityRepository.findByAuthority("Postgraduate");
        Authority teacher = authorityRepository.findByAuthority("Teacher");
        Integer oldG = graduate.getMaxBorrowTime();
        Integer oldP = postgraduate.getMaxBorrowTime();
        Integer oldT = teacher.getMaxBorrowTime();
        graduate.setMaxBorrowTime(0);
        postgraduate.setMaxBorrowTime(10);
        teacher.setMaxBorrowTime(19287318);
        assertEquals(0, graduate.getMaxBorrowTime());
        assertEquals(10, postgraduate.getMaxBorrowTime());
        assertEquals(19287318, teacher.getMaxBorrowTime());
        graduate.setMaxBorrowTime(oldG);
        postgraduate.setMaxBorrowTime(oldP);
        teacher.setMaxBorrowTime(oldT);
    }

    @Test
    public void setMaxBorrowTime2NegativeTime(){
        Authority graduate = authorityRepository.findByAuthority("Undergraduate");
        Integer oldG = graduate.getMaxBorrowTime();
        assertThrows(BadRequestException.class, () -> authorityService.setMaxBorrowTime(-1, "Undergraduate"));
        graduate.setMaxBorrowTime(oldG);
    }

    @Test
    public void setMaxBorrowTime3NullAuth(){
        assertThrows(NotFoundException.class, () -> authorityService.setMaxBorrowTime(86400, ""));
    }

    @Test
    public void setMaxBorrowTime4FalseAuth(){
        assertThrows(NotFoundException.class, () -> authorityService.setMaxBorrowTime(86400, " "));
        assertThrows(NotFoundException.class, () -> authorityService.setMaxBorrowTime(86400, "Librari"));
    }

    @Test
    public void getMaxCopies() {
        assertNull(authorityService.getMaxCopies("Admin"));
        assertNotNull(authorityService.getMaxCopies("Postgraduate"));
        assertThrows(NotFoundException.class, () -> authorityService.getMaxCopies("121"));
    }

    @Test
    public void getMaxBorrowTime() {
        assertNull(authorityService.getMaxBorrowTime("Admin"));
        assertNotNull(authorityService.getMaxBorrowTime("Postgraduate"));
        assertThrows(NotFoundException.class, () -> authorityService.getMaxBorrowTime("121"));
    }

    @Test
    public void getMaxReserveTime() {
        assertNull(authorityService.getMaxReserveTime("Admin"));
        assertNotNull(authorityService.getMaxReserveTime("Postgraduate"));
        assertThrows(NotFoundException.class, () -> authorityService.getMaxReserveTime("121"));
    }

    @Test
    public void init() throws IOException {
        if (bookRepository.findByIsbn("123-155") == null) {
            bookService.upload("123-155", "test", "ada", "aaa", null, 12F, null);
            bookService.addCopies("123-155", 6, "张江图书馆");
        }
        if (userRepository.findByUsername("19302010050") == null) {
            Set<Authority> authorities = new HashSet<>();
            authorities.add(authorityRepository.findByAuthority("Student"));
            authorities.add(authorityRepository.findByAuthority("Reader"));
            userRepository.save(new User("19302010050", "fudan123", "19302010050@fudan.edu.cn", authorities));
        }
        if (userRepository.findByUsername("19302010017") == null) {
            Set<Authority> authorities = new HashSet<>();
            authorities.add(authorityRepository.findByAuthority("Student"));
            authorities.add(authorityRepository.findByAuthority("Reader"));
            userRepository.save(new User("19302010017", "test123", "19302010017@fudan.edu.cn", authorities));
        }
    }

}
