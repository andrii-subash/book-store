package book.store.dto.user;

import book.store.validation.Email;
import book.store.validation.FieldsValueMatch;
import book.store.validation.Password;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@FieldsValueMatch(
        field = "password",
        fieldMatch = "repeatPassword",
        message = "Passwords do not match!")
@Data
public class UserRegistrationRequestDto {
    @Email
    private String email;
    @Password
    private String password;
    private String repeatPassword;
    @NotNull
    @Size(min = 3, max = 15)
    private String firstName;
    @NotNull
    @Size(min = 3, max = 15)
    private String lastName;
    @NotNull
    @Size(min = 7, max = 40)
    private String shippingAddress;
}
