package courseManagment.app.enrollment.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateEnrollmentDTO {
    @NotBlank(message = "Course is required")
    private String course;
    @NotBlank(message = "Student is required")
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
