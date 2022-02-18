package fudan.se.hardlibrary.utils;

import fudan.se.hardlibrary.TestConfig;
import fudan.se.hardlibrary.service.BookService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class DateUtilTest extends TestConfig {

    @Autowired
    BookService bookService;

    @Test
    public void toDateString1Normal() {
        DateUtil dateUtil = new DateUtil();
        assertEquals(String.class, dateUtil.toDateString(new Date(), "yy-mm-dd").getClass());
    }

    @Test
    public void toDateString2NullDate() {
        DateUtil dateUtil = new DateUtil();
        assertNull(dateUtil.toDateString(null, "yy-mm-dd"));
    }

    @Test
    public void toDateString3NullFormat() {
        DateUtil dateUtil = new DateUtil();
        assertNull(dateUtil.toDateString(new Date(), null));
    }

    @Test
    public void after() {
        bookService.delete("123-155");
    }

}