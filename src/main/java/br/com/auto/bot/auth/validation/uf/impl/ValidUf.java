package br.com.auto.bot.auth.validation.uf.impl;

import br.com.auto.bot.auth.dto.enums.UnidadeFederativaEnum;
import br.com.auto.bot.auth.validation.uf.IsValidUf;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;

public class ValidUf implements ConstraintValidator<IsValidUf, String> {

    @Override
    public void initialize(IsValidUf constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String uf, ConstraintValidatorContext constraintValidatorContext) {
       return Arrays.stream(UnidadeFederativaEnum.values()).map(UnidadeFederativaEnum::getSigla).anyMatch(uf::equals);
    }
}
