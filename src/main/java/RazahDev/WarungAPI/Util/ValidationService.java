package RazahDev.WarungAPI.Util;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class ValidationService {

    private final Validator validator;

    public void validate(Object request)
    {
        Set<ConstraintViolation<Object>> validate = validator.validate(request);
        if (!validate.isEmpty())
        {
            throw new ConstraintViolationException(validate);
        }
    }
}
