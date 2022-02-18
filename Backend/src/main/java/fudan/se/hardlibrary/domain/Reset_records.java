package fudan.se.hardlibrary.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
public class Reset_records {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String time;
    private Boolean pass;

    public Reset_records() {

    }

    public Reset_records(String username, String time, Boolean pass) {
        this.username = username;
        this.time = time;
        this.pass = pass;
    }
}
