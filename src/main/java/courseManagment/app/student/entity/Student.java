package courseManagment.app.student.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import courseManagment.app.enrollment.entity.Enrollment;
import courseManagment.app.user.entity.User;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
//import org.springframework.security.core.userdetails.User;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "students")
public class Student {
    @Id
    @JdbcTypeCode(java.sql.Types.VARCHAR)
    @Column(name = "user_id")
    private UUID id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @OneToOne(cascade = CascadeType.ALL)
    @MapsId
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User user;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    private List<Enrollment> enrollments = new ArrayList<>();

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public List<Enrollment> getEnrollments() {
        return enrollments;
    }

    public void setEnrollments(List<Enrollment> enrollments) {
        this.enrollments = enrollments;
    }

    public Student() {
    }

    public Student(String firstName, String lastName, Boolean isActive) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.isActive = isActive;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", isActive=" + isActive +
                '}';
    }

    public void addEnrollment(Enrollment enrollment) {
        enrollments.add(enrollment);
        enrollment.setStudent(this);
    }

    public void removeEnrollment(Enrollment enrollment) {
        enrollments.remove(enrollment);
        enrollment.setStudent(null);
    }
}
