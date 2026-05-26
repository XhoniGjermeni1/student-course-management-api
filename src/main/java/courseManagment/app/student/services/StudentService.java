package courseManagment.app.student.services;

import courseManagment.app.course.entity.Course;
import courseManagment.app.course.repository.CourseRepository;
import courseManagment.app.enrollment.entity.Enrollment;
import courseManagment.app.enrollment.entity.Grade;
import courseManagment.app.enrollment.repository.EnrollmentRepository;
import courseManagment.app.student.dto.AssignGradeDTO;
import courseManagment.app.student.entity.Student;
import courseManagment.app.student.repository.StudentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;

    public StudentService(StudentRepository studentRepository, EnrollmentRepository enrollmentRepository, CourseRepository courseRepository) {
        this.studentRepository = studentRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.courseRepository = courseRepository;
    }

    public String getStudentWithEnrolledCourses(Student student) {

        Optional<Student> student1 = studentRepository.findByFirstNameIgnoreCaseAndIsActive(student.getFirstName(), true);
        Student tempStudent = student1.get();

        List<Enrollment> enrollments = enrollmentRepository.findByStudent(tempStudent);
        if (enrollments.isEmpty()) {
            return "Studenti " + tempStudent.getFirstName() + " nuk është i regjistruar në asnjë kurs.";
        }

        StringBuilder sb = new StringBuilder();
        for (Enrollment e : enrollments) {
            sb.append("[").append(e.getCourse().getName()).append("] ");
        }

        return "Kurset e studentit " + tempStudent.getFirstName() + " jane: " + sb.toString();
    }

    public Page<Student> getFilteredStudents(String name, Pageable pageable) {
        if (name != null && !name.trim().isEmpty()) {
            return studentRepository.findByFirstNameContainingIgnoreCase(name, pageable);
        } else {
            return studentRepository.findAll(pageable);
        }
    }

    @Transactional
    public void deleteStudentIfNoEnrolled(String name) {
        Student student1 = studentRepository.findByFirstNameIgnoreCaseAndIsActive(name, true)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        List<Enrollment> enrollments = enrollmentRepository.findByStudent(student1);
        if (!enrollments.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (Enrollment e : enrollments) {
                sb.append("[").append(e.getCourse().getName()).append("] ");

                throw new RuntimeException("Nuk mund ta fshish studentin " + name + " sepse eshte regjistruar ne kurset: "
                    + sb.toString());
        }
        studentRepository.delete(student1);
        }
    }

    @Transactional
    public void assignGradeToStudent (AssignGradeDTO dto, String student, String course) {
        Student tempStudent = studentRepository.findByFirstNameIgnoreCaseAndIsActive(student, true)
                .orElseThrow(()-> new RuntimeException("Student not found"));
        Course tempCourse = courseRepository.findByName(course)
                .orElseThrow(()-> new RuntimeException("Course not found"));
        String inputGrade = dto.getGrade().toUpperCase().trim();
        Grade validGrade;
        try {
        validGrade = Grade.valueOf(inputGrade);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Nota '" + inputGrade + "' nuk është e vlefshme! Lejohen vetëm: A, B, C, D, E, F.");
        }
        Enrollment enrollment = enrollmentRepository.findByStudentAndCourse(tempStudent, tempCourse)
                .orElseThrow(()-> new RuntimeException("Enrollment not found"));

        enrollment.setGrade(dto.getGrade().toUpperCase());
    }

    public List<Enrollment> listEnrollmentsForStudent(String student){
        Student tempStudent = studentRepository.findByFirstNameIgnoreCaseAndIsActive(student, true)
                .orElseThrow(()-> new RuntimeException("Student not found"));
        List<Enrollment> enrollment = enrollmentRepository.findByStudent(tempStudent);
               if (enrollment.isEmpty()) {
                   throw new RuntimeException("No enrollment for student " + student);
               }
               return enrollment;
    }

    public double getStudentGpa(String student) {
        Student tempStudent = studentRepository.findByFirstNameIgnoreCaseAndIsActive(student, true)
                .orElseThrow(()-> new RuntimeException("Student not found"));

        List<Enrollment> enrollments = enrollmentRepository.findByStudent(tempStudent);
        if (enrollments.isEmpty()) {
            throw new RuntimeException("No enrollment for student " + student);
        }
        double piket= 0.0;
        int nrKurseve = 0;
        for (Enrollment e : enrollments) {
            if(e.getGrade() != null){
                String Grade = e.getGrade().toUpperCase().trim();

                switch (Grade) {
                    case "A": piket +=4; break;
                    case "B": piket +=3; break;
                    case "C": piket +=2; break;
                    case "D": piket +=1; break;
                    default: continue;
                }
                nrKurseve++;
            }
        }
        if (nrKurseve == 0) {
            return 0.0;
        }
        return piket/nrKurseve;
    }
}
