package svt.projekat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import svt.projekat.model.dto.AccountRequestDTO;
import svt.projekat.model.entity.AccountRequest;

import java.util.Optional;

@Repository
public interface AccountRequestRepository extends JpaRepository<AccountRequest, Long> {
    Optional<AccountRequest> findByEmail(String email);

}
