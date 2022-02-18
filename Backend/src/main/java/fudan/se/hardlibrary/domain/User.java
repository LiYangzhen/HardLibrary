package fudan.se.hardlibrary.domain;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.*;

/**
 * @author LBW
 */
@Entity
public class User implements UserDetails {

    private static final long serialVersionUID = -6140085056226164016L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    private String password;
    private String email;
    private Float fine;
    private Integer credit;

    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinTable(name = "user_authority",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "authority_id", referencedColumnName = "id")})
    private Set<Authority> authorities = new HashSet<>();


    public User() {}
    public User(String username, String password, String email, Set<Authority> authorities) {
        this.username = username;
        this.password= password;
        this.email = email;
        this.authorities = authorities;
        this.fine = 0f;
        this.credit = 100;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAuthorities(Set<Authority> authorities) {
        this.authorities = authorities;
    }

    public Float getFine() {
        return fine;
    }

    public void setFine(Float fine) {
        this.fine = fine;
    }

    public Integer getCredit() {
        return credit;
    }

    public void setCredit(Integer credit) {
        this.credit = credit;
    }

    public int getMaxCopies() {
        int max = 0;
        for (Authority authority: authorities)
            if (authority.getMaxCopies() != null && authority.getMaxCopies() > max)
                max = authority.getMaxCopies();
        return max;
    }

    public int getMaxReserveTime() {
        int max = 0;
        for (Authority authority: authorities)
            if (authority.getMaxReserveTime() != null && authority.getMaxReserveTime() > max)
                max = authority.getMaxReserveTime();
        return max;
    }

    public int getMaxBorrowTime() {
        int max = 0;
        for (Authority authority: authorities)
            if (authority.getMaxBorrowTime() != null && authority.getMaxBorrowTime() > max)
                max = authority.getMaxBorrowTime();
        return max;
    }
}
