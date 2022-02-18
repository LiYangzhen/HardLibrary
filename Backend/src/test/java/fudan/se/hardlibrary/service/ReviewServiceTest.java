package fudan.se.hardlibrary.service;

import fudan.se.hardlibrary.TestConfig;
import fudan.se.hardlibrary.domain.Comment;
import fudan.se.hardlibrary.domain.Review;
import fudan.se.hardlibrary.repository.BorrowRecordsRepository;
import fudan.se.hardlibrary.repository.CommentRepository;
import fudan.se.hardlibrary.repository.ReviewRepository;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ReviewServiceTest extends TestConfig {

    @Autowired
    ReviewService reviewService;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    BorrowRecordsRepository borrowRecordsRepository;

    @Autowired
    BookService bookService;

    @Autowired
    ReviewRepository reviewRepository;

    @Test
    public void publishReview0() {
        assertThrows(Exception.class, () -> reviewService.publishReview("123-155", "19302010050", "111", 5.5f));
        reviewService.publishReview("123-155", "19302010050", "111", 0.5f);
        assertEquals(0.5f, bookService.findByIsbn("123-155").getRating());
    }

    @Test
    public void publishReview1() {
        assertThrows(Exception.class, () -> reviewService.publishReview("123-155", "nouser", "111", 0.5f));
    }

    @Test
    public void publishReview2() {
        assertThrows(Exception.class, () -> reviewService.publishReview("123-155", "19302010050", "111", 0.5f));
    }

    @Test
    public void publishReview3() {
        assertThrows(Exception.class, () -> reviewService.publishReview("516165156", "19302010050", "111", 0.5f));
    }

    @Test
    public void publishReview4() {
        assertThrows(Exception.class, () -> reviewService.publishReview("978-7-666-66666-6", "19302010050", "111", 0.5f));
        Review review = reviewRepository.findByIsbnAndUsername("123-155", "19302010050");
        reviewService.hideReview(review.getId());
        reviewRepository.delete(review);
    }

    @Test
    public void getReviewDetails() {
        Map map = reviewService.getReviewDetails(16);
        assertNotNull(map.get("comments"));
        assertNotNull(map.get("status"));
    }

    @Test
    public void test1() {
        reviewService.publishReview("123-155", "19302010050", "111", 0.5f);
        Review review = reviewRepository.findByIsbnAndUsername("123-155", "19302010050");
        reviewService.publishComment("19302010050", "test1", review.getId());
        reviewService.hideReview(review.getId());
        assertThrows(Exception.class, () -> reviewService.publishComment("19302010050", "test2", review.getId()));
        assertThrows(Exception.class, () -> reviewService.publishComment("admin", "test3", review.getId()));
        assertThrows(Exception.class, () -> reviewService.publishComment("19302010050", "test4", review.getId() + 100));
        reviewService.showReview(review.getId());
        reviewService.publishComment("19302010050", "test5", review.getId());
    }

    @Test
    public void test2() {
        List<Map> map = reviewService.getBookReviews("123-155");
        assertEquals(1, map.size());
        assertNotNull(map.get(0).get("review"));
        assertNotNull(map.get(0).get("comments"));
        assertThrows(Exception.class, () -> reviewService.getBookReviews("123-144"));
    }

    @Test
    public void test3() {
        List<Map> mapList = reviewService.getUserComments("19302010050");
        assertFalse(mapList.isEmpty());
        assertNotNull(mapList.get(0).get("status"));
        assertNotNull(mapList.get(0).get("comment"));
        assertThrows(Exception.class, () -> reviewService.getUserComments("admin"));
    }

    @Test
    public void test4() {
        List<Map> mapList = reviewService.getUserReviews("19302010050");
        assertFalse(mapList.isEmpty());
        assertNotNull(mapList.get(0).get("reviewStatus"));
        assertThrows(Exception.class, () -> reviewService.getUserComments("admin"));
    }

    @Test
    public void test5() {
        Review review = reviewRepository.findByIsbnAndUsername("123-155", "19302010050");
        reviewService.hideReview(review.getId());
        assertThrows(Exception.class, () -> reviewService.hideReview(review.getId()));
        assertEquals(0, bookService.findByIsbn("123-155").getRating());
        reviewService.showReview(review.getId());
        assertEquals(0.5f, bookService.findByIsbn("123-155").getRating());
        assertThrows(Exception.class, () -> reviewService.showReview(review.getId()));
        reviewService.hideReview(review.getId());
        assertThrows(Exception.class, () -> reviewService.deleteReview(review.getId(), "19302010017"));
        reviewService.deleteReview(review.getId(), "19302010050");
        assertEquals(0, bookService.findByIsbn("123-155").getRating_count());
        assertThrows(Exception.class, () -> reviewService.deleteReview(review.getId(), "19302010050"));
        assertThrows(Exception.class, () -> reviewService.showReview(review.getId()));
        assertThrows(Exception.class, () -> reviewService.hideReview(review.getId()));
    }

    @Test
    public void test6() {
        Review review = reviewRepository.findByIsbnAndUsername("123-155", "19302010050");
        Comment comment = commentRepository.findAllByReviewIdOrderByIdDesc(review.getId()).get(0);
        reviewService.hideComment(comment.getId());
        assertThrows(Exception.class, () -> reviewService.hideComment(comment.getId()));
        reviewService.showComment(comment.getId());
        assertFalse(commentRepository.findAllByReviewIdOrderByIdDesc(review.getId()).get(0).getHide_flag());
        assertThrows(Exception.class, () -> reviewService.showComment(comment.getId()));
        reviewService.hideComment(comment.getId());
        assertThrows(Exception.class, () -> reviewService.deleteComment(comment.getId(), "19302010017"));
        reviewService.deleteComment(comment.getId(), "19302010050");
        assertThrows(Exception.class, () -> reviewService.deleteComment(comment.getId(), "19302010050"));
        assertThrows(Exception.class, () -> reviewService.showComment(comment.getId()));
        assertThrows(Exception.class, () -> reviewService.hideComment(comment.getId()));
        reviewRepository.delete(review);
        commentRepository.deleteAllByReviewId(review.getId());
    }

}