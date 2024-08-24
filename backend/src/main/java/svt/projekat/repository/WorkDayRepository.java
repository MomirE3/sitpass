package svt.projekat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import svt.projekat.model.entity.Facility;
import svt.projekat.model.entity.WorkDay;

import java.util.List;

@Repository
public interface WorkDayRepository extends JpaRepository<WorkDay, Long> {
    List<WorkDay> findByFacility(Facility facility);
    void deleteByFacilityAndIdNotIn(Facility facility, List<Long> ids);

}
