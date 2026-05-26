package courseManagment.app.student.repository;

import courseManagment.app.student.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StudentRepository extends JpaRepository<Student, UUID> {
    Optional<Student> findByFirstNameIgnoreCaseAndIsActive(String name, boolean active);
    Optional<Student> findByFirstNameIgnoreCase(String name);
    Page<Student> findByFirstNameContainingIgnoreCase(String name, Pageable pageable);
    @Query("Select s from Student s " +
            "JOIN s.enrollments e " +
            "join e.course c " +
            "where lower(c.name) = lower(:courseName)"
    )
    List<Student> findByCourse(@Param("courseName") String courseName);
}
