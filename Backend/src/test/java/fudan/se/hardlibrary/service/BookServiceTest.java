package fudan.se.hardlibrary.service;

import fudan.se.hardlibrary.TestConfig;

import fudan.se.hardlibrary.domain.Book_copies;
import fudan.se.hardlibrary.exception.BadRequestException;
import fudan.se.hardlibrary.exception.NotFoundException;
import fudan.se.hardlibrary.repository.BookRepository;
import org.junit.FixMethodOrder;
import org.junit.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

public class BookServiceTest extends TestConfig {

    @Autowired
    BookService bookService;

    @Autowired
    BookRepository bookRepository;

    @Test
    public void findBooks1() {
        assertNotNull(bookService.findBooks(0, 8));
    }

    @Test
    public void findBooks2() {
        assertNotNull(bookService.findBooks(999, 8));
    }

    @Test
    public void findBooks3() {
        assertThrows(BadRequestException.class, () -> bookService.findBooks(-1, 8));
    }

    @Test
    public void findBooks4() {
        assertThrows(BadRequestException.class, () -> bookService.findBooks(0, -1));
    }


    @Test
    public void findByIsbn1() throws IOException {
        if (bookRepository.findByIsbn("123-155") == null)
            bookService.upload("123-155", "bookname", "bookauthor", "bookintroduction", new Date(), (float) 0.3, null);
        bookService.addCopies("123-155", 1, "测试图书馆");
        assertNotNull(bookService.findByIsbn("123-155"));
    }

    @Test
    public void findByIsbn2() {
        assertThrows(NotFoundException.class, () -> bookService.findByIsbn(""));
    }

    @Test
    public void findByIsbn3() {
        assertThrows(NotFoundException.class, () -> bookService.findByIsbn("9876543210"));
    }

    @Test
    public void findByIsbn4() {
        assertThrows(NotFoundException.class, () -> bookService.findByIsbn("??//$$##%%&&"));
    }


    @Test
    public void searchByIsbn1() {
        assertNotNull(bookService.searchByIsbn("123-155").get(0));
        assertNotNull(bookService.searchByIsbn("123").get(0));
        assertNotNull(bookService.searchByIsbn("15").get(0));
    }

    @Test
    public void searchByIsbn2() {
        assertThrows(BadRequestException.class, () -> bookService.searchByIsbn(""));
    }


    @Test
    public void searchByName1() {
        assertNotNull(bookService.searchByName("Java").get(0));
        assertNotNull(bookService.searchByName("ava").get(0));
        assertNotNull(bookService.searchByName("a").get(0));
    }

    @Test
    public void searchByName2() {
        assertThrows(BadRequestException.class, () -> bookService.searchByName(""));
    }


    @Test
    public void searchByAuthor1() {
        assertNotNull(bookService.searchByAuthor("uce").get(0));
        assertNotNull(bookService.searchByAuthor("uc").get(0));
        assertNotNull(bookService.searchByAuthor("u").get(0));
    }

    @Test
    public void searchByAuthor2() {
        assertThrows(BadRequestException.class, () -> bookService.searchByAuthor(""));
    }


    @Test
    public void findCopiesByIsbn1() throws IOException {
        if (bookRepository.findByIsbn("123-155") == null)
            bookService.upload("123-155", "bookname", "bookauthor", "bookintroduction", new Date(), (float) 0.3, null);
        bookService.addCopies("123-155", 1, "测试图书馆");
        assertNotNull(bookService.findCopiesByIsbn("123-155").get(0));
    }

    @Test
    public void findCopiesByIsbn2() {
        assertEquals(0, bookService.findCopiesByIsbn("").size());
    }

    @Test
    public void findCopiesByIsbn3() {
        assertEquals(0, bookService.findCopiesByIsbn("123-155-001").size());
    }

    @Test
    public void findCopiesByIsbn4() {
        assertEquals(0, bookService.findCopiesByIsbn("223-334").size());
    }

    @Test
    public void findCopiesByIsbn5() {
        assertEquals(0, bookService.findCopiesByIsbn("ds&^*%(*Jl;").size());
    }

    @Test
    public void findCopiesByIsbn6() {
        assertEquals(0, bookService.findCopiesByIsbn("123").size());
    }


    @Test
    public void findCopyByIsbn1() throws IOException {
        if (bookRepository.findByIsbn("123-155") == null) {
            bookService.upload("123-155", "bookname", "bookauthor", "bookintroduction", new Date(), (float) 0.3, null);
            bookService.addCopies("123-155", 1, "测试图书馆");
        }
        assertNotNull(bookService.findCopyByIsbn("123-155-001"));
    }

    @Test
    public void findCopyByIsbn2() {
        assertThrows(NotFoundException.class, () -> bookService.findCopyByIsbn(""));
    }

    @Test
    public void findCopyByIsbn3() {
        assertThrows(NotFoundException.class, () -> bookService.findCopyByIsbn("233-334"));
    }

