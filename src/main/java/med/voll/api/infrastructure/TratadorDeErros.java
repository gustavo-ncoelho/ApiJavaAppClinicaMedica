package med.voll.api.infrastructure;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice  // <- Notação que diz pro spring que esse será um recurso voltado para as Controllers
public class TratadorDeErros {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity tratarErro404(){
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)                           // Vários erros de validação podem cair nesta exception, por isso deve-se fazer
    public ResponseEntity tratarErro400(MethodArgumentNotValidException exception){    // com que ela devolva cada um deles com nome do campo e mensagem. Usa-se um dto para isso.
        var erros = exception.getFieldErrors();    // <- Aqui será salvo na variável uma lista com o campo errors da exception lançada
        return ResponseEntity.badRequest().body(erros.stream().map(DadosErroValidacao::new).toList());
    }                                        // ^ aqui é feita a conversão da lista de errors, para o DTO de erros de validacao
                                             //   que contém somente o campo e a mensagem

    private record DadosErroValidacao(String campo, String mensagem){   // <- Esse é o DTO do erro de validação
        public DadosErroValidacao(FieldError erro){
            this(erro.getField(), erro.getDefaultMessage());
        }
    }

}
