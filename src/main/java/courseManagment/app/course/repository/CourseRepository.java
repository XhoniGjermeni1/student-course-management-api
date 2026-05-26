package courseManagment.app.course.repository;

import courseManagment.app.course.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CourseRepository extends JpaRepository<Course, UUID> {
    boolean existsByName(String name);
    Optional<Course> findByName(String name);
}
