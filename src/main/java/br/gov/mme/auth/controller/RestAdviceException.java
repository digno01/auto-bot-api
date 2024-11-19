package br.gov.mme.auth.controller;

import br.gov.mme.auth.exceptions.BusinessException;
import br.gov.mme.auth.exceptions.RegistroDuplicadoException;
import br.gov.mme.auth.exceptions.RegistroNaoEncontradoException;
import br.gov.mme.auth.model.error.ErrorHandleDTO;
import br.gov.mme.auth.model.error.MessageDTO;
import com.google.gson.Gson;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class RestAdviceException {

    @Autowired
    private MessageSource messageSource;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public List<ErrorHandleDTO> handleCamposInvalidos(MethodArgumentNotValidException e) {
        List<FieldError> f= e.getBindingResult().getFieldErrors();

        List<ErrorHandleDTO> listError = new ArrayList<>();
        f.forEach(err ->
                listError.add(new ErrorHandleDTO(err.getField(), err.getDefaultMessage()))
        );

        return listError;
    }

    @ExceptionHandler(RegistroDuplicadoException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public List<ErrorHandleDTO> handleResgistroDuplicadoEncontrado(RegistroDuplicadoException e) {
        Gson gson = new Gson();
        return List.of(gson.fromJson(e.getMessage(), ErrorHandleDTO[].class));
    }


    @ExceptionHandler(RegistroNaoEncontradoException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public MessageDTO handleRegistroNaoEncontrado(RegistroNaoEncontradoException e) {
        return new MessageDTO(e.getMessage());

    }

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public MessageDTO handleBusinessException(BusinessException e) {
        return new MessageDTO(e.getMessage());
    }

    @ExceptionHandler(ExpiredJwtException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public MessageDTO handleBusinessException(ExpiredJwtException e) {
        return new MessageDTO(e.getMessage());
    }



}