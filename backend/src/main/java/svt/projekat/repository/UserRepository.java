package svt.projekat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import svt.projekat.model.entity.User;

import java.util.List;
import java.util.Optional;
@Repository

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findFirstByEmail(String email);
    @Query("SELECT u FROM User u WHERE TYPE(u) = User")
    List<User> findAllUsersWithUserType();
}
