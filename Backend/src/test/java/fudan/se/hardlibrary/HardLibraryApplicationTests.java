package fudan.se.hardlibrary;

import fudan.se.hardlibrary.service.*;
import fudan.se.hardlibrary.utils.DateUtilTest;
import fudan.se.hardlibrary.utils.ValidateUtilTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({AuthorityServiceTest.class, AuthServiceTest.class, BookServiceTest.class, BorrowServiceTest.class, ReviewServiceTest.class, CreditServiceTest.class, EmailServiceTest.class, JwtUserDetailsServiceTest.class,  ValidateUtilTest.class, DateUtilTest.class,})
public class HardLibraryApplicationTests {

}
