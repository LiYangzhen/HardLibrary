package fudan.se.hardlibrary.service;

import fudan.se.hardlibrary.domain.*;
import fudan.se.hardlibrary.exception.BadRequestException;
import fudan.se.hardlibrary.exception.NotFoundException;
import fudan.se.hardlibrary.repository.BookCopiesRepository;
import fudan.se.hardlibrary.repository.BorrowRecordsRepository;
import fudan.se.hardlibrary.repository.UserRepository;
import fudan.se.hardlibrary.utils.DateUtil;
import fudan.se.hardlibrary.utils.MapUtil;
import fudan.se.hardlibrary.utils.ValidateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * Class for kinds of borrow and return operation
 *
 * Possible exception thrown by this class：
 * @NotFoundException: http status: 404, reason: isbn not found or username not found.
 * @BadRequestException: http status: 400, reason: authority insufficient, copy's status wrong and so on.
 */
@Service
public class BorrowService {

    private BorrowRecordsRepository borrowRecordsRepository;
    private BookCopiesRepository bookCopiesRepository;
    private UserRepository userRepository;
    private BookService bookService;
    private ValidateUtil validateUtil;

    @Resource
    private DateUtil dateUtil;

    @Resource
    private MapUtil mapUtil;

    @Resource
    private JwtUserDetailsService jwtUserDetailsService;

    static String format = "yyyy-MM-dd HH:mm:ss";

    @Autowired
    public BorrowService(BorrowRecordsRepository borrowRecordsRepository, BookCopiesRepository bookCopiesRepository, UserRepository userRepository, BookService bookService, ValidateUtil validateUtil) {
        this.borrowRecordsRepository = borrowRecordsRepository;
        this.bookCopiesRepository = bookCopiesRepository;
        this.userRepository = userRepository;
        this.bookService = bookService;
        this.validateUtil = validateUtil;
    }

    /**
     * 用户预约图书方法
     *
     * @param username      预约人username，不存在时抛异常
     * @param isbn          副本isbn，不存在时抛异常
     * @return              成功时返回 {"message":"success"} 失败时抛异常
     */
    public Map<String, String> reserveBook(String username, String isbn) {
        validateUtil.authorityValidate(username, "Student, Teacher");
        validateUtil.borrowLimitValidate(username, 1);
        User user = (User) jwtUserDetailsService.loadUserByUsername(username);
        validateUtil.creditValidate(user, 50);

        Book_copies book_copies = bookService.findCopyByIsbn(isbn);
        if (!book_copies.getStatus().equals("可借阅"))
            throw new BadRequestException("Wrong status for book: " + isbn);

        String date = dateUtil.toDateString(new Date(), format);
        Borrow_records borrow_records = new Borrow_records(isbn, username, "进行中");
        borrow_records.setReserveTime(date);
        borrowRecordsRepository.save(borrow_records);

        book_copies.setStatus("已预约");
        book_copies.setBorrower(username);
        book_copies.setTime(date);
        book_copies.setTime_limit(user.getMaxReserveTime());
        bookCopiesRepository.save(book_copies);

        return mapUtil.getMessageMap("success");
    }

    /**
     * 用户拿走预约图书的方法（可一次拿走多个），当预约过期不能成功取书
     *
     * @param location      管理员上班位置，需要与所要拿走图书的位置一致
     * @param admin         操作管理员，不会对管理员是否存在进行检测（一个标记而已）
     * @param username      借阅人username
     * @param copiesIsbn    多个副本的isbn，状态必须是已预约
     * @return              成功时返回 {"message":"success"} 失败时抛异常
     */
    public Map<String, String> takeReservedBook(String location, String admin, String username, String[] copiesIsbn) {
        validateUtil.authorityValidate(username, "Student, Teacher");
        User user = (User) jwtUserDetailsService.loadUserByUsername(username);
        validateUtil.creditValidate(user, 1);

        int size = copiesIsbn.length;
        Book_copies[] book_copies = new Book_copies[size];
        Borrow_records[] borrow_records = new Borrow_records[size];

        for (int i = 0; i < size; i ++) {
            book_copies[i] = validateUtil.copyStatusValidate(copiesIsbn[i], "已预约", location);
            borrow_records[i] = findRecord(username, copiesIsbn[i]);
            if (ifOverdue(book_copies[i]))
                throw new BadRequestException("Reserve for book: " + copiesIsbn[i] + " has expired");
        }

        borrow(borrow_records, location, admin, new Date(), user.getMaxBorrowTime(), book_copies);

        return mapUtil.getMessageMap("success");
    }

