package fudan.se.hardlibrary.repository;

import fudan.se.hardlibrary.domain.Authority;
import fudan.se.hardlibrary.domain.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * @author LBW
 */
@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    User findByUsername(String username);

    List<User> findAllByAuthoritiesContaining(Authority authority);

}
