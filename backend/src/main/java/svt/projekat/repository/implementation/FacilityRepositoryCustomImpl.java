package svt.projekat.repository.implementation;

import org.springframework.stereotype.Repository;
import svt.projekat.model.entity.Facility;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import svt.projekat.repository.FacilityRepositoryCustom;

import java.time.LocalTime;
import java.util.List;

@Repository
public class FacilityRepositoryCustomImpl implements FacilityRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Facility> filterFacilities(String discipline_name, Double minRating, Double maxRating,
                                           LocalTime fromTime, LocalTime untilTime, String city) {
        StringBuilder queryBuilder = new StringBuilder("SELECT f FROM Facility f LEFT JOIN f.disciplines d LEFT JOIN f.workDays wd WHERE 1=1");
        System.out.println(fromTime);
        if (discipline_name != null && !discipline_name.isEmpty()) {
            queryBuilder.append(" AND d.name = :discipline_name");
        }

        if (city != null && !city.isEmpty()) {
            queryBuilder.append(" AND f.city = :city");
        }

        if (minRating != null && maxRating != null) {
            queryBuilder.append(" AND f.totalRating BETWEEN :minRating AND :maxRating");
        } else if (minRating != null) {
            queryBuilder.append(" AND f.totalRating >= :minRating");
        } else if (maxRating != null) {
            queryBuilder.append(" AND f.totalRating <= :maxRating");
        }

        if (fromTime != null && untilTime != null) {
            queryBuilder.append(" AND wd.from <= :untilTime AND wd.until >= :fromTime");
        } else if (fromTime != null) {
            queryBuilder.append(" AND wd.until >= :fromTime");
        } else if (untilTime != null) {
            queryBuilder.append(" AND wd.from <= :untilTime");
        }

        TypedQuery<Facility> query = entityManager.createQuery(queryBuilder.toString(), Facility.class);

        if (discipline_name != null && !discipline_name.isEmpty()) {
            query.setParameter("discipline_name", discipline_name);
        }

        if (city != null && !city.isEmpty()) {
            query.setParameter("city", city);
        }

        if (minRating != null) {
            query.setParameter("minRating", minRating);
        }

        if (maxRating != null) {
            query.setParameter("maxRating", maxRating);
        }

        if (fromTime != null) {
            query.setParameter("fromTime", fromTime);
        }

        if (untilTime != null) {
            query.setParameter("untilTime", untilTime);
        }

        return query.getResultList();
    }
}
