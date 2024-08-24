package svt.projekat.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "facilities")
public class Facility {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private LocalDate createdAt;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String city;

    @Column()
    private Double totalRating;

    @Column(nullable = false)
    private boolean active;

    @JsonIgnore
    @OneToMany(mappedBy = "facility" , fetch = FetchType.LAZY , cascade = CascadeType.ALL)
    private Set<Manages> manages = new HashSet<Manages>();

    @JsonIgnore
    @OneToMany(mappedBy = "atFacility" , fetch = FetchType.LAZY , cascade = CascadeType.ALL)
    private Set<Exercise> exercises = new HashSet<Exercise>();

    @JsonIgnore
    @OneToMany(mappedBy = "facility" , fetch = FetchType.LAZY , cascade = CascadeType.ALL)
    private Set<WorkDay> workDays = new HashSet<WorkDay>();

    @JsonIgnore
    @OneToMany(mappedBy = "byFacility" , fetch = FetchType.LAZY , cascade = CascadeType.ALL)
    private Set<Discipline> disciplines = new HashSet<Discipline>();

    @JsonIgnore
    @OneToMany(mappedBy = "belongsToFacility" , fetch = FetchType.LAZY , cascade = CascadeType.ALL)
    private Set<Image> images = new HashSet<Image>();

    @JsonIgnore
    @OneToMany(mappedBy = "facility", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Review> reviews = new HashSet<Review>();
}

