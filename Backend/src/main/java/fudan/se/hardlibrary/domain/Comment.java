package fudan.se.hardlibrary.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long reviewId;
    private String username;
    private String comment;
    private String time;
    private Boolean hide_flag;
    private Boolean delete_flag;

    public Comment() {

    }

    public Comment(Long review_id, String username, String comment, String time) {
        this.reviewId = review_id;
        this.username = username;
        this.comment = comment;
        this.time = time;
        this.hide_flag = false;
        this.delete_flag = false;
    }
}
