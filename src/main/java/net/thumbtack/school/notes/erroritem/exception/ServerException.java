package net.thumbtack.school.notes.erroritem.exception;

import net.thumbtack.school.notes.erroritem.code.ServerErrorCodeWithField;

public class ServerException extends Exception {
    private final ServerErrorCodeWithField serverErrorCodeWithField;

    public ServerException(ServerErrorCodeWithField serverErrorCodeWithField) {
        this.serverErrorCodeWithField = serverErrorCodeWithField;
    }

    public ServerErrorCodeWithField getServerErrorCodeWithField() {
        return serverErrorCodeWithField;
    }
}
