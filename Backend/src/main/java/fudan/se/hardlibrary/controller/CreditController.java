package fudan.se.hardlibrary.controller;

import fudan.se.hardlibrary.service.CreditService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class CreditController {

    @Resource
    CreditService creditService;

    @GetMapping("/getCredit/{username}")
    public ResponseEntity<?> getCredit(@PathVariable String username) {
        return ResponseEntity.ok(creditService.getCredit(username));
    }

    @PreAuthorize("hasAuthority('Reader')")
    @GetMapping("/resetCredit/{username}")
    public ResponseEntity<?> resetCredit(@PathVariable String username) {
        return ResponseEntity.ok(creditService.resetCredit(username));
    }

    @GetMapping("/getResetRecords/{username}")
    public ResponseEntity<?> getResetRecords(@PathVariable String username) {
        return ResponseEntity.ok(creditService.getResetRecords(username));
    }
}
