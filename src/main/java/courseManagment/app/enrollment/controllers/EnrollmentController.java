package courseManagment.app.enrollment.controllers;

import courseManagment.app.course.entity.Course;
import courseManagment.app.course.repository.CourseRepository;
import courseManagment.app.enrollment.dto.CreateEnrollmentDTO;
import courseManagment.app.enrollment.dto.DeleteStudentFromCourseDTO;
import courseManagment.app.enrollment.services.EnrollmentService;
import courseManagment.app.student.entity.Student;
import courseManagment.app.student.repository.StudentRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;
    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;

    public EnrollmentController(EnrollmentService enrollmentService, CourseRepository courseRepository, StudentRepository studentRepository) {
        this.enrollmentService = enrollmentService;
        this.courseRepository = courseRepository;
        this.studentRepository = studentRepository;
    }

    @PostMapping("/create/enrollment")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER') or @authorizationService.isCurrentStudent(#dto.student)")
    public ResponseEntity<?> addStudentToCourse(@Valid @RequestBody CreateEnrollmentDTO dto) {
        String message = enrollmentService.addEnrollment(dto);
        return new ResponseEntity<>(message, HttpStatus.CREATED);
    }

    @DeleteMapping("/delete/enrollment")
    @PreAuthorize("hasAnyRole('ADMIN') or @authorizationService.isCurrentStudent(#dto.student)")
    public ResponseEntity<?> deleteStudentFromCourse(@Valid @RequestBody DeleteStudentFromCourseDTO dto) {
        Optional<Course> tempCourse = courseRepository.findByName(dto.getCourse());
        Optional <Student> tempStudent = studentRepository.findByFirstNameIgnoreCaseAndIsActive(dto.getStudent(), true);

        Course crs = tempCourse.get();
        Student std = tempStudent.get();
        String message = enrollmentService.deleteEnrollment(crs, std);
        return new ResponseEntity<>(message, HttpStatus.OK);
    }
}
