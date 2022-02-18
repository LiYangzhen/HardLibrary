package fudan.se.hardlibrary.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
public class Verification_code {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String code;
    private String time;

    public Verification_code() {}

    public Verification_code(String email, String code, String time) {
        this.email = email;
        this.code = code;
        this.time = time;
    }

}
