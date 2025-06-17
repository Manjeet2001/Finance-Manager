package finance_manager.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserSignUpRequest {
    @Email
    @NotBlank
    private String username;

    @Size(min = 6, max = 20)
    private String password;

    @NotBlank
    private String fullName;

    @Pattern(regexp = "^\\+?[0-9]{10,15}$")
    private String phoneNumber;
}
