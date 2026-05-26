package courseManagment.app.enrollment.dto;

import courseManagment.app.course.entity.Course;
import courseManagment.app.student.entity.Student;

public class CreateEnrollmentDTO {
    private String course;
    private String student;

    public CreateEnrollmentDTO() {
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getStudent() {
        return student;
    }

    public void setStudent(String student) {
        this.student = student;
    }
}
