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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EnrollmentServiceTest {

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private EnrollmentService enrollmentService;

    @Test
    void shouldAddEnrollmentSuccessfully() {
        CreateEnrollmentDTO dto = createEnrollmentDto("Java", "John");
        Course course = createCourse("Java", 2);
        Student student = createStudent("John");

        when(courseRepository.findByName("Java")).thenReturn(Optional.of(course));
        when(studentRepository.findByFirstNameIgnoreCaseAndIsActive("John", true)).thenReturn(Optional.of(student));
        when(enrollmentRepository.existsByStudentAndCourse(student, course)).thenReturn(false);
        when(enrollmentRepository.countByCourse(course)).thenReturn(0);

        String response = enrollmentService.addEnrollment(dto);

        assertTrue(response.contains("Regjistrimi u krye me sukses"));
        verify(enrollmentRepository).save(any(Enrollment.class));
    }

    @Test
    void shouldThrowBusinessRuleExceptionWhenEnrollmentAlreadyExists() {
        CreateEnrollmentDTO dto = createEnrollmentDto("Java", "John");
        Course course = createCourse("Java", 2);
        Student student = createStudent("John");

        when(courseRepository.findByName("Java")).thenReturn(Optional.of(course));
        when(studentRepository.findByFirstNameIgnoreCaseAndIsActive("John", true)).thenReturn(Optional.of(student));
        when(enrollmentRepository.existsByStudentAndCourse(student, course)).thenReturn(true);

        assertThrows(BusinessRuleException.class, () -> enrollmentService.addEnrollment(dto));
        verify(enrollmentRepository, never()).save(any(Enrollment.class));
    }

    @Test
    void shouldThrowBusinessRuleExceptionWhenCourseIsFull() {
        CreateEnrollmentDTO dto = createEnrollmentDto("Java", "John");
        Course course = createCourse("Java", 1);
        Student student = createStudent("John");

        when(courseRepository.findByName("Java")).thenReturn(Optional.of(course));
        when(studentRepository.findByFirstNameIgnoreCaseAndIsActive("John", true)).thenReturn(Optional.of(student));
        when(enrollmentRepository.existsByStudentAndCourse(student, course)).thenReturn(false);
        when(enrollmentRepository.countByCourse(course)).thenReturn(1);

        assertThrows(BusinessRuleException.class, () -> enrollmentService.addEnrollment(dto));
        verify(enrollmentRepository, never()).save(any(Enrollment.class));
    }

    @Test
    void shouldThrowNotFoundWhenCourseDoesNotExist() {
        CreateEnrollmentDTO dto = createEnrollmentDto("Java", "John");

        when(courseRepository.findByName("Java")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> enrollmentService.addEnrollment(dto));
    }

    private CreateEnrollmentDTO createEnrollmentDto(String courseName, String studentName) {
        CreateEnrollmentDTO dto = new CreateEnrollmentDTO();
        dto.setCourse(courseName);
        dto.setStudent(studentName);
        return dto;
    }

    private Course createCourse(String name, int capacity) {
        Course course = new Course();
        course.setName(name);
        course.setCapacity(capacity);
        course.setCredits(3);
        return course;
    }

    private Student createStudent(String firstName) {
        Student student = new Student();
        student.setFirstName(firstName);
        student.setLastName("Doe");
        student.setActive(true);
        return student;
    }
}
