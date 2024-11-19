package br.com.auto.bot.auth.validation.email;

import br.com.auto.bot.auth.validation.email.impl.EmailExists;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = EmailExists.class)
public @interface IsEmailExists {

    public String message() default "E-mail já cadastrado.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
