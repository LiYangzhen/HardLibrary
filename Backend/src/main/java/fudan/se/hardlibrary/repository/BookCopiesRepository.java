package fudan.se.hardlibrary.repository;

import fudan.se.hardlibrary.domain.Book_copies;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookCopiesRepository extends JpaRepository<Book_copies, String > {

    @Query(value = "select * from book_copies where isbn regexp ?1 and isbn like ?2", nativeQuery = true)
    List<Book_copies> findAllByIsbnMatchesAndLike(String regexp, String isbn);

    Book_copies findByIsbn(String isbn);

    List<Book_copies> findAllByBorrowerAndStatus(String borrower, String status);

}