    /**
     * 现场借书的方法（可一次借多个）
     *
     * @param location      管理员上班位置
     * @param admin         操作管理员
     * @param username      借阅人username
     * @param copiesIsbn    多个副本的isbn，状态必须是可借阅
     * @return              成功时返回 {"message":"success"} 失败时抛异常
     */
    public Map<String, String> borrowBook(String location, String admin, String username, String[] copiesIsbn) {
        validateUtil.authorityValidate(username, "Student, Teacher");
        validateUtil.borrowLimitValidate(username, copiesIsbn.length);
        User user = (User) jwtUserDetailsService.loadUserByUsername(username);
        validateUtil.creditValidate(user, 1);

        int size = copiesIsbn.length;
        Book_copies[] book_copies = new Book_copies[size];
        Borrow_records[] borrow_records = new Borrow_records[size];

        for (int i = 0; i < size; i ++) {
            book_copies[i] = validateUtil.copyStatusValidate(copiesIsbn[i], "可借阅", location);
            borrow_records[i] = new Borrow_records(copiesIsbn[i], username, "进行中");
        }

        borrow(borrow_records, location, admin, new Date(), user.getMaxBorrowTime(), book_copies);

        return mapUtil.getMessageMap("success");
    }

    /**
     * 用户还书（可一次还多个），违规时生成罚款
     *
     * @param location      管理员上班位置
     * @param admin         操作管理员
     * @param copiesIsbn    多个副本的isbn，状态必须是已借阅
     * @return              成功时返回 {"message":"success"} 失败时抛异常
     */
    public List<String> returnBook(String location, String admin, String[] copiesIsbn, Boolean[] ifDamaged, Boolean[] ifLost) {
        List<String> overdueList = new ArrayList<>();
        int size = copiesIsbn.length;
        Book_copies[] book_copies = new Book_copies[size];
        Borrow_records[] borrow_records = new Borrow_records[size];

        for (int i = 0; i < size; i ++) {
            book_copies[i] = bookCopiesRepository.findByIsbn(copiesIsbn[i]);
            if (!book_copies[i].getStatus().equals("已借阅"))
                throw new BadRequestException("Wrong status for book: " + copiesIsbn[i]);
            Borrow_records record =  borrowRecordsRepository.findByStatusAndIsbn("进行中", copiesIsbn[i]);
            record = record == null ? borrowRecordsRepository.findByStatusAndIsbn("已逾期", copiesIsbn[i]) : record;
            borrow_records[i] = record;
            if (borrow_records[i] == null)
                throw new NotFoundException("Record for " + copiesIsbn[i] + " not found");
        }

        for (int i = 0; i < size; i ++) {
            if (returnBook(book_copies[i], borrow_records[i], admin, location, ifLost[i], ifDamaged[i]))
                overdueList.add(copiesIsbn[i]);
        }

        return overdueList;
    }


    /**
     * 获取某一副本的借阅记录
     */
    public List<Map> getCopyHistory(String isbn) {
        bookService.findCopyByIsbn(isbn);
        List<Borrow_records> borrow_records = borrowRecordsRepository.findAllByIsbn(isbn);
        return getRecords(borrow_records);
    }

    /**
     * 获取用户借阅记录，用户信息不对（权限不对，用户名不存在等）会抛异常
     *
     * @return   List<Map>，map中含有的健：name, author, copy(封装副本信息), record(借阅记录)
     */
    public List<Map> getUserRecords(String username) {
        validateUtil.authorityValidate(username, "Student, Teacher");
        updateRecordsStatus(username);
        List<Borrow_records> borrow_records = borrowRecordsRepository.findAllByBorrower(username);
        return getRecords(borrow_records);
    }

    /**
     * 获取用户已经预约的副本信息，用户信息不对（权限不对，用户名不存在等）会抛异常
     *
     * @return   List<Map>，map中含有的健：name, author, copy(封装副本信息), record(借阅记录)
     */
    public List<Map> getReservedBook(String username) {
        validateUtil.authorityValidate(username, "Student, Teacher");
        List<Borrow_records> borrow_records = borrowRecordsRepository.findAllByBorrowerAndStatusAndBorrowTime(username, "进行中", null);
        return getRecords(borrow_records);
    }

    public void updateRecordsStatus(String username) {
        List<Borrow_records> borrow_records = borrowRecordsRepository.findAllByBorrowerAndStatus(username, "进行中");
        for(Borrow_records records: borrow_records) {
            Book_copies book_copies = bookService.findCopyByIsbn(records.getIsbn());
            if (book_copies.getStatus().equals("已借阅") && ifOverdue(book_copies)) {
                records.setStatus("已逾期");
                borrowRecordsRepository.save(records);
            }
        }
    }

    /**
     * 工具方法，带异常的借阅记录查找
     */
    public Borrow_records findRecord(String username, String isbn) {
        Borrow_records borrow_records = borrowRecordsRepository.findByBorrowerAndStatusAndIsbn(username, "进行中", isbn);
        if (borrow_records == null)
            throw new NotFoundException("Record for " + isbn + " not found");
        return borrow_records;
    }

