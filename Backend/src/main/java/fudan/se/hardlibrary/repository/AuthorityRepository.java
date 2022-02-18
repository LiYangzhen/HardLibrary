package fudan.se.hardlibrary.repository;

import fudan.se.hardlibrary.domain.Authority;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author LBW
 */
@Repository
public interface AuthorityRepository extends CrudRepository<Authority, Long> {

    Authority findByAuthority(String authority);

}
