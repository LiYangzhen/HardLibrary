package fudan.se.hardlibrary.repository;

import fudan.se.hardlibrary.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByReviewIdOrderByIdDesc(Long id);

    List<Comment> findAllByUsernameOrderByIdDesc(String username);

    @Transactional
    void deleteAllByReviewId(Long id);

}
