package courseManagment.app.enrollment.repository;

import courseManagment.app.course.entity.Course;
import courseManagment.app.enrollment.entity.Enrollment;
import courseManagment.app.student.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, UUID> {
    boolean existsByStudentAndCourse(Student student, Course course);
    Optional<Enrollment> findByStudentAndCourse(Student student, Course course);
    List<Enrollment> findByStudent(Student student);
    List<Enrollment> findByCourse(Course course);
    int countByCourse(Course course);
}