    /**
     * 工具方法，检测副本借阅或预约是否逾期
     */
    public boolean ifOverdue(Book_copies book_copies) {
        Date date = dateUtil.stringToDate(book_copies.getTime(), format);
        if (book_copies.getTime_limit() != null  && ((new Date().getTime() - date.getTime()) / 1000) > book_copies.getTime_limit())
            return true;
        return false;
    }

    /**
     * 工具方法，清空副本的借阅信息并设置状态
     */
    public void setStatusAndNull(Book_copies book_copies, String status) {
        book_copies.setStatus(status);
        book_copies.setBorrower(null);
        book_copies.setTime(null);
        book_copies.setTime_limit(null);
    }

    /**
     * 工具方法，扣除用户信用分
     */
    public void creditDeduct(User user, int dec) {
        int credit = user.getCredit();
        credit -= dec;
        if (credit < 0)
            credit = 0;
        user.setCredit(credit);
        userRepository.save(user);
    }

    /**
     * 内部方法，用于返回借阅记录信息的包装
     */
    private List<Map> getRecords(List<Borrow_records> borrow_records) {
        List<Book> books = new ArrayList<>();
        List<Book_copies> book_copies = new ArrayList<>();

        for (Borrow_records record: borrow_records) {
            String isbn = record.getIsbn();
            book_copies.add(bookService.findCopyByIsbn(isbn));
            books.add(bookService.findByIsbn(isbn.substring(0, isbn.length() - 4)));
        }
        List<Map> mapList = new ArrayList<>();

        for (int i = 0; i < borrow_records.size(); i ++) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", books.get(i).getName());
            map.put("author", books.get(i).getAuthor());
            map.put("copy", book_copies.get(i));
            map.put("record", borrow_records.get(i));

            if (book_copies.get(i).getBorrower() != null)
                map.put("expiration", dateUtil.sumDate(book_copies.get(i).getTime(), book_copies.get(i).getTime_limit()));
            else
                map.put("expiration", null);

            mapList.add(map);
        }
        return mapList;
    }


    /**
     * 内部方法，借书预约时的数据库操作
     */
    private void borrow(Borrow_records[] borrow_records, String location, String admin, Date time, Integer time_limit, Book_copies[] book_copies) {
        int size = borrow_records.length;
        String date = dateUtil.toDateString(time, format);
        for (int i = 0; i < size; i ++) {
            borrow_records[i].setBorrow_location(location);
            borrow_records[i].setBorrow_admin(admin);
            borrow_records[i].setBorrowTime(date);
            borrowRecordsRepository.save(borrow_records[i]);

            book_copies[i].setStatus("已借阅");
            book_copies[i].setLocation(null);
            book_copies[i].setBorrower(borrow_records[i].getBorrower());
            book_copies[i].setTime(date);
            book_copies[i].setTime_limit(time_limit);
            bookCopiesRepository.save(book_copies[i]);
        }
    }

    /**
     * 内部方法，还书时的数据库操作
     */
    private boolean returnBook(Book_copies book_copies, Borrow_records borrow_records, String admin, String location, boolean ifLost, boolean ifDamaged) {
        boolean ifOverdue = false;
        float fine = 0;
        int credit = 0;
        Book book = bookService.findByIsbn(book_copies.getIsbn().substring(0, book_copies.getIsbn().length() - 4));
        String status = "已结束";
        String copyStatus = "可借阅";

        if (ifLost) {
            if (ifDamaged)
                throw new BadRequestException("Book can't be lost and damaged at the same time (from " + book_copies.getIsbn() + ")");
            status = "图书遗失";
            copyStatus = "已遗失";
            fine = book.getPrice();
            credit = 40;
        } else if (ifDamaged) {
            status = "图书损坏";
            copyStatus = "已损坏";
            fine = book.getPrice() / 2;
            credit = 30;
        } else if (ifOverdue(book_copies)) {
            status = "逾期归还";
            fine = book.getPrice() / 4;
            ifOverdue = true;
            credit = 20;
        }

        if (fine > 0) {
            fine = (float) ((int)(100 * fine) / 100.0);
            User user = (User) jwtUserDetailsService.loadUserByUsername(book_copies.getBorrower());
            user.setFine(user.getFine() + fine);
            creditDeduct(user, credit);
            borrow_records.setCredit(credit);
            borrow_records.setFine(fine);
        }

        borrow_records.setReturn_admin(admin);
        borrow_records.setReturn_location(location);
        borrow_records.setReturnTime(dateUtil.toDateString(new Date(), format));
        borrow_records.setStatus(status);
        borrowRecordsRepository.save(borrow_records);

        book_copies.setLocation(ifLost ? null : location);
        setStatusAndNull(book_copies, copyStatus);
        bookCopiesRepository.save(book_copies);
        return ifOverdue;
    }

}
