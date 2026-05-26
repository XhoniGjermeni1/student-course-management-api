package courseManagment.app.security;

import courseManagment.app.student.entity.Student;
import courseManagment.app.student.repository.StudentRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service("authorizationService")
public class AuthorizationService {

    private final StudentRepository studentRepository;

    public AuthorizationService(StudentRepository studentRepository){
        this.studentRepository = studentRepository;
    }

    public boolean isCurrentStudent(String studentName){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication == null || !authentication.isAuthenticated()){
            return false;
        }

        String username = authentication.getName();
        Student student = studentRepository.findByFirstNameIgnoreCaseAndIsActive(studentName, true)
                .orElse(null);
        if (student == null || student.getUser() == null){
            return false;
        }
        return student.getUser().getUsername().equalsIgnoreCase(username);
    }
}
