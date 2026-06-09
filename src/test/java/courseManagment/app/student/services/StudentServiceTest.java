package courseManagment.app.student.services;

import courseManagment.app.course.entity.Course;
import courseManagment.app.course.repository.CourseRepository;
import courseManagment.app.enrollment.entity.Enrollment;
import courseManagment.app.enrollment.repository.EnrollmentRepository;
import courseManagment.app.exception.BusinessRuleException;
import courseManagment.app.exception.NotFoundException;
import courseManagment.app.student.entity.Student;
import courseManagment.app.student.repository.StudentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private StudentService studentService;

    @Test
    void shouldCalculateGpaCorrectly() {
        Student student = new Student();
        student.setFirstName("John");

        Course java = new Course();
        java.setCredits(3);

        Course math = new Course();
        math.setCredits(1);

        Enrollment javaEnrollment = new Enrollment();
        javaEnrollment.setCourse(java);
        javaEnrollment.setGrade("A");

        Enrollment mathEnrollment = new Enrollment();
        mathEnrollment.setCourse(math);
        mathEnrollment.setGrade("C");

        when(studentRepository.findByFirstNameIgnoreCaseAndIsActive("John", true))
                .thenReturn(Optional.of(student));
        when(enrollmentRepository.findByStudent(student))
                .thenReturn(List.of(javaEnrollment, mathEnrollment));

        double gpa = studentService.getStudentGpa("John");

        assertEquals(3.5, gpa, 0.001);
    }

    @Test
    void shouldIgnoreUngradedEnrollmentsWhenCalculatingGpa() {
        Student student = new Student();
        student.setFirstName("John");

        Course java = new Course();
        java.setCredits(3);

        Course math = new Course();
        math.setCredits(3);

        Enrollment gradedEnrollment = new Enrollment();
        gradedEnrollment.setCourse(java);
        gradedEnrollment.setGrade("A");

        Enrollment ungradedEnrollment = new Enrollment();
        ungradedEnrollment.setCourse(math);
        ungradedEnrollment.setGrade(null);

        when(studentRepository.findByFirstNameIgnoreCaseAndIsActive("John", true))
                .thenReturn(Optional.of(student));
        when(enrollmentRepository.findByStudent(student))
                .thenReturn(List.of(gradedEnrollment, ungradedEnrollment));

        double gpa = studentService.getStudentGpa("John");

        assertEquals(4.0, gpa, 0.001);
    }

    @Test
    void shouldThrowNotFoundWhenStudentDoesNotExist() {
        when(studentRepository.findByFirstNameIgnoreCaseAndIsActive("Unknown", true))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> studentService.getStudentGpa("Unknown"));
    }

    @Test
    void shouldThrowBusinessRuleExceptionForInvalidGrade() {
        Student student = new Student();
        student.setFirstName("John");

        Course course = new Course();
        course.setCredits(3);

        Enrollment enrollment = new Enrollment();
        enrollment.setCourse(course);
        enrollment.setGrade("X");

        when(studentRepository.findByFirstNameIgnoreCaseAndIsActive("John", true))
                .thenReturn(Optional.of(student));
        when(enrollmentRepository.findByStudent(student))
                .thenReturn(List.of(enrollment));

        assertThrows(BusinessRuleException.class, () -> studentService.getStudentGpa("John"));
    }
}
