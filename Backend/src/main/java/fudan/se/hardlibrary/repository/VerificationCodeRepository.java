package fudan.se.hardlibrary.repository;

import fudan.se.hardlibrary.domain.Verification_code;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationCodeRepository extends JpaRepository<Verification_code, Long> {

    Verification_code findByEmailAndCode(String email, String code);

    Verification_code findFirstByEmailOrderByIdDesc(String username);

}
