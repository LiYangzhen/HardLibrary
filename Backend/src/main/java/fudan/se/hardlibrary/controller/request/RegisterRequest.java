package fudan.se.hardlibrary.controller.request;

import lombok.Data;

import java.util.Set;

/**
 * @author LBW
 */

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private String email;
    private String verificationCode;
    private Set<String> authorities;

    public RegisterRequest() {}

    public RegisterRequest(String username, String password, String email, String verificationCode, Set<String> authorities) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.verificationCode = verificationCode;
        this.authorities = authorities;
    }

}

