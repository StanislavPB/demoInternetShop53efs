package org.demointernetshop53efs.service.validation;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class ValidationExceptionHandler {

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorsDto> handlerValidationException(MethodArgumentNotValidException e){
        List<ValidationErrorDto> validationErrors = new ArrayList<>();

        List<ObjectError> errors = e.getBindingResult().getAllErrors();

        for (ObjectError error : errors){
            FieldError fieldError = (FieldError) error;
            ValidationErrorDto errorDto = ValidationErrorDto.builder()
                    .field(fieldError.getField())
                    .message(fieldError.getDefaultMessage())
                    .build();

            if (fieldError.getRejectedValue() != null) {
                errorDto.setRejectedValue(fieldError.getRejectedValue().toString());
            }
            validationErrors.add(errorDto);
        }

        return ResponseEntity
                .badRequest()
                .body(ValidationErrorsDto.builder()
                        .errors(validationErrors)
                        .build());

     }

     @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, String>> handlerMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e){
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error","Invalid parameter type");
        errorResponse.put("parameter",e.getName());
        errorResponse.put("message",e.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
     }

}
