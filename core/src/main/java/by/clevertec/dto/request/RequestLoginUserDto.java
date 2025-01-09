package by.clevertec.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RequestLoginUserDto {
    @NotEmpty
    @Size(min = 4, max = 30, message = "Username should be between 4 and 30 character")
    private String username;
    @Size(min = 2, max = 15, message = "Password should be between 2 and 15 character")
    private String password;
}
