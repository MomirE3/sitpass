package svt.projekat.repository;

import svt.projekat.model.entity.Facility;

import java.time.LocalTime;
import java.util.List;

public interface FacilityRepositoryCustom {
    List<Facility> filterFacilities(String discipline_name, Double minRating, Double maxRating,
                                    LocalTime fromTime, LocalTime untilTime, String city);
}
