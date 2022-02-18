package fudan.se.hardlibrary.service;

import fudan.se.hardlibrary.domain.Book;
import fudan.se.hardlibrary.domain.Book_copies;
import fudan.se.hardlibrary.domain.Borrow_records;
import fudan.se.hardlibrary.domain.User;
import fudan.se.hardlibrary.exception.BadRequestException;
import fudan.se.hardlibrary.exception.DBException;
import fudan.se.hardlibrary.exception.NotFoundException;
import fudan.se.hardlibrary.repository.BookCopiesRepository;
import fudan.se.hardlibrary.repository.BookRepository;
import fudan.se.hardlibrary.repository.BorrowRecordsRepository;
import fudan.se.hardlibrary.repository.UserRepository;
import fudan.se.hardlibrary.utils.DateUtil;
import fudan.se.hardlibrary.utils.MapUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Class for getting books and copies information, uploading, deleting and so on
 *
 * Possible exception thrown by this class：
 * @NotFoundException: http status: 404, reason: isbn not found.
 * @BadRequestException: http status: 400, reason: isbn has existed, add too many copies and so on.
 * @DBException: http status: 503, reason: upload or delete failure possibly caused by serve busy.
 */
@Service
public class BookService {

    private BookRepository bookRepository;
    private BookCopiesRepository bookCopiesRepository;
    private DateUtil dateUtil;
    private MapUtil mapUtil;

    @Resource
    BorrowRecordsRepository borrowRecordsRepository;

    @Resource
    UserRepository userRepository;

    @Autowired
    public BookService(BookRepository bookRepository, BookCopiesRepository bookCopiesRepository, DateUtil dateUtil, MapUtil mapUtil) {
        this.bookRepository = bookRepository;
        this.bookCopiesRepository = bookCopiesRepository;
        this.dateUtil = dateUtil;
        this.mapUtil = mapUtil;
    }

    /**
     * 根据分页情况，查找所有图书
     *
     * @param page      页数索引，从零开始
     * @param size      每页中书的最大数量
     * @return          Page<Book>
     */
    public Page<Book> findBooks(Integer page, Integer size) {
        if (page < 0 || size < 0)
            throw new BadRequestException("Page or size can't be negative");
        Pageable pageable = PageRequest.of(page, size);
        return bookRepository.findAll(pageable);
    }

    /**
     * 根据isbn查找图书
     *
     * @param isbn      不存在时抛异常
     * @return          Book or Exception
     */
    public Book findByIsbn(String isbn) {
        Book book = bookRepository.findByIsbn(isbn);
        if (book == null)
            throw new NotFoundException("Isbn: " + isbn + " not found");
        return book;
    }

    /**
     * 根据isbn搜索图书，isbn空时抛异常
     */
    public List<Book> searchByIsbn(String isbn) {
        if (isbn == null || isbn.length() == 0)
            throw new BadRequestException("Key word can't be null");
        return bookRepository.findAllByIsbnLike("%" + isbn + "%");
    }

    /**
     * 根据图书名称搜索图书，name空时抛异常
     */
    public List<Book> searchByName(String name) {
        if (name == null || name.length() == 0)
            throw new BadRequestException("Key word can't be null");
        return bookRepository.findAllByNameLike("%" + name + "%");
    }

    /**
     * 根据作者搜索图书，author空时抛异常
     */
    public List<Book> searchByAuthor(String author) {
        if (author == null || author.length() == 0)
            throw new BadRequestException("Key word can't be null");
        return bookRepository.findAllByAuthorLike("%" + author + "%");
    }

    /**
     * 根据isbn查找副本， 副本格式应严格遵循格式：isbn-xxx
     *
     * @param isbn  图书的isbn而非副本的
     */
    public List<Book_copies> findCopiesByIsbn(String isbn) {
        int length = isbn.length() + 4;
        String regexp = "^[0-9-]{" + length +  "}$";
        return bookCopiesRepository.findAllByIsbnMatchesAndLike(regexp,isbn + "%");
    }

    /**
     * 根据isbn查找副本
     *
     * @param isbn  副本isbn
     */
    public Book_copies findCopyByIsbn(String isbn) {
        Book_copies book_copies = bookCopiesRepository.findByIsbn(isbn);
        if (book_copies == null)
            throw new NotFoundException("Isbn: " + isbn + " not found");
        return book_copies;
    }

    /**
     * 上传，isbn已存在时抛异常
     *
     * @return  成功时返回 {"message":"success"} 失败时抛异常
     * @throws  DBException :传入信息正确，但上传失败，一般不会触发
     */
    public Map<String, String> upload(String isbn, String name, String author, String introduction, Date date, Float price, MultipartFile image) throws IOException {
        if (isbn == null)
            throw new BadRequestException("Isbn can't be null");
        if (bookRepository.findByIsbn(isbn) != null)
            throw new BadRequestException("Isbn has existed");
        if(price < 0)
            throw new BadRequestException("Negative price");
        price = (float)((int)(price * 100) / 100.0);
        Book book = new Book(isbn, name, author, introduction, dateUtil.toDateString(date, "yyyy"), price);
        if (image != null)
            book.setImage(image.getBytes());
        bookRepository.save(book);
        if (bookRepository.findByIsbn(isbn) == null)
            throw new DBException("DB error, please try again later");
        return mapUtil.getMessageMap("success");
    }

