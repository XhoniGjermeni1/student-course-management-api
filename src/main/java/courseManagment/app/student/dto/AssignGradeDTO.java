package courseManagment.app.student.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class AssignGradeDTO {

    @NotBlank(message = "Grade is required")
    @Pattern(regexp = "(?i)^(A|B|C|D|F)$", message = "Grade must be one of: A, B, C, D, F")
    private String grade;

    public AssignGradeDTO() {
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }
}
