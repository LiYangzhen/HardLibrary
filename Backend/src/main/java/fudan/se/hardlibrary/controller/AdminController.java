package fudan.se.hardlibrary.controller;

import fudan.se.hardlibrary.service.AuthorityService;
import fudan.se.hardlibrary.service.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
public class AdminController {

    @Resource
    AuthorityService authorityService;

    @Resource
    EmailService emailService;

    @PreAuthorize("hasAuthority('Admin')")
    @PostMapping("/setMaxCopies")
    public ResponseEntity<?> setMaxCopies(@RequestParam("maxCopies") Integer maxCopies,
                                          @RequestParam("authority") String authority) {
        return ResponseEntity.ok(authorityService.setMaxCopies(maxCopies, authority));
    }

    @PreAuthorize("hasAuthority('Admin')")
    @PostMapping("/setMaxReserveTime")
    public ResponseEntity<?> setMaxReserveTime(@RequestParam("maxReserveTime") Integer maxReserveTime,
                                          @RequestParam("authority") String authority) {
        return ResponseEntity.ok(authorityService.setMaxReserveTime(maxReserveTime, authority));
    }

    @PreAuthorize("hasAuthority('Admin')")
    @PostMapping("/setMaxBorrowTime")
    public ResponseEntity<?> setMaxBorrowTime(@RequestParam("maxBorrowTime") Integer maxBorrowTime,
                                               @RequestParam("authority") String authority) {
        return ResponseEntity.ok(authorityService.setMaxBorrowTime(maxBorrowTime, authority));
    }


    @PreAuthorize("hasAuthority('Admin')")
    @GetMapping("/sendAlert")
    public ResponseEntity<?> sendAlert() {
        return ResponseEntity.ok(emailService.sendAlert());
    }

}
