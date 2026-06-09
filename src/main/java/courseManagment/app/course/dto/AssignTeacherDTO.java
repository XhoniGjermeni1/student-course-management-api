package courseManagment.app.course.dto;

import jakarta.validation.constraints.NotBlank;

public class AssignTeacherDTO {
    @NotBlank(message = "Course is required")
    private String course;
    @NotBlank(message = "Teacher name is required")
    private String teacher;

    public AssignTeacherDTO() {
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }
}
