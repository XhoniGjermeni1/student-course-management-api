package courseManagment.app.course.services;

import courseManagment.app.course.dto.AssignTeacherDTO;
import courseManagment.app.course.dto.CreateCourseDTO;
import courseManagment.app.course.dto.UpdateCourseDTO;
import courseManagment.app.course.entity.Course;
import courseManagment.app.course.repository.CourseRepository;
import courseManagment.app.enrollment.entity.Enrollment;
import courseManagment.app.enrollment.repository.EnrollmentRepository;
import courseManagment.app.student.entity.Student;
import courseManagment.app.student.repository.StudentRepository;
import courseManagment.app.user.entity.Role;
import courseManagment.app.user.entity.User;
import courseManagment.app.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.View;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CourseService {

    private final View error;
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository, View error, UserRepository userRepository, EnrollmentRepository enrollmentRepository, StudentRepository studentRepository) {
        this.courseRepository = courseRepository;
        this.error = error;
        this.userRepository = userRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.studentRepository = studentRepository;
    }

    @Transactional
    public String createCourse(CreateCourseDTO dto) {
        if (courseRepository.existsByName(dto.getName().trim())) {
            return "Course already exists: " + dto.getName();
        }

        Course course = new Course();
        course.setName(dto.getName());
        course.setCapacity(dto.getCapacity());
        course.setCredits(dto.getCredits());

        courseRepository.save(course);

        return "Course created: " + course;
    }

    public Optional<Course> findCourseByName(String name) {
       Optional<Course> course = courseRepository.findByName(name);
       return course;
    }

    @Transactional
    public String updateCourse(UpdateCourseDTO dto, String name) {
        Course course = courseRepository.findByName(name)
                .orElseThrow(()-> new RuntimeException ("Course with name *"+ name +"* not found"));
//           Course updatedCourse = course.get(); //zberthen optional dhe nxjerr prej aty objektin Course
//  Nuk i bejme merge sepse kur i bejme set hibernate i ben update
//  vete ne databaze pa u dashur ne ti japim komanden merge per update
            course.setName(dto.getName());
            course.setCapacity(dto.getCapacity());
            course.setCredits(dto.getCredits());
            courseRepository.save(course);
            return "Course updated: " + course;

        }
        @Transactional
        public Course assignTeacher(AssignTeacherDTO dto) {
            Course course = courseRepository.findByName(dto.getCourse())
                    .orElseThrow(() -> new RuntimeException("Kursi '" + dto.getCourse() + "' nuk u gjet!"));
            User teacher = userRepository.findByUsernameIgnoreCase(dto.getTeacher())
                    .orElseThrow(() -> new RuntimeException("Mësuesi '" + dto.getTeacher() + "' nuk u gjet!"));
            if (teacher.getRole() != Role.TEACHER) {
                throw new RuntimeException("Përdoruesi " + dto.getTeacher() + " nuk mund të caktohet si mësues sepse ka rolin: " + teacher.getRole());
            }
            course.setTeacher(teacher);
            return course;
        }

        public List<Course> findAllCourses() {
        return courseRepository.findAll();
        }

        @Transactional
        public void deleteCourse(String name) {
        Course course = courseRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Course with name *"+ name +"* not found"));

            List<Enrollment> enrollment = enrollmentRepository.findByCourse(course);
            if (!enrollment.isEmpty()) {
                throw new RuntimeException("Kursi '" + name + "' nuk mund te fshihet!");
            }else
                courseRepository.delete(course);
        }

        public List<Student> getCourseWithStudents(String name){
            Course course = courseRepository.findByName(name)
                    .orElseThrow(() -> new RuntimeException("Course with name *"+ name +"* not found"));

            List<Student> students = studentRepository.findByCourse(course.getName());
            return students;
    }
}
