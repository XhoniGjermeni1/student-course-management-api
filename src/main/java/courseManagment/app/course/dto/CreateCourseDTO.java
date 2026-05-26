package courseManagment.app.course.dto;

public class CreateCourseDTO {

    private String name;
    private int capacity;
    private int credits;

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

    public CreateCourseDTO() {
    }
}
