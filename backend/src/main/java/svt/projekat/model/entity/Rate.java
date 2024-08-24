package svt.projekat.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "rates")
public class Rate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private Integer equipment;

    @Column(nullable = false)
    private Integer staff;

    @Column(nullable = false)
    private Integer hygiene;

    @Column(nullable = false)
    private Integer space;

    @JsonIgnore
    @OneToOne(mappedBy = "rate", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Review review;
}
