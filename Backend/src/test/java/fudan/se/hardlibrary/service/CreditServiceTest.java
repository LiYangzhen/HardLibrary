package fudan.se.hardlibrary.service;

import fudan.se.hardlibrary.TestConfig;
import fudan.se.hardlibrary.domain.User;
import fudan.se.hardlibrary.repository.UserRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

public class CreditServiceTest extends TestConfig {

    @Autowired
    CreditService creditService;

    @Autowired
    UserRepository userRepository;

    @Test
    public void getCredit1() {
        assertTrue(creditService.getCredit("19302010050") <= 100);
        assertTrue(creditService.getCredit("19302010050") >= 0);
    }

    @Test
    public void getCredit2() {
        assertThrows(Exception.class, () -> creditService.getCredit("admin"));
        assertThrows(Exception.class, () -> creditService.getCredit("nouser"));
    }

//    @Test
//    public void resetCredit() {
//        User user = userRepository.findByUsername("19302010050");
//        if (user.getCredit() == 100)
//            user.setCredit(90);
//        if (user.getFine() == 0)
//            user.setFine(15f);
//        userRepository.save(user);
//        assertThrows(Exception.class, () -> creditService.resetCredit("19302010050"));
//        user.setFine(0f);
//        userRepository.save(user);
//        creditService.resetCredit("19302010050");
//        assertEquals(100, userRepository.findByUsername("19302010050").getCredit());
//        assertThrows(Exception.class, () -> creditService.resetCredit("19302010050"));
//    }

    @Test
    public void getResetRecords() {
        assertTrue(creditService.getResetRecords("19302010050").size() > 0);
        assertThrows(Exception.class, () -> creditService.getResetRecords("nouser"));
    }
}