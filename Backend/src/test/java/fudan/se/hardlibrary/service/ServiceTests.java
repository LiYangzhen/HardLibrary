package fudan.se.hardlibrary.service;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({AuthorityServiceTest.class, AuthServiceTest.class, BookServiceTest.class, BorrowServiceTest.class, ReviewServiceTest.class, CreditServiceTest.class, EmailServiceTest.class, JwtUserDetailsServiceTest.class})
public class ServiceTests {

}
