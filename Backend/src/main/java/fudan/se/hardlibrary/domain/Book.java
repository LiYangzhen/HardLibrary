package fudan.se.hardlibrary.domain;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Entity;

@Entity
@Data
public class Book {

    @Id
    private String isbn;

    private String name;
    private String author;
    private String introduction;
    private String date;
    private Integer count;
    private Float price;
    private Float rating;
    private Double rating_sum;
    private Integer rating_count;
    private byte[] image;

    public Book() {
    }

    public Book(String isbn, String name, String author, String introduction, String date, Float price) {
        this.isbn = isbn;
        this.name = name;
        this.author = author;
        this.introduction = introduction;
        this.date = date;
        this.count = 0;
        this.rating = 0f;
        this.rating_sum = (double) 0;
        this.rating_count = 0;
        this.price = price;
    }


}
