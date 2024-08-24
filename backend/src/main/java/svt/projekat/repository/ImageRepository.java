package svt.projekat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import svt.projekat.model.entity.Image;

import java.util.Optional;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
    Optional<Image> findByPath(String path);
    void deleteById(Long id);
    @Modifying
    @Query("delete from Image i where i.id = :id")
    void deleteById(@Param("id") long id);
    void flush();
}
