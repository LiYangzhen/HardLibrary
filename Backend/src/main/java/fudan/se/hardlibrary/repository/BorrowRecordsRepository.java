package fudan.se.hardlibrary.repository;

import fudan.se.hardlibrary.domain.Borrow_records;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface BorrowRecordsRepository extends JpaRepository<Borrow_records, Long> {

    Borrow_records findByStatusAndIsbn(String status, String isbn);

    Borrow_records findByBorrowerAndStatusAndIsbn(String borrower, String status, String isbn);

    List<Borrow_records> findAllByBorrower(String borrower);

    List<Borrow_records> findAllByIsbn(String isbn);

    List<Borrow_records> findAllByBorrowerAndStatus(String borrower, String status);

    List<Borrow_records> findAllByBorrowerAndStatusAndBorrowTime(String borrower, String status, String borrowTime);

    List<Borrow_records> findAllByIsbnLikeAndBorrowerAndReturnTimeNotNull(String isbn, String borrower);

    List<Borrow_records> findAllByBorrowerAndReturnTimeNotNullOrderByReturnTimeDesc(String borrower);

    @Transactional
    void deleteAllByIsbn(String isbn);

}
