package br.gov.mme.auth.validation.email.impl;

import br.gov.mme.auth.model.User;
import br.gov.mme.auth.repository.UserRepository;
import br.gov.mme.auth.validation.email.IsEmailExists;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Optional;

public class EmailExists implements ConstraintValidator<IsEmailExists, String> {

    private UserRepository repository;

    public EmailExists(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public void initialize(IsEmailExists constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext constraintValidatorContext) {
       boolean isNotValid = true;
       Optional<User> opt = repository.findByEmail(email);
       if(opt.isPresent()){
           isNotValid = false;
       }
        return isNotValid;
    }
}
