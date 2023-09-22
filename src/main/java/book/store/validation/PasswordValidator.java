package book.store.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class PasswordValidator implements ConstraintValidator<Password, String> {
    private static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])"
            + "(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        return Pattern.compile(PASSWORD_PATTERN).matcher(value).matches();
    }
}