    @Test
    public void findCopyByIsbn4() {
        assertThrows(NotFoundException.class, () -> bookService.findCopyByIsbn("123-155"));
    }

    @Test
    public void findCopyByIsbn5() {
        assertThrows(NotFoundException.class, () -> bookService.findCopyByIsbn("asfwce1q!@#!#?>!"));
    }


    @Test
    public void upload() throws IOException {
        long a = System.currentTimeMillis() % 1000;
        Map<String, String> map = bookService.upload("100-" + a, "bookname", "bookauthor", "bookintroduction", new Date(), (float) 0.1, null);
        assertEquals("success", map.get("message"));
        assertThrows(BadRequestException.class, () -> bookService.upload("100-" + a, "bookname", "bookauthor", "bookintroduction", new Date(), (float) 0.2, null));
        bookRepository.deleteById("100-" + a);
    }

    @Test
    public void update1() throws IOException {
        long a = System.currentTimeMillis() % 1000;
        bookService.upload("200-" + a, "bookname", "bookauthor", "bookintroduction", new Date(), (float) 99.8, null);
        Map<String, String> map = bookService.update("200-" + a, "booname", "booauthor", "boointroduction", new Date(12345), (float) 99.8, null);
        assertEquals("success", map.get("message"));
        bookRepository.deleteById("200-" + a);
    }
    @Test
    public void update2() {
        assertThrows(NotFoundException.class, () -> bookService.update("233-334", "bookname", "bookauthor", "bookintroduction", new Date(), (float) 99.8, null));
    }
    @Test
    public void update3() {
        assertThrows(NotFoundException.class, () -> bookService.update("", "", "", "", new Date(), (float) 99.8, null));
    }


    @Test
    public void delete1() throws IOException {
        long a = System.currentTimeMillis() % 1000;
        bookService.upload("300-" + a, "bookname", "bookauthor", "bookintroduction", new Date(), (float) 99.8, null);
        Map<String, String> map = bookService.delete("300-" + a);
        assertEquals("success", map.get("message"));
    }

    @Test
    public void delete2() {
        assertThrows(NotFoundException.class, () -> bookService.delete(""));
    }

    @Test
    public void delete3() {
        assertThrows(NotFoundException.class, () -> bookService.delete("96877"));
    }


    @Test
    public void addCopies() throws IOException {
        long a = System.currentTimeMillis() % 1000;
        bookService.upload("400-" + a, "bookname", "bookauthor", "bookintroduction", new Date(), (float) 0.3, null);
        Map<String, String> map = bookService.addCopies("400-" + a, 2, "测试图书馆");
        assertEquals("success", map.get("message"));
        assertThrows(NotFoundException.class, () -> bookService.addCopies("", 2, "测试图书馆"));
        assertThrows(NotFoundException.class, () -> bookService.addCopies("??!!..22asq", 2, "测试图书馆"));
        assertThrows(BadRequestException.class, () -> bookService.addCopies("400-" + a, 1001, "测试图书馆"));
        assertThrows(BadRequestException.class, () -> bookService.addCopies("400-" + a, 0, "测试图书馆"));
        assertThrows(BadRequestException.class, () -> bookService.addCopies("400-" + a, -1, "测试图书馆"));
        assertThrows(BadRequestException.class, () -> bookService.addCopies("400-" + a, -13, "测试图书馆"));
        bookService.delete("400-" + a);
    }


    @Test
    public void updateCopy1() {
        bookService.addCopies("123-155", 1, "测试图书馆");
        Map<String, String> map = bookService.updateCopy("123-155-001", "测试图书馆", false);
        assertEquals("success", map.get("message"));
        bookService.updateCopy("123-155-001", "张江图书馆", false);
    }

    @Test
    public void updateCopy2() {
        assertThrows(BadRequestException.class, () -> bookService.updateCopy("123-155-001", "", false));
    }

    @Test
    public void updateCopy3() {
        bookService.updateCopy("123-155-001", "测试图书馆", true);
        Book_copies book_copies = bookService.findCopyByIsbn("123-155-001");
        assertEquals("已遗失", book_copies.getStatus());
        assertNull(book_copies.getLocation());
        bookService.updateCopy("123-155-001", "张江图书馆", false);
        book_copies = bookService.findCopyByIsbn("123-155-001");
        assertEquals("可借阅", book_copies.getStatus());
    }


    @Test
    public void deleteCopy() throws IOException {
        long a = System.currentTimeMillis() % 1000;
        bookService.upload("500-" + a, "bookname", "bookauthor", "bookintroduction", new Date(), (float) 99.8, null);
        bookService.addCopies("500-" + a, 1, "测试图书馆");
        Map<String, String> map = bookService.deleteCopy("500-" + a + "-001");
        assertEquals("success", map.get("message"));
        assertThrows(NotFoundException.class, () -> bookService.deleteCopy("500-" + a + "-002"));
        bookService.delete("500-" + a);
    }

}