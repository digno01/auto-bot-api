package br.gov.mme.auth.validation.uf;

import br.gov.mme.auth.validation.uf.impl.ValidUf;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = ValidUf.class)
public @interface IsValidUf {

    public String message() default "UF informada inválida";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
