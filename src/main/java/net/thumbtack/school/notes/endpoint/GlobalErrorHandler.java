package net.thumbtack.school.notes.endpoint;

import net.thumbtack.school.notes.erroritem.code.ServerErrorCode;
import net.thumbtack.school.notes.erroritem.code.ServerErrorCodeWithField;
import net.thumbtack.school.notes.erroritem.dto.ErrorDtoContainer;
import net.thumbtack.school.notes.erroritem.dto.ErrorDtoItem;
import net.thumbtack.school.notes.erroritem.exception.ServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class GlobalErrorHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalErrorHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorDtoContainer handleValidation(MethodArgumentNotValidException exc) {
        List<ErrorDtoItem> errorDtoItems = new ArrayList<>();

        exc.getBindingResult().getGlobalErrors().forEach(error -> {
            ServerErrorCodeWithField serverErrorCodeWithField =
                    ServerErrorCodeWithField.valueOf(error.getDefaultMessage());
            errorDtoItems.add(
                    new ErrorDtoItem(serverErrorCodeWithField.toString(),
                            serverErrorCodeWithField.getField(),
                            serverErrorCodeWithField.getMessage())
            );
        });

        exc.getBindingResult().getFieldErrors().forEach(error -> {
            ServerErrorCode serverErrorCode =
                    ServerErrorCode.valueOf(error.getDefaultMessage());
            errorDtoItems.add(
                    new ErrorDtoItem(
                            serverErrorCode.toString(),
                            error.getField(),
                            serverErrorCode.getMessage())
            );
        });
        return new ErrorDtoContainer(errorDtoItems);
    }


    @ExceptionHandler(ServerException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorDtoContainer handleValidation(ServerException e) {
        List<ErrorDtoItem> errorDtoItems = new ArrayList<>();

        ServerErrorCodeWithField serverErrorCodeWithField = e.getServerErrorCodeWithField();

        errorDtoItems.add(
                new ErrorDtoItem(serverErrorCodeWithField.toString(),
                        serverErrorCodeWithField.getField(),
                        serverErrorCodeWithField.getMessage())
        );

        return new ErrorDtoContainer(errorDtoItems);
    }


    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorDtoContainer paramsValidation(ConstraintViolationException e) {
        List<ErrorDtoItem> errorDtoItems = new ArrayList<>();

        e.getConstraintViolations().forEach(error -> {
            ServerErrorCodeWithField serverErrorCodeWithField =
                    ServerErrorCodeWithField.valueOf(error.getMessage());
            errorDtoItems.add(
                    new ErrorDtoItem(serverErrorCodeWithField.toString(),
                            serverErrorCodeWithField.getField(),
                            serverErrorCodeWithField.getMessage())
            );
        });

        return new ErrorDtoContainer(errorDtoItems);
    }

    @ExceptionHandler(MissingRequestCookieException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorDtoContainer cookieValidation(MissingRequestCookieException e) {
        List<ErrorDtoItem> errorDtoItems = new ArrayList<>();

        ServerErrorCodeWithField serverErrorCodeWithField = ServerErrorCodeWithField.COOKIE_MISSING;

        errorDtoItems.add(
                new ErrorDtoItem(serverErrorCodeWithField.toString(),
                        serverErrorCodeWithField.getField(),
                        serverErrorCodeWithField.getMessage())
        );

        return new ErrorDtoContainer(errorDtoItems);
    }

    @ExceptionHandler(ConversionFailedException.class)
    public ResponseEntity<String> handleConflict(RuntimeException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
