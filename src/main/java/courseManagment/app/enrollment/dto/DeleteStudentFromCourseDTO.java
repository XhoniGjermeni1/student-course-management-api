package courseManagment.app.enrollment.dto;

public class DeleteStudentFromCourseDTO {
    private String course;
    private String student;

    public DeleteStudentFromCourseDTO() {
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
