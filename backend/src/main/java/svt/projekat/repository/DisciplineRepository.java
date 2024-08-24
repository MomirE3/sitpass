package svt.projekat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import svt.projekat.model.entity.Discipline;
import svt.projekat.model.entity.Facility;

import java.util.List;

@Repository
public interface DisciplineRepository extends JpaRepository<Discipline, Long> {

    @Query("SELECT DISTINCT d.name FROM Discipline d WHERE d.byFacility.active = true")
    List<String> findAllUniqueDisciplineNames();

    List<Discipline> findByByFacility(Facility facility);

    void deleteByByFacilityAndNameNotIn(Facility facility, List<String> names);
}
