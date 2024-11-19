package br.com.auto.bot.auth.validation.cpf.impl;

import br.com.auto.bot.auth.repository.UserRepository;
import br.com.auto.bot.auth.validation.cpf.IsValidCpf;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidCpfExists implements ConstraintValidator<IsValidCpf, String> {

    private UserRepository repository;

    public ValidCpfExists(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public void initialize(IsValidCpf constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String cpf, ConstraintValidatorContext constraintValidatorContext) {
       return isCPFValido(cpf);
    }

    protected boolean isCPFValido(String cpf) {
        if (cpf.length() == 11) {
            char digDez = cpf.charAt(9);
            char digOnze = cpf.charAt(10);
            boolean numerosValidos;
            try {
                char digDezCalculado = calcularDigitoVerificador(cpf, 10);
                char digOnzeCalculado = calcularDigitoVerificador(cpf, 11);
                numerosValidos = digDez == digDezCalculado && digOnze == digOnzeCalculado;
            } catch (Exception var6) {
                numerosValidos = false;
            }
                return numerosValidos;
        } else {
            return false;
        }
    }
    protected static char calcularDigitoVerificador(String cpf, int digito) {
            int soma = 0;
            int peso = digito;
            int diferenca = 11 - cpf.length();
            int resto;
            for(resto = 0; resto < diferenca; ++resto) {
                --peso;
            }
            for(resto = diferenca; resto < digito - 1; ++resto) {
                int digitoAtual = cpf.charAt(resto - diferenca) - 48;
                soma += digitoAtual * peso;
                --peso;
            }
            resto = 11 - soma % 11;
            return resto != 10 && resto != 11 ? (char)(resto + 48) : '0';
    }
}
