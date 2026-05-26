package courseManagment.app.enrollment.controllers;

import courseManagment.app.course.entity.Course;
import courseManagment.app.course.repository.CourseRepository;
import courseManagment.app.enrollment.dto.CreateEnrollmentDTO;
import courseManagment.app.enrollment.dto.DeleteStudentFromCourseDTO;
import courseManagment.app.enrollment.entity.Enrollment;
import courseManagment.app.enrollment.services.EnrollmentService;
import courseManagment.app.student.entity.Student;
import courseManagment.app.student.repository.StudentRepository;
import courseManagment.app.user.entity.User;
import courseManagment.app.user.repository.UserRepository;
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
public ResponseEntity<?> addStudentToCourse(@RequestBody CreateEnrollmentDTO dto) {
    try{
        String message = enrollmentService.addEnrollment(dto);
        return new ResponseEntity<>(message, HttpStatus.CREATED);
    }catch(Exception e){
        return ResponseEntity.badRequest().body("Gabim--- "+ e.getMessage());
    }
}

    @DeleteMapping("/delete/enrollment")
    @PreAuthorize("hasAnyRole('ADMIN') or @authorizationService.isCurrentStudent(#dto.student)")
    public ResponseEntity<?> deleteStudentFromCourse(@RequestBody DeleteStudentFromCourseDTO dto) {
        try{
            Optional<Course> tempCourse = courseRepository.findByName(dto.getCourse());
            Optional <Student> tempStudent = studentRepository.findByFirstNameIgnoreCaseAndIsActive(dto.getStudent(), true);

            Course crs = tempCourse.get();
            Student std = tempStudent.get();
            String message = enrollmentService.deleteEnrollment(crs, std);
            return new ResponseEntity<>(message, HttpStatus.OK);
        }catch(Exception e){
            return ResponseEntity.badRequest().body("Gabim--- "+ e.getMessage());
        }
}
}
