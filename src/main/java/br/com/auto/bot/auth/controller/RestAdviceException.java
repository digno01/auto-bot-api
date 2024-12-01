package br.com.auto.bot.auth.controller;

import br.com.auto.bot.auth.exceptions.BusinessException;
import br.com.auto.bot.auth.model.error.ErrorHandleDTO;
import br.com.auto.bot.auth.model.error.MessageDTO;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.validation.UnexpectedTypeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
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

    @ExceptionHandler(UnexpectedTypeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public List<ErrorHandleDTO> handleCamposInvalidos(UnexpectedTypeException e) {
        return  List.of(new ErrorHandleDTO("invalido", "Formulario contém erros reveja os campos informados"));
    }


    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorHandleDTO handleCamposInvalidos(HttpMessageNotReadableException e) {
        return new ErrorHandleDTO(e.getMessage());
    }


    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public MessageDTO handleBusinessException(BusinessException e) {
        return new MessageDTO(e.getMessage().toString());
    }



    @ExceptionHandler(ExpiredJwtException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public MessageDTO handleBusinessException(ExpiredJwtException e) {
        return new MessageDTO(e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public MessageDTO handleRuntimeException(RuntimeException e) {
        return new MessageDTO(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public MessageDTO handleRuntimeException(Exception e) {
        return new MessageDTO(e.getMessage());
    }


}