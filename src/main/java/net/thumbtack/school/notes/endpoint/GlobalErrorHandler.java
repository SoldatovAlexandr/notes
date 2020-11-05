package net.thumbtack.school.notes.endpoint;

import net.thumbtack.school.notes.erroritem.code.ServerErrorCode;
import net.thumbtack.school.notes.erroritem.code.ServerErrorCodeWithField;
import net.thumbtack.school.notes.erroritem.dto.ErrorDtoContainer;
import net.thumbtack.school.notes.erroritem.dto.ErrorDtoItem;
import net.thumbtack.school.notes.erroritem.exception.ServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

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

        //TODO: PASSWORDS DO NOT MATH
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


}
