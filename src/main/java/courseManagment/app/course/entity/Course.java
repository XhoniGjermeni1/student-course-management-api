package courseManagment.app.course.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import courseManagment.app.enrollment.entity.Enrollment;
import courseManagment.app.user.entity.User;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(
        name = "courses",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_courses_name", columnNames = "name")
        }
)
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(java.sql.Types.VARCHAR)
    @Column(name = "id")
    private UUID id;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "capacity", nullable = false)
    private int capacity;

    @Column(name = "credits", nullable = false)
    private int credits;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id")
    @JsonManagedReference
    private User teacher;

    @OneToMany(mappedBy = "course" , cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Enrollment> enrollments = new ArrayList<>();

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public User getTeacher() {
        return teacher;
    }

    public void setTeacher(User teacher) {
        this.teacher = teacher;
    }

    public List<Enrollment> getEnrollments() {
        return enrollments;
    }

    public void setEnrollments(List<Enrollment> enrollments) {
        this.enrollments = enrollments;
    }

    public Course() {
    }

    public Course(String name, int capacity, int credits) {
        this.name = name;
        this.capacity = capacity;
        this.credits = credits;
    }

    @Override
    public String toString() {
        return "Course{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", capacity=" + capacity +
                ", credits=" + credits +
                '}';
    }

    public void addEnrollment(Enrollment enrollment) {
        enrollments.add(enrollment);
        enrollment.setCourse(this);
    }

    public void removeEnrollment(Enrollment enrollment) {
        enrollments.remove(enrollment);
        enrollment.setCourse(null);
    }
}
