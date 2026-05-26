package courseManagment.app.course.dto;

import courseManagment.app.user.entity.User;

public class AssignTeacherDTO {
private String course;
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
