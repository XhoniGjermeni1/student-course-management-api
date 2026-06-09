package courseManagment.app.enrollment.services;

import courseManagment.app.course.entity.Course;
import courseManagment.app.course.repository.CourseRepository;
import courseManagment.app.enrollment.dto.CreateEnrollmentDTO;
import courseManagment.app.enrollment.entity.Enrollment;
import courseManagment.app.enrollment.repository.EnrollmentRepository;
import courseManagment.app.exception.BusinessRuleException;
import courseManagment.app.exception.NotFoundException;
import courseManagment.app.student.entity.Student;
import courseManagment.app.student.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;

    public EnrollmentService(EnrollmentRepository enrollmentRepository, CourseRepository courseRepository, StudentRepository studentRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.courseRepository = courseRepository;
        this.studentRepository = studentRepository;
    }

    @Transactional
    public String addEnrollment(CreateEnrollmentDTO dto) {
        Enrollment enrollment = new Enrollment();
        Course tempCourse = courseRepository.findByName(dto.getCourse())
                .orElseThrow(() -> new NotFoundException("Kursi nuk u gjet!"));
        Student tempStudent = studentRepository.findByFirstNameIgnoreCaseAndIsActive(dto.getStudent(), true)
                .orElseThrow(() -> new NotFoundException("Studenti nuk u gjet!"));

        if (enrollmentRepository.existsByStudentAndCourse(tempStudent, tempCourse)) {
            throw new BusinessRuleException("Studenti " + dto.getStudent() + " eshte i regjistruar nje here ne kete kurs!");
        }

        int numOfStudents = enrollmentRepository.countByCourse(tempCourse);
        if (tempCourse.getCapacity() > numOfStudents) {
            enrollment.setCourse(tempCourse);
            enrollment.setStudent(tempStudent);

            enrollmentRepository.save(enrollment);
            return "Regjistrimi u krye me sukses per studentin: " + dto.getStudent();
        } else {
            throw new BusinessRuleException("Kursi nuk ka me kapacitet per studente te rinj!");
        }
    }

    @Transactional
    public String deleteEnrollment(Course course, Student student) {
        Optional<Enrollment> enrollment = enrollmentRepository.findByStudentAndCourse(student, course);
        if (enrollment.isPresent()) {
            enrollmentRepository.delete(enrollment.get());
        } else {
            throw new NotFoundException("Regjistrimi nuk u gjet!");
        }
        return "U fshi me sukses!";
    }
}
