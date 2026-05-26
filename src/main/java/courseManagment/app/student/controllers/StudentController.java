package courseManagment.app.student.controllers;

import courseManagment.app.course.entity.Course;
import courseManagment.app.course.repository.CourseRepository;
import courseManagment.app.enrollment.entity.Enrollment;
import courseManagment.app.student.dto.AssignGradeDTO;
import courseManagment.app.student.entity.Student;
import courseManagment.app.student.repository.StudentRepository;
import courseManagment.app.student.services.StudentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/students")
public class StudentController {

private final StudentService studentService;
    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;

    public StudentController(StudentService studentService, CourseRepository courseRepository, StudentRepository studentRepository) {
    this.studentService = studentService;
        this.courseRepository = courseRepository;
        this.studentRepository = studentRepository;
    }

    @GetMapping("/{student}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER') or @authorizationService.isCurrentStudent(#student)")
    public ResponseEntity<?> getStudentCourses(@PathVariable String student) {
        try{
            Student student1 = studentRepository.findByFirstNameIgnoreCaseAndIsActive(student, true)
                    .orElseThrow(()-> new RuntimeException("Studenti nuk u gjet!"));
       String message = studentService.getStudentWithEnrolledCourses(student1);
       return ResponseEntity.ok(message);
        }catch (RuntimeException e) {
            // Kjo do të kthejë "Studenti nuk u gjet!" me status 404 ose 400
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/paginated/students")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<?>  getStudentsPaginated(@RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "10") int size,
                                                   @RequestParam(required = false) String name){
        Pageable pageable = PageRequest.of(page, size);
        Page<Student> studentPage = studentService.getFilteredStudents(name, pageable);
        return ResponseEntity.ok(studentPage);
    }

    @DeleteMapping("/noEnrollment/{student}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<?> deleteStudent(@PathVariable String student) {
        try {
            studentService.deleteStudentIfNoEnrolled(student);
            return ResponseEntity.ok().build();
        }catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("grade/{student}/{course}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<?> updateStudentGrade(@PathVariable String student, @PathVariable String course, @RequestBody AssignGradeDTO dto) {
        try{
            studentService.assignGradeToStudent(dto, student, course);
            return ResponseEntity.ok("U vendos me sukses");
        }catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("{student}/enrollments")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER') or @authorizationService.isCurrentStudent(#student)")
    public ResponseEntity<?> getStudentEnrollments(@PathVariable String student) {
        try{
            List<Enrollment> enrollment = studentService.listEnrollmentsForStudent(student);
            return ResponseEntity.ok("Student: " + student + "\n *****Enrollments: *****" + enrollment);
        }catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/{student}/gpa")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER') or @authorizationService.isCurrentStudent(#student)")
    public ResponseEntity<?> getStudentGPA(@PathVariable String student) {
        try{
       double gpa = studentService.getStudentGpa(student);
       return ResponseEntity.ok("GPA: " + gpa);
        }catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
