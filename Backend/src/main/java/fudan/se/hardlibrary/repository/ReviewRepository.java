package fudan.se.hardlibrary.repository;

import fudan.se.hardlibrary.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    Review findByIsbnAndUsername(String isbn, String username);

    List<Review> findAllByIsbnOrderByIdDesc(String isbn);

}
