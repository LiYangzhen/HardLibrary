package fudan.se.hardlibrary.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.util.Set;

/**
 * @author LBW
 */
@Entity
public class Authority implements GrantedAuthority {

    private static final long serialVersionUID = -8974777274465208640L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String authority;
    private Integer maxCopies;
    private Integer maxBorrowTime;
    private Integer maxReserveTime;

    @ManyToMany(mappedBy = "authorities")
    @JsonIgnore
    private Set<User> users;

    public Authority() {
    }

    public Authority(String authority) {
        this.authority = authority;
    }

    @Override
    public String getAuthority() {
        return authority;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    public Integer getMaxCopies() {
        return maxCopies;
    }

    public void setMaxCopies(Integer maxCopies) {
        this.maxCopies = maxCopies;
    }

    public Integer getMaxBorrowTime() {
        return maxBorrowTime;
    }

    public void setMaxBorrowTime(Integer maxBorrowTime) {
        this.maxBorrowTime = maxBorrowTime;
    }

    public Integer getMaxReserveTime() {
        return maxReserveTime;
    }

    public void setMaxReserveTime(Integer maxReserveTime) {
        this.maxReserveTime = maxReserveTime;
    }
}
