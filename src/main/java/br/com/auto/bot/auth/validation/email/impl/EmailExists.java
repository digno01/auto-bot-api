package br.com.auto.bot.auth.validation.email.impl;

import br.com.auto.bot.auth.model.User;
import br.com.auto.bot.auth.repository.UserRepository;
import br.com.auto.bot.auth.validation.email.IsEmailExists;
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
