package fudan.se.hardlibrary.repository;

import fudan.se.hardlibrary.domain.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, String> {

    Book findByIsbn(String isbn);

    List<Book> findAllByNameLike(String name);

    List<Book> findAllByIsbnLike(String isbn);

    List<Book> findAllByAuthorLike(String author);

}
