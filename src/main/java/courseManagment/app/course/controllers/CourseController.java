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
        try{
            String message = courseService.createCourse(dto);
            return new ResponseEntity (message, HttpStatus.CREATED);
        }catch(Exception e){
            return new ResponseEntity("Gabim "+ e.getMessage() ,HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/update/course")
    public ResponseEntity<String> updateCourse(@RequestBody UpdateCourseDTO dto, @RequestBody String oldName) {
        try{
            String message = courseService.updateCourse(dto, oldName);
            return new ResponseEntity (message, HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity("Gabim--- "+ e.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/assign")
    public ResponseEntity<?> assignCourse(@RequestBody AssignTeacherDTO dto ) {
        try {
            Course updatedCourse = courseService.assignTeacher(dto);
            return ResponseEntity.ok(updatedCourse);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Gabim: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ndodhi një gabim teknik.");
        }
    }

    @GetMapping("/all/courses")
    public ResponseEntity<?> getAllCourses() {
        try{
            List<Course> courses = courseService.findAllCourses();
            return ResponseEntity.ok( courses);
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Nuk u gjet asnje kurs!");
        }
    }

    @DeleteMapping("noStudent/{course}")
    public ResponseEntity<?> deleteCourse(@PathVariable String course) {
        try{
            courseService.deleteCourse(course);
            return ResponseEntity.ok().build();
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("{course}/students")
    public ResponseEntity<?> getAllStudents(@PathVariable String course) {
        try {
           List<Student> students =  courseService.getCourseWithStudents(course);
            return ResponseEntity.ok("Course: ***"+ course + "*** students: " + students);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }
}
