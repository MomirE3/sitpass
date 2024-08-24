package svt.projekat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import svt.projekat.model.entity.Rate;
@Repository

public interface RateRepository extends JpaRepository<Rate, Long> {
}
