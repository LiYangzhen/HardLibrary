package fudan.se.hardlibrary.service;

import fudan.se.hardlibrary.TestConfig;

import fudan.se.hardlibrary.domain.Book_copies;
import fudan.se.hardlibrary.exception.BadRequestException;
import fudan.se.hardlibrary.exception.NotFoundException;
import org.junit.FixMethodOrder;
import org.junit.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BorrowServiceTest extends TestConfig {

    @Autowired
    BorrowService borrowService;

    @Autowired
    BookService bookService;

    @Test
    public void test1() {
        borrowService.reserveBook("19302010050", "123-155-001");
        Book_copies book_copies = bookService.findCopyByIsbn("123-155-001");
        assertEquals("已预约", book_copies.getStatus());
        assertThrows(BadRequestException.class, () -> borrowService.reserveBook("19302010050", "123-155-001"));
        assertThrows(UsernameNotFoundException.class, () -> borrowService.reserveBook("nouser", "123-155-001"));
        assertThrows(BadRequestException.class, () -> borrowService.reserveBook("admin", "123-155-001"));
        borrowService.reserveBook("19302010050", "123-155-002");
    }

    @Test
    public void test2() {
        assertThrows(NotFoundException.class, () -> borrowService.takeReservedBook("张江图书馆", "admin", "19302010013", new String[]{"123-155-001", "123-155-002"}));
        assertThrows(BadRequestException.class, () -> borrowService.takeReservedBook("错误图书馆", "admin", "19302010050", new String[]{"123-155-001", "123-155-002"}));
        assertThrows(BadRequestException.class, () -> borrowService.takeReservedBook("张江图书馆", "admin", "19302010050", new String[]{"123-155-001", "123-155-003"}));
        borrowService.takeReservedBook("张江图书馆", "admin", "19302010050", new String[]{"123-155-001", "123-155-002"});
        Book_copies book_copies = bookService.findCopyByIsbn("123-155-001");
        assertEquals("已借阅", book_copies.getStatus());
        assertEquals("19302010050", book_copies.getBorrower());
        assertNull(book_copies.getLocation());
    }

    @Test
    public void test3() {
        borrowService.borrowBook("张江图书馆", "admin", "19302010050", new String[]{"123-155-003", "123-155-004"});
        assertThrows(BadRequestException.class, () -> borrowService.borrowBook("错误图书馆", "admin", "19302010050", new String[]{"123-155-005"}));
        assertThrows(BadRequestException.class, () -> borrowService.borrowBook("张江图书馆", "admin", "19302010050", new String[]{"123-155-001"}));
        Book_copies book_copies = bookService.findCopyByIsbn("123-155-003");
        assertEquals("已借阅", book_copies.getStatus());
        assertEquals("19302010050", book_copies.getBorrower());
        assertNull(book_copies.getLocation());
        borrowService.borrowBook("张江图书馆", "admin", "19302010050", new String[]{"123-155-005"});
        assertThrows(BadRequestException.class, () -> borrowService.borrowBook("张江图书馆", "admin", "19302010050", new String[]{"123-155-006"}));
        assertThrows(BadRequestException.class, () -> borrowService.reserveBook("19302010050","123-155-006"));
    }

    @Test
    public void test4() {
       borrowService.returnBook("江湾图书馆", "admin", new String[]{"123-155-001", "123-155-002"}, new Boolean[]{false, false}, new Boolean[]{false, false});
       assertEquals("江湾图书馆", bookService.findCopyByIsbn("123-155-001").getLocation());
       assertEquals("可借阅", bookService.findCopyByIsbn("123-155-002").getStatus());
       assertThrows(BadRequestException.class, () -> borrowService.returnBook("张江图书馆", "admin", new String[]{"123-155-001"}, new Boolean[]{false}, new Boolean[]{false}));
       borrowService.reserveBook("19302010050", "123-155-001");
    }

    @Test
    public void test5() {
        assertEquals(1, borrowService.getReservedBook("19302010050").size());
        assertThrows(UsernameNotFoundException.class, () -> borrowService.getReservedBook("").size());
    }

    @Test
    public void test6() {
        borrowService.getCopyHistory("123-155-001");
        assertThrows(NotFoundException.class, () -> borrowService.getCopyHistory("981-293-095"));
    }

    @Test
    public void test7() {
        Book_copies bc = new Book_copies(null, null, null);
        bc.setTime("2021-05-17 20:00:00");
        bc.setTime_limit(86400);
        assertTrue(borrowService.ifOverdue(bc));
    }

    @Test
    public void setNullAndStatusTest() {
        Book_copies bc = new Book_copies(null, null, null);
        bc.setBorrower("不是我");
        bc.setTime("真的不是我");
        bc.setTime_limit(666);
        borrowService.setStatusAndNull(bc, "牛");
        assertEquals("牛", bc.getStatus());
        assertNull(bc.getBorrower());
        assertNull(bc.getTime());
        assertNull(bc.getTime_limit());
    }


}