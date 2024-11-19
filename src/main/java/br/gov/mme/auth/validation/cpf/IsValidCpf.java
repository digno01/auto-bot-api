package br.gov.mme.auth.validation.cpf;

import br.gov.mme.auth.validation.cpf.impl.ValidCpfExists;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = ValidCpfExists.class)
public @interface IsValidCpf {

    public String message() default "CPF inválido.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
