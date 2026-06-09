package courseManagment.app.course.services;

import courseManagment.app.course.dto.AssignTeacherDTO;
import courseManagment.app.course.dto.CreateCourseDTO;
import courseManagment.app.course.dto.UpdateCourseDTO;
import courseManagment.app.course.entity.Course;
import courseManagment.app.course.repository.CourseRepository;
import courseManagment.app.enrollment.entity.Enrollment;
import courseManagment.app.enrollment.repository.EnrollmentRepository;
import courseManagment.app.exception.BusinessRuleException;
import courseManagment.app.exception.NotFoundException;
import courseManagment.app.student.entity.Student;
import courseManagment.app.student.repository.StudentRepository;
import courseManagment.app.user.entity.Role;
import courseManagment.app.user.entity.User;
import courseManagment.app.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;

    public CourseService(CourseRepository courseRepository, UserRepository userRepository, EnrollmentRepository enrollmentRepository, StudentRepository studentRepository) {
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.studentRepository = studentRepository;
    }

    @Transactional
    public String createCourse(CreateCourseDTO dto) {
        if (courseRepository.existsByName(dto.getName().trim())) {
            throw new BusinessRuleException("Course already exists: " + dto.getName());
        }

        Course course = new Course();
        course.setName(dto.getName());
        course.setCapacity(dto.getCapacity());
        course.setCredits(dto.getCredits());

        courseRepository.save(course);

        return "Course created: " + course;
    }

    public Optional<Course> findCourseByName(String name) {
        return courseRepository.findByName(name);
    }

    @Transactional
    public String updateCourse(UpdateCourseDTO dto) {
        Course course = courseRepository.findByName(dto.getOldName())
                .orElseThrow(() -> new NotFoundException("Course with name *" + dto.getOldName() + "* not found"));

        course.setName(dto.getName());
        course.setCapacity(dto.getCapacity());
        course.setCredits(dto.getCredits());
        courseRepository.save(course);

        return "Course updated: " + course;
    }

    @Transactional
    public Course assignTeacher(AssignTeacherDTO dto) {
        Course course = courseRepository.findByName(dto.getCourse())
                .orElseThrow(() -> new NotFoundException("Kursi '" + dto.getCourse() + "' nuk u gjet!"));

        User teacher = userRepository.findByUsernameIgnoreCase(dto.getTeacher())
                .orElseThrow(() -> new NotFoundException("Mesuesi '" + dto.getTeacher() + "' nuk u gjet!"));

        if (teacher.getRole() != Role.TEACHER) {
            throw new BusinessRuleException("Perdoruesi " + dto.getTeacher() + " nuk mund te caktohet si mesues sepse ka rolin: " + teacher.getRole());
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
                .orElseThrow(() -> new NotFoundException("Course with name *" + name + "* not found"));

        List<Enrollment> enrollment = enrollmentRepository.findByCourse(course);
        if (!enrollment.isEmpty()) {
            throw new BusinessRuleException("Kursi '" + name + "' nuk mund te fshihet!");
        }

        courseRepository.delete(course);
    }

    public List<Student> getCourseWithStudents(String name) {
        Course course = courseRepository.findByName(name)
                .orElseThrow(() -> new NotFoundException("Course with name *" + name + "* not found"));

        return studentRepository.findByCourse(course.getName());
    }
}
