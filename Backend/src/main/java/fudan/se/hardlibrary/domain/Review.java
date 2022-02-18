package fudan.se.hardlibrary.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String isbn;
    private String username;
    private String review;
    private Float rating;
    private String time;
    private Boolean delete_flag;
    private Boolean hide_flag;

    public Review() {

    }

    public Review(String isbn, String username, String review, Float rating, String time) {
        this.isbn = isbn;
        this.username = username;
        this.review = review;
        this.rating = rating;
        this.time = time;
        this.delete_flag = false;
        this.hide_flag = false;
    }


}