    /**
     * 修改图书信息，和上传基本一致，isbn不存在时抛异常
     */
    public Map<String, String> update(String isbn, String name, String author, String introduction, Date date, Float price, MultipartFile image) throws IOException {
        Book book = findByIsbn(isbn);
        book.setName(name);
        book.setAuthor(author);
        book.setIntroduction(introduction);
        book.setDate(dateUtil.toDateString(date, "yyyy"));
        book.setPrice(price);
        if (image != null)
            book.setImage(image.getBytes());
        bookRepository.save(book);
        return mapUtil.getMessageMap("success");
    }

    /**
     * 根据isbn删除图书信息，isbn不存在时抛异常
     *
     * @return  成功时返回 {"message":"success"} 失败时抛异常
     * @throws  DBException
     */
    public Map<String, String> delete(String isbn) {
        findByIsbn(isbn);
        for (Book_copies copy: findCopiesByIsbn(isbn)) {
            bookCopiesRepository.delete(copy);
            borrowRecordsRepository.deleteAllByIsbn(copy.getIsbn());
        }
        bookRepository.deleteById(isbn);
        if (bookRepository.findByIsbn(isbn) != null)
            throw new DBException("DB error, please try again later");
        return mapUtil.getMessageMap("success");
    }

    /**
     * 添加副本
     *
     * @param isbn      图书isbn
     * @param count     添加副本数量，不应该太多，副本总数应小于999
     * @param location  位置
     * @return          成功时返回 {"message":"success"} 失败时抛异常
     */
    public Map<String, String> addCopies(String isbn, int count, String location) {
        Book book = findByIsbn(isbn);
        if (count <= 0)
            throw new BadRequestException("Count must be positive integer");
        if (book.getCount() + count > 999)
            throw new BadRequestException("Too many copy uploads");
        for (int i = 0; i < count; i ++) {
            int next_count = book.getCount() + 1;
            StringBuilder copy_isbn;
            do {
                copy_isbn = new StringBuilder(next_count + "");
                while (copy_isbn.length() < 3)
                    copy_isbn.insert(0, '0');
                copy_isbn.insert(0, book.getIsbn() + "-");
                next_count ++;
            } while (bookCopiesRepository.findByIsbn(copy_isbn.toString()) != null);

            Book_copies book_copies = new Book_copies(copy_isbn.toString(), "可借阅", location);
            bookCopiesRepository.save(book_copies);
            book.setCount(book.getCount() + 1);
        }
        bookRepository.save(book);
        return mapUtil.getMessageMap("success");
    }

    /**
     * 修改副本信息
     *
     * @param isbn      副本isbn
     * @param location  要修改的location
     * @param ifLost    是否遗失
     * @return          成功时返回 {"message":"success"} 失败时抛异常
     */
    public Map<String, String> updateCopy(String isbn, String location, boolean ifLost) {
        Book_copies book_copies = findCopyByIsbn(isbn);
        if (ifLost) {
            addFine(book_copies);
            book_copies.setStatus("已遗失");
            book_copies.setLocation(null);
            book_copies.setBorrower(null);
            book_copies.setTime(null);
            book_copies.setTime_limit(null);
        } else {
            if (location == null || location.length() == 0)
                throw new BadRequestException("Location can't be null");
            if (book_copies.getStatus().equals("已借阅"))
                throw new BadRequestException("Can't change location for the book which has been lent");
            if (book_copies.getStatus().equals("已遗失"))
                book_copies.setStatus("可借阅");
            book_copies.setLocation(location);
        }
        bookCopiesRepository.save(book_copies);
        return mapUtil.getMessageMap("success");
    }

    /**
     * 删除副本，和删除图书差不多
     */
    public Map<String, String> deleteCopy(String isbn) {
        Book_copies book_copies = findCopyByIsbn(isbn);
        bookCopiesRepository.delete(book_copies);
        if (bookCopiesRepository.findByIsbn(isbn) != null)
            throw new DBException("DB error, please try again later");
        Book book = findByIsbn(isbn.substring(0, isbn.length() - 4));
        book.setCount(book.getCount() - 1);
        bookRepository.save(book);
        borrowRecordsRepository.deleteAllByIsbn(isbn);
        return mapUtil.getMessageMap("success");
    }

    /**
     * 内部方法，只用于updateCopy 当图书遗失时生成罚款
     */
    private void addFine(Book_copies book_copies) {
        if (book_copies.getStatus().equals("已借阅")) {
            Book book = findByIsbn(book_copies.getIsbn().substring(0, book_copies.getIsbn().length() - 4));
            User user = userRepository.findByUsername(book_copies.getBorrower());
            if (user != null) {
                user.setCredit(user.getCredit() - 40);
                if (user.getCredit() < 0)
                    user.setCredit(0);
                user.setFine(user.getFine() + book.getPrice());
                userRepository.save(user);
            }
            Borrow_records borrow_records = borrowRecordsRepository.findByBorrowerAndStatusAndIsbn(book_copies.getBorrower(), "进行中", book_copies.getIsbn());
            if (borrow_records != null) {
                borrow_records.setStatus("图书遗失");
                borrow_records.setFine(book.getPrice());
                borrow_records.setCredit(40);
                borrowRecordsRepository.save(borrow_records);
            }
        }
    }

}
