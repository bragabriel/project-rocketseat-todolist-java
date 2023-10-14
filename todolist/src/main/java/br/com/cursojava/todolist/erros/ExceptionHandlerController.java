package br.com.cursojava.todolist.erros;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice //Usado para definir classes globais no tratamento de exceções. Toda exception passa por esse controller, e se for do tipo que queremos, ele trata
public class ExceptionHandlerController {

    //Toda exception desse tipo vai passar por aqui antes de retornar ao usuário
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handHttpMessageNotReadableException(HttpMessageNotReadableException e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMostSpecificCause().getMessage());
    }
}
