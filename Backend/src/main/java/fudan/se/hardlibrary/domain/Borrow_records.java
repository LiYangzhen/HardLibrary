package fudan.se.hardlibrary.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
public class Borrow_records {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String isbn;
    private String borrower;
    private String reserveTime;
    private String borrowTime;
    private String returnTime;
    private String borrow_admin;
    private String return_admin;
    private String borrow_location;
    private String return_location;
    private String status;
    private Float fine;
    private Integer credit;

    public Borrow_records() {

    }

    public Borrow_records(String isbn, String borrower, String status) {
        this.isbn = isbn;
        this.borrower = borrower;
        this.status = status;
    }


}
