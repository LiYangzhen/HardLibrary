package fudan.se.hardlibrary.service;

import fudan.se.hardlibrary.TestConfig;

import org.junit.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class JwtUserDetailsServiceTest extends TestConfig {

    @Autowired
    JwtUserDetailsService jwtUserDetailsService;

    @Test
    public void loadUserByUsername1Normal() {
        assertNotNull(jwtUserDetailsService.loadUserByUsername("admin"));
    }

    @Test
    public void loadUserByUsername2Null() {
        assertThrows(UsernameNotFoundException.class, () -> jwtUserDetailsService.loadUserByUsername(""));
    }

    @Test
    public void loadUserByUsername3NotExist() {
        assertThrows(UsernameNotFoundException.class, () -> jwtUserDetailsService.loadUserByUsername("notexist"));
    }

}