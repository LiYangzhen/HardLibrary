package fudan.se.hardlibrary.controller;

import fudan.se.hardlibrary.controller.request.BorrowRequest;
import fudan.se.hardlibrary.service.AuthorityService;
import fudan.se.hardlibrary.service.BorrowService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
public class BorrowController {

    @Resource
    private BorrowService borrowService;

    @Resource
    private AuthorityService authorityService;

    @PreAuthorize("hasAuthority('Librarian')")
    @PostMapping("/borrowBook")
    public ResponseEntity<?> borrowBook(@RequestBody BorrowRequest borrowRequest) {
        return ResponseEntity.ok(borrowService.borrowBook(
                borrowRequest.getLocation(), borrowRequest.getAdmin(),
                borrowRequest.getUsername(), borrowRequest.getCopiesIsbn()));
    }

    @PreAuthorize("hasAuthority('Librarian')")
    @PostMapping("/returnBook")
    public ResponseEntity<?> returnBook(@RequestBody BorrowRequest borrowRequest) {
        return ResponseEntity.ok(borrowService.returnBook(
                borrowRequest.getLocation(), borrowRequest.getAdmin(), borrowRequest.getCopiesIsbn(), borrowRequest.getIfDamaged(), borrowRequest.getIfLost()));
    }

    @PreAuthorize("hasAuthority('Reader')")
    @PostMapping("/reserveBook")
    public ResponseEntity<?> reserveBook(@RequestParam("username") String username,
                                         @RequestParam("isbn") String isbn) {
        return ResponseEntity.ok(borrowService.reserveBook(username, isbn));
    }

    @PreAuthorize("hasAuthority('Librarian')")
    @PostMapping("/takeReservedBook")
    public ResponseEntity<?> takeReservedBook(@RequestBody BorrowRequest borrowRequest) {
        return ResponseEntity.ok(borrowService.takeReservedBook(
                borrowRequest.getLocation(), borrowRequest.getAdmin(),
                borrowRequest.getUsername(), borrowRequest.getCopiesIsbn()));
    }

    @PreAuthorize("hasAuthority('Librarian')")
    @GetMapping("/getReservedBook/{username}")
    public ResponseEntity<?> getReservedBook(@PathVariable String username) {
        return ResponseEntity.ok(borrowService.getReservedBook(username));
    }

    @GetMapping("/getUserRecords/{username}")
    public ResponseEntity<?> getUserRecords(@PathVariable String username) {
        return ResponseEntity.ok(borrowService.getUserRecords(username));
    }

    @PreAuthorize("hasAuthority('Librarian')")
    @GetMapping("/getCopyHistory/{isbn}")
    public ResponseEntity<?> getCopyHistory(@PathVariable String isbn) {
        return ResponseEntity.ok(borrowService.getCopyHistory(isbn));
    }

    @GetMapping("/getMaxCopies/{authority}")
    public ResponseEntity<?> getMaxCopies(@PathVariable String authority) {
        return ResponseEntity.ok(authorityService.getMaxCopies(authority));
    }

    @GetMapping("/getMaxReserveTime/{authority}")
    public ResponseEntity<?> getMaxReserveTime(@PathVariable String authority) {
        return ResponseEntity.ok(authorityService.getMaxReserveTime(authority));
    }

    @GetMapping("/getMaxBorrowTime/{authority}")
    public ResponseEntity<?> getMaxBorrowTime(@PathVariable String authority) {
        return ResponseEntity.ok(authorityService.getMaxBorrowTime(authority));
    }


}
