package fudan.se.hardlibrary.utils;

import fudan.se.hardlibrary.domain.Book_copies;
import fudan.se.hardlibrary.domain.Borrow_records;
import fudan.se.hardlibrary.domain.User;
import fudan.se.hardlibrary.exception.BadRequestException;
import fudan.se.hardlibrary.repository.BorrowRecordsRepository;
import fudan.se.hardlibrary.service.BookService;
import fudan.se.hardlibrary.service.JwtUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Util class for validating everything
 *
 * Possible exception thrown by this class：
 * @NotFoundException: http status: 404, reason: isbn not found.
 * @BadRequestException: http status: 400, reason: register information pattern wrong, authority insufficient, borrow number more than 5 and so on.
 */
@Component
public class ValidateUtil {

    JwtUserDetailsService jwtUserDetailsService;
    BorrowRecordsRepository borrowRecordsRepository;
    BookService bookService;

    @Autowired
    public ValidateUtil(JwtUserDetailsService jwtUserDetailsService, BorrowRecordsRepository borrowRecordsRepository, BookService bookService) {
        this.jwtUserDetailsService = jwtUserDetailsService;
        this.borrowRecordsRepository = borrowRecordsRepository;
        this.bookService = bookService;
    }

    /**
     * validate username pattern when registering, just for reader
     */
    public void usernameValidate(String username) {
        if (!Pattern.matches("^[0-9]{11}$", username))
            throw new BadRequestException("Username pattern wrong");
    }

    /**
     * validate password pattern when registering
     */
    public void passwordValidate(String username, String password) {
        boolean allNum = Pattern.matches("^[0-9]{6,32}$", password);
        boolean allLetter = Pattern.matches("^[a-zA-Z]{6,32}$", password);
        boolean allSpecial = Pattern.matches("^[-_]{6,32}$", password);
        if (!Pattern.matches("^[a-zA-Z0-9-_]{6,32}$", password)) {
            throw new BadRequestException("Password pattern wrong");
        } else if (allNum || allLetter || allSpecial) {
            throw new BadRequestException("Password all same type");
        } else if (password.contains(username)) {
            throw new BadRequestException("Password contains username");
        }
    }

    /**
     * validate email pattern when registering, just for reader
     */
    public void emailValidate(String email) {
        if (!Pattern.matches("^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$", email))
            throw new BadRequestException("Email pattern wrong");
    }

    /**
     * validate whether user has some authority
     *
     * @param username      throw exception when username not found
     * @param authority     authority string, format: "authority1, authority2....", as long as the user has one of them, it won't throw exception
     */
    public void authorityValidate(String username, String authority) {
        UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(username);
        for (GrantedAuthority auth: userDetails.getAuthorities())
            if (authority.contains(auth.getAuthority()))
                return;
        throw new BadRequestException(username + " has no authority");
    }

    /**
     * validate whether user's borrow count will exceed the limit: 5, if true, throw exception
     *
     * @param borrowNumber  the book count that user will borrow
     */
    public void borrowLimitValidate(String username, int borrowNumber) {
        User user = (User) jwtUserDetailsService.loadUserByUsername(username);
        if (borrowNumber < 0)
            throw new BadRequestException("Negative borrow number");
        List<Borrow_records> userRecords = borrowRecordsRepository.findAllByBorrowerAndStatus(username, "进行中");
        if (userRecords.size() + borrowNumber > user.getMaxCopies())
            throw new BadRequestException("User:" + username +"'s number of borrowings has reached the limit");
    }

    /**
     * validate the book copy's status and location
     *
     * @param isbn          copy's isbn, throw exception when isbn not found
     * @param status        excepted copy's status
     * @param location      excepted copy's location
     * @return              return Book_copies when verified, if not, throw exception
     */
    public Book_copies copyStatusValidate(String isbn, String status, String location) {
        Book_copies book_copies = bookService.findCopyByIsbn(isbn);
        if (!book_copies.getStatus().equals(status))
            throw new BadRequestException("Wrong status for book: " + isbn);
        if (!book_copies.getLocation().equals(location))
            throw new BadRequestException("The book " + isbn + " is not in the current library");
        return book_copies;
    }

    /**
     * validate whether user's credit is less than some value
     */
    public void creditValidate(User user, int needCredit) {
        if (user.getCredit() < needCredit)
            throw new BadRequestException("信用分过低，无法进行该操作");
    }
}
