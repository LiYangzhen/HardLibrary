package fudan.se.hardlibrary.controller;

import fudan.se.hardlibrary.service.AuthService;
import fudan.se.hardlibrary.controller.request.RegisterRequest;
import fudan.se.hardlibrary.service.EmailService;
import fudan.se.hardlibrary.utils.ValidateUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;


@RestController
public class AuthController {

    @Resource
    private AuthService authService;

    @Resource
    private EmailService emailService;

    @Resource
    private ValidateUtil validateUtil;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        validateUtil.usernameValidate(request.getUsername());
        validateUtil.passwordValidate(request.getUsername(), request.getPassword());
        validateUtil.emailValidate(request.getEmail());
        emailService.verificationCodeValidate(request.getEmail(), request.getVerificationCode());

        return ResponseEntity.ok(authService.register(request));
    }

    @PreAuthorize("hasAuthority('Admin')")
    @PostMapping("/adminRegister")
    public ResponseEntity<?> adminRegister(@RequestBody RegisterRequest request) {
        validateUtil.passwordValidate(request.getUsername(), request.getPassword());
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam("username") String username,
                                   @RequestParam("password") String password) {

        return ResponseEntity.ok(authService.login(username, password));
    }

    @PostMapping("/changePassword")
    public ResponseEntity<?> changePass(@RequestParam("username") String username,
                                        @RequestParam("oldPassword") String oldPass,
                                        @RequestParam("newPassword") String newPass) {
        validateUtil.passwordValidate(username, newPass);
        return ResponseEntity.ok(authService.changePass(username, oldPass, newPass));
    }

    @PostMapping("/sendVerificationCode")
    public ResponseEntity<?> sendVerificationCode(@RequestParam("email") String email) {
        validateUtil.emailValidate(email);
        return ResponseEntity.ok(emailService.sendVerificationCode(email));
    }

    @GetMapping("/payFine/{username}")
    public ResponseEntity<?> payFine(@PathVariable String username) {
        return ResponseEntity.ok(authService.payFine(username));
    }

    @GetMapping("/getFine/{username}")
    public ResponseEntity<?> getFine(@PathVariable String username) {
        return ResponseEntity.ok(authService.getFine(username));
    }
}



