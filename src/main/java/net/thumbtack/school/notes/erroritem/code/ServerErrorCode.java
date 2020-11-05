package net.thumbtack.school.notes.erroritem.code;

public enum ServerErrorCode {
    PASSWORDS_DO_MATCH("passwords must not math"),

    FIRST_NAME_NOT_SET("first name must be set"),
    LAST_NAME_NOT_SET("last name must be set"),
    LOGIN_NOT_SET("login must be set"),
    PASSWORD_NOT_SET("password must be set"),

    INVALID_LENGTH("length is incorrect"),
    INVALID_FIRST_NAME("first name is invalid"),
    INVALID_LAST_NAME("last name is invalid"),
    INVALID_LOGIN("login is invalid"),
    INVALID_PASSWORD("password is invalid"),
    INVALID_PATRONYMIC("patronymic is invalid");
    private final String message;

    ServerErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
