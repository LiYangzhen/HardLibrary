package fudan.se.hardlibrary.service;

import fudan.se.hardlibrary.domain.Book;
import fudan.se.hardlibrary.domain.Borrow_records;
import fudan.se.hardlibrary.domain.Comment;
import fudan.se.hardlibrary.domain.Review;
import fudan.se.hardlibrary.exception.BadRequestException;
import fudan.se.hardlibrary.exception.NotFoundException;
import fudan.se.hardlibrary.repository.BookRepository;
import fudan.se.hardlibrary.repository.BorrowRecordsRepository;
import fudan.se.hardlibrary.repository.CommentRepository;
import fudan.se.hardlibrary.repository.ReviewRepository;
import fudan.se.hardlibrary.utils.DateUtil;
import fudan.se.hardlibrary.utils.MapUtil;
import fudan.se.hardlibrary.utils.ValidateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class ReviewService {

    MapUtil mapUtil;
    BookService bookService;
    ValidateUtil validateUtil;
    ReviewRepository reviewRepository;
    CommentRepository commentRepository;
    DateUtil dateUtil;

    @Resource
    BorrowRecordsRepository borrowRecordsRepository;

    @Resource
    BookRepository bookRepository;

    static String format = "yyyy-MM-dd HH:mm:ss";

    @Autowired
    public ReviewService(MapUtil mapUtil, BookService bookService, ValidateUtil validateUtil, ReviewRepository reviewRepository, CommentRepository commentRepository, DateUtil dateUtil) {
        this.mapUtil = mapUtil;
        this.bookService = bookService;
        this.validateUtil = validateUtil;
        this.reviewRepository = reviewRepository;
        this.commentRepository = commentRepository;
        this.dateUtil = dateUtil;
    }

    /**
     * 发表书评，每本书只能发表一次
     */
    public Map<String, String> publishReview(String isbn, String username, String review, float rating) {
        validateUtil.authorityValidate(username, "Reader");
        if (!isBorrowed(username, isbn))
            throw new BadRequestException("You can only review after borrowing and returning this book");
        if (reviewRepository.findByIsbnAndUsername(isbn, username) != null)
            throw new BadRequestException("You have reviewed before");
        if (rating < 0 || rating > 5)
            throw new BadRequestException("Rating out of range: 0-5");

        Review newReview = new Review(isbn, username, review, rating, dateUtil.toDateString(new Date(), format));
        changeRating(newReview, 1);
        reviewRepository.save(newReview);
        return mapUtil.getMessageMap("success");
    }

    /**
     * 发表评价的回复
     */
    public Map<String, String> publishComment(String username, String comment, Long id) {
        validateUtil.authorityValidate(username, "Reader");
        Review review = getReview(id);
        reviewValidate(review);

        commentRepository.save(new Comment(id, username, comment, dateUtil.toDateString(new Date(), format)));
        return mapUtil.getMessageMap("success");
    }

    /**
     * 获取评论详情，返回有review, comments, status(已删除，已隐藏，已评价)
     */
    public Map<String, Object> getReviewDetails(long id) {
        Review review = getReview(id);
        return getReviewDetails(review);
    }

    /**
     * 获取书评
     */
    public List<Map> getBookReviews(String isbn) {
        bookService.findByIsbn(isbn);
        List<Review> reviews = reviewRepository.findAllByIsbnOrderByIdDesc(isbn);
        List<Map> mapList = new ArrayList<>();
        for (Review review: reviews)
            mapList.add(getReviewDetails(review));
        return mapList;
    }

    /**
     * 获取用户的回复状态
     */
    public List<Map> getUserComments(String username) {
        validateUtil.authorityValidate(username, "Reader");
        List<Comment> comments = commentRepository.findAllByUsernameOrderByIdDesc(username);
        List<Map> maps = new ArrayList<>();
        for (Comment comment: comments) {
            Review review = getReview(comment.getReviewId());
            Map<String, Object> map = new HashMap<>();
            addBookInfo(map, review.getIsbn());
            map.put("review", review);
            map.put("comment", comment);
            String status;
            if (comment.getDelete_flag())
                status = "已删除";
            else if (comment.getHide_flag())
                status = "已隐藏";
            else
                status = "已评价";
            map.put("status", status);
            maps.add(map);
        }
        return maps;
    }

    /**
     * 获取用户评价状态，包括未评价的书
     */
    public List<Map> getUserReviews(String username) {
        validateUtil.authorityValidate(username, "Reader");
        List<Borrow_records> records = borrowRecordsRepository.findAllByBorrowerAndReturnTimeNotNullOrderByReturnTimeDesc(username);
        List<Map> maps = new ArrayList<>();
        for (Borrow_records record: records) {
            boolean isExisted = false;
            String recordIsbn = record.getIsbn().substring(0, record.getIsbn().length() - 4);
            for (Map map: maps) {
                if (map.get("isbn").equals(recordIsbn)) {
                    map.put("returnTime", record.getReturnTime());
                    isExisted = true;
                    break;
                }
            }
            if (!isExisted) {
                Review review = reviewRepository.findByIsbnAndUsername(recordIsbn, username);
                String status = getReviewStatus(review);
                Map<String, Object> map = new HashMap<>();
                addBookInfo(map, recordIsbn);
                map.put("returnTime", record.getReturnTime());
                map.put("reviewStatus", status);
                map.put("review", review);
                maps.add(map);
            }
        }
        return maps;
    }

    /**
     * 删除评价，不可恢复
     */
    public Map<String, String> deleteReview(Long id, String username) {
        Review review = getReview(id);
        if (!review.getUsername().equals(username))
            throw new BadRequestException("Review can only be deleted by publisher");
        if (review.getDelete_flag())
            throw new BadRequestException("This review has been deleted");

        review.setDelete_flag(true);
        reviewRepository.save(review);
        if (!review.getHide_flag())
            changeRating(review, -1);
        return mapUtil.getMessageMap("success");
    }

    /**
     * 删除回复，不可恢复
     */
    public Map<String, String> deleteComment(Long id, String username) {
        Comment comment = getComment(id);
        if (!comment.getUsername().equals(username))
            throw new BadRequestException("Comment can only be deleted by publisher");
        if (comment.getDelete_flag())
            throw new BadRequestException("This comment has been deleted");

        comment.setDelete_flag(true);
        commentRepository.save(comment);
        return mapUtil.getMessageMap("success");
    }

    /**
     * 管理员隐藏评论
     */
    public Map<String, String> hideReview(Long id) {
        Review review = getReview(id);
        reviewValidate(review);
        review.setHide_flag(true);
        reviewRepository.save(review);
        changeRating(review, -1);
        return mapUtil.getMessageMap("success");
    }

    /**
     * 管理员隐藏回复
     */
    public Map<String, String> hideComment(Long id) {
        Comment comment = getComment(id);
        commentValidate(comment);
        comment.setHide_flag(true);
        commentRepository.save(comment);
        return mapUtil.getMessageMap("success");
    }

    /**
     * 管理员取消隐藏评论
     */
    public Map<String, String> showReview(Long id) {
        Review review = getReview(id);
        if (!review.getHide_flag())
            throw new BadRequestException("This review has not been hidden");
        if (review.getDelete_flag())
            throw new BadRequestException("This review has been deleted");

        review.setHide_flag(false);
        reviewRepository.save(review);
        changeRating(review, 1);
        return mapUtil.getMessageMap("success");
    }

    /**
     * 管理员取消隐藏回复
     */
    public Map<String, String> showComment(Long id) {
        Comment comment = getComment(id);
        if (!comment.getHide_flag())
            throw new BadRequestException("This comment has not been hidden");
        if (comment.getDelete_flag())
            throw new BadRequestException("This comment has been deleted");

        comment.setHide_flag(false);
        commentRepository.save(comment);
        return mapUtil.getMessageMap("success");
    }

    /**
     * 内部方法，包装评论详情为map
     */
    private Map<String, Object> getReviewDetails(Review review) {
        List<Comment> comments = commentRepository.findAllByReviewIdOrderByIdDesc(review.getId());
        Map<String, Object> map = new HashMap<>();
        map.put("review", review);
        map.put("comments", comments);
        map.put("status", getReviewStatus(review));
        return map;
    }

    /**
     * 内部方法，获取评论状态
     */
    private String getReviewStatus(Review review) {
        String status;
        if (review == null)
            status = "待评价";
        else if (review.getDelete_flag())
            status = "已删除";
        else if (review.getHide_flag())
            status = "已隐藏";
        else
            status = "已评价";

        return status;
    }

    /**
     * 内部方法，查看用户是否借过某本书
     */
    private boolean isBorrowed(String username, String isbn) {
        if (borrowRecordsRepository.findAllByIsbnLikeAndBorrowerAndReturnTimeNotNull(isbn + "%", username).size() == 0)
            return false;
        return true;
    }

    /**
     * 内部方法，带异常处理的根据id获取评论方法
     */
    private Review getReview(Long id) {
        Optional<Review> reviewOptional = reviewRepository.findById(id);
        if (!reviewOptional.isPresent())
            throw new NotFoundException("Review id not found");
        return reviewOptional.get();
    }

    /**
     * 内部方法，带异常处理的根据id获取回复方法
     */
    private Comment getComment(Long id) {
        Optional<Comment> commentOptional = commentRepository.findById(id);
        if (!commentOptional.isPresent())
            throw new NotFoundException("Comment id not found");
        return commentOptional.get();
    }

    /**
     * 内部方法，改变书的评分
     */
    private void changeRating(Review review, int inc) {
        Book book = bookService.findByIsbn(review.getIsbn());
        book.setRating_sum(book.getRating_sum() + inc * review.getRating());
        book.setRating_count(book.getRating_count()  + inc);
        float rating = (float) ((int)(10 * book.getRating_sum() / book.getRating_count()) / 10.0);
        book.setRating(rating);
        bookRepository.save(book);
    }

    /**
     * 内部方法，验证评论没被删除或者隐藏
     */
    private void reviewValidate(Review review) {
        if (review.getDelete_flag() || review.getHide_flag())
            throw new BadRequestException("This review has been deleted or hidden");
    }

    /**
     * 内部方法，验证回复没被删除或者隐藏
     */
    private void commentValidate(Comment comment) {
        if (comment.getDelete_flag() || comment.getHide_flag())
            throw new BadRequestException("This comment has been deleted or hidden");
    }

    /**
     * 内部方法，向map里添加book信息
     */
    private void addBookInfo(Map map, String isbn) {
        Book book = bookService.findByIsbn(isbn);
        map.put("isbn", book.getIsbn());
        map.put("name", book.getName());
        map.put("author", book.getAuthor());
    }
}
