package net.thumbtack.school.notes.erroritem.code;

public enum ServerErrorCode {
    FIRST_NAME_NOT_SET("first name must be set"),
    LAST_NAME_NOT_SET("last name must be set"),
    LOGIN_NOT_SET("login must be set"),
    PASSWORD_NOT_SET("password must be set"),
    NAME_NOT_SET("name must be set"),
    SECTION_ID_NOT_SET("sectionId must be set"),
    SUBJECT_NOT_SET("subject must be set"),
    BODY_NOT_SET("body must be set"),
    PARAMS_NOT_SET("at least one parameter must be set"),
    NOTE_ID_NOT_SET("noteId must be set"),
    RATING_NOT_SET("rating must be set"),

    INVALID_RATING("rating is incorrect"),
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
