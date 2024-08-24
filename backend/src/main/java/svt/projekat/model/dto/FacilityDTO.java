package svt.projekat.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FacilityDTO {
    private long id;
    private String name;
    private String city;
    private String address;
    private String description;
    private double totalRating;
    private List<String> disciplines;
    private List<WorkDayDTO> workDays;
    private List<ReviewDTO> reviews;
    private List<String> images;
    private List<String> imagesToDelete;

    public FacilityDTO(long id, String name, String city, String address, String description, double totalRating, List<String> disciplines, List<WorkDayDTO> workDays) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.address = address;
        this.description = description;
        this.totalRating = totalRating;
        this.disciplines = disciplines;
        this.workDays = workDays;
        this.images = null;
        this.imagesToDelete = null;
    }

    public FacilityDTO(long id, String name, String city, String address, String description, double totalRating, List<String> disciplines, List<WorkDayDTO> workDays, List<String> images) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.address = address;
        this.description = description;
        this.totalRating = totalRating;
        this.disciplines = disciplines;
        this.workDays = workDays;
        this.images = images;
        this.imagesToDelete = null;
    }
}
