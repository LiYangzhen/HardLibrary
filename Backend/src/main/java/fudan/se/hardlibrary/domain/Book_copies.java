package fudan.se.hardlibrary.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
public class Book_copies {
    @Id
    private String isbn;
    private String status;
    private String location;
    private String borrower;
    private String time;
    private Integer time_limit;

    public Book_copies() {

    }

    public Book_copies(String isbn, String status, String location) {
        this.isbn = isbn;
        this.status = status;
        this.location = location;
    }

}
