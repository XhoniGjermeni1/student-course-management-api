package courseManagment.app.course.controllers;

import courseManagment.app.course.dto.AssignTeacherDTO;
import courseManagment.app.course.dto.CreateCourseDTO;
import courseManagment.app.course.dto.UpdateCourseDTO;
import courseManagment.app.course.entity.Course;
import courseManagment.app.course.services.CourseService;
import courseManagment.app.student.entity.Student;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
public class CourseController {
    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @PostMapping("/create/course")
    public ResponseEntity<String> createCourse(@RequestBody CreateCourseDTO dto) {
        String message = courseService.createCourse(dto);
        return new ResponseEntity<>(message, HttpStatus.CREATED);
    }

    @PutMapping("/update/course")
    public ResponseEntity<String> updateCourse(@RequestBody UpdateCourseDTO dto, @RequestBody String oldName) {
        String message = courseService.updateCourse(dto, oldName);
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @PutMapping("/assign")
    public ResponseEntity<?> assignCourse(@RequestBody AssignTeacherDTO dto ) {
        Course updatedCourse = courseService.assignTeacher(dto);
        return ResponseEntity.ok(updatedCourse);
    }

    @GetMapping("/all/courses")
    public ResponseEntity<?> getAllCourses() {
        List<Course> courses = courseService.findAllCourses();
        return ResponseEntity.ok(courses);
    }

    @DeleteMapping("noStudent/{course}")
    public ResponseEntity<?> deleteCourse(@PathVariable String course) {
        courseService.deleteCourse(course);
        return ResponseEntity.ok().build();
    }

    @GetMapping("{course}/students")
    public ResponseEntity<?> getAllStudents(@PathVariable String course) {
        List<Student> students = courseService.getCourseWithStudents(course);
        return ResponseEntity.ok("Course: ***"+ course + "*** students: " + students);
    }
}
