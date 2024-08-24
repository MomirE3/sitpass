package svt.projekat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import svt.projekat.model.entity.Comment;
@Repository

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
