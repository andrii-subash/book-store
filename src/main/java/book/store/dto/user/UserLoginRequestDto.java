package book.store.dto.user;

import book.store.validation.Email;
import book.store.validation.Password;
import lombok.Data;

@Data
public class UserLoginRequestDto {
    @Email
    private String email;
    @Password
    private String password;
}
