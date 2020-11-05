package net.thumbtack.school.notes.erroritem.code;

public enum ServerErrorCodeWithField {
    WRONG_ID("wrong id", "Id"),
    NO_PERMISSIONS("you are not allowed to perform this action", "cookie"),
    UNAUTHORIZED_ACCESS("login required", "cookie"),
    LOGIN_NOT_EXIST("login not exist", "login"),
    INCORRECT_PASSWORD("incorrect password", "password"),
    LOGIN_ALREADY_EXIST("login already exist", "login");

    private final String message;
    private final String field;

    ServerErrorCodeWithField(String message, String field) {
        this.message = message;
        this.field = field;
    }

    public String getMessage() {
        return message;
    }

    public String getField() {
        return field;
    }
}
