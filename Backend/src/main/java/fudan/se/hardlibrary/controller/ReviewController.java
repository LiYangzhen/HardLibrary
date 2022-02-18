package fudan.se.hardlibrary.controller;

import fudan.se.hardlibrary.service.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
public class ReviewController {

    @Resource
    ReviewService reviewService;

    @PreAuthorize("hasAuthority('Reader')")
    @PostMapping("/publishReview")
    public ResponseEntity<?> publishReview(@RequestParam("isbn") String isbn,
                                           @RequestParam("username") String username,
                                           @RequestParam("review") String review,
                                           @RequestParam("rating") Float rating) {
        return ResponseEntity.ok(reviewService.publishReview(isbn, username, review, rating));
    }

    @PreAuthorize("hasAuthority('Reader')")
    @PostMapping("/publishComment")
    public ResponseEntity<?> publishComment(@RequestParam("reviewID") Long id,
                                           @RequestParam("username") String username,
                                           @RequestParam("comment") String comment) {
        return ResponseEntity.ok(reviewService.publishComment(username, comment, id));
    }

    @GetMapping("/getReviewDetails/{reviewID}")
    public ResponseEntity<?> getReviewDetails(@PathVariable("reviewID") Long id) {
        return ResponseEntity.ok(reviewService.getReviewDetails(id));
    }

    @GetMapping("/getUserReviews/{username}")
    public ResponseEntity<?> getReviewDetails(@PathVariable String username) {
        return ResponseEntity.ok(reviewService.getUserReviews(username));
    }

    @GetMapping("/getUserComments/{username}")
    public ResponseEntity<?> getUserComments(@PathVariable String username) {
        return ResponseEntity.ok(reviewService.getUserComments(username));
    }

    @GetMapping("/getBookReviews/{isbn}")
    public ResponseEntity<?> getBookReviews(@PathVariable String isbn) {
        return ResponseEntity.ok(reviewService.getBookReviews(isbn));
    }

    @PreAuthorize("hasAuthority('Reader')")
    @DeleteMapping("/deleteReview")
    public ResponseEntity<?> deleteReview(@RequestParam("reviewID") Long id,
                                          @RequestParam("username") String username) {
        return ResponseEntity.ok(reviewService.deleteReview(id, username));
    }

    @PreAuthorize("hasAuthority('Reader')")
    @DeleteMapping("/deleteComment")
    public ResponseEntity<?> deleteComment(@RequestParam("commentID") Long id,
                                          @RequestParam("username") String username) {
        return ResponseEntity.ok(reviewService.deleteComment(id, username));
    }

    @PreAuthorize("hasAuthority('Librarian')")
    @GetMapping("/hideReview/{reviewID}")
    public ResponseEntity<?> hideReview(@PathVariable("reviewID") Long id) {
        return ResponseEntity.ok(reviewService.hideReview(id));
    }

    @PreAuthorize("hasAuthority('Librarian')")
    @GetMapping("/hideComment/{commentID}")
    public ResponseEntity<?> hideComment(@PathVariable("commentID") Long id) {
        return ResponseEntity.ok(reviewService.hideComment(id));
    }

    @PreAuthorize("hasAuthority('Librarian')")
    @GetMapping("/showReview/{reviewID}")
    public ResponseEntity<?> showReview(@PathVariable("reviewID") Long id) {
        return ResponseEntity.ok(reviewService.showReview(id));
    }

    @PreAuthorize("hasAuthority('Librarian')")
    @GetMapping("/showComment/{commentID}")
    public ResponseEntity<?> showComment(@PathVariable("commentID") Long id) {
        return ResponseEntity.ok(reviewService.showComment(id));
    }


}
