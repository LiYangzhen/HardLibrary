package fudan.se.hardlibrary.repository;

import fudan.se.hardlibrary.domain.Reset_records;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResetRecordsRepository extends JpaRepository<Reset_records, Long> {

    List<Reset_records> findAllByUsernameOrderByIdDesc(String username);
}
