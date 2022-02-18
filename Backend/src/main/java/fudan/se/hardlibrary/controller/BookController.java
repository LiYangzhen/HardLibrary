package fudan.se.hardlibrary.controller;

import fudan.se.hardlibrary.domain.Book;
import fudan.se.hardlibrary.domain.Book_copies;
import fudan.se.hardlibrary.service.BookService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@RestController
public class BookController {

    @Resource
    BookService bookService;

    @GetMapping("/findBooks/{page}/{size}")
    public Page<Book> findBooks(@PathVariable("page") Integer page, @PathVariable("size") Integer size) {
        return bookService.findBooks(page, size);
    }

    @GetMapping("/findBookByIsbn/{isbn}")
    public Book findByIsbn(@PathVariable String isbn) {
        return bookService.findByIsbn(isbn);
    }

    @GetMapping("/searchByName/{name}")
    public List<Book> searchByName(@PathVariable String name) {
        return bookService.searchByName(name);
    }

    @GetMapping("/searchByAuthor/{author}")
    public List<Book> searchByAuthor(@PathVariable String author) {
        return bookService.searchByAuthor(author);
    }

    @GetMapping("/searchByIsbn/{isbn}")
    public List<Book> searchByIsbn(@PathVariable String isbn) {
        return bookService.searchByIsbn(isbn);
    }

    @GetMapping("/findCopiesByIsbn/{isbn}")
    public List<Book_copies> findCopiesByIsbn(@PathVariable String isbn) {
        return bookService.findCopiesByIsbn(isbn);
    }

    @PreAuthorize("hasAuthority('Librarian')")
    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam("isbn") String isbn,
                                    @RequestParam("name") String name,
                                    @RequestParam("author") String author,
                                    @RequestParam("introduction") String introduction,
                                    @RequestParam("date") Date date,
                                    @RequestParam("price") Float price,
                                    @RequestParam(value = "image", required = false) MultipartFile image) throws IOException {
        return ResponseEntity.ok(bookService.upload(isbn, name, author, introduction, date, price, image));
    }

    @PreAuthorize("hasAuthority('Librarian')")
    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestParam("isbn") String isbn,
                                    @RequestParam("name") String name,
                                    @RequestParam("author") String author,
                                    @RequestParam("introduction") String introduction,
                                    @RequestParam("date") Date date,
                                    @RequestParam("price") Float price,
                                    @RequestParam(value = "image", required = false) MultipartFile image) throws IOException {
        return ResponseEntity.ok(bookService.update(isbn, name, author, introduction, date, price, image));
    }

    @PreAuthorize("hasAuthority('Librarian')")
    @DeleteMapping("/delete/{isbn}")
    public ResponseEntity<?> delete(@PathVariable String isbn) {
        return ResponseEntity.ok(bookService.delete(isbn));
    }

    @PreAuthorize("hasAuthority('Librarian')")
    @PostMapping("/addCopies")
    public ResponseEntity<?> addCopies(@RequestParam("isbn") String isbn,
                                       @RequestParam("count") Integer count,
                                       @RequestParam("location") String location) {
        return ResponseEntity.ok(bookService.addCopies(isbn, count, location));
    }

    @PreAuthorize("hasAuthority('Librarian')")
    @PostMapping("/updateCopy")
    public ResponseEntity<?> updateCopy(@RequestParam("isbn") String isbn,
                                        @RequestParam("location") String location,
                                        @RequestParam("ifLost") Boolean ifLost) {
        return ResponseEntity.ok(bookService.updateCopy(isbn, location, ifLost));
    }

    @PreAuthorize("hasAuthority('Librarian')")
    @DeleteMapping("/deleteCopy/{isbn}")
    public ResponseEntity<?> deleteCopy(@PathVariable String isbn) {
        return ResponseEntity.ok(bookService.deleteCopy(isbn));
    }
}
