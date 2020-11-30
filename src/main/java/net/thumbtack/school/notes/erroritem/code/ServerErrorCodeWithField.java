package net.thumbtack.school.notes.erroritem.code;

public enum ServerErrorCodeWithField {
    WRONG_SORT_BY_RATING("wrong sortByRating", "sortByRating"),
    PASSWORDS_DO_MATCH("Passwords do not match", "password"),
    USER_IS_DELETED("User is deleted", "login"),
    NO_PERMISSIONS("you are not allowed to perform this action", "cookie"),
    UNAUTHORIZED_ACCESS("login required", "cookie"),
    INCORRECT_PASSWORD("incorrect password", "password"),
    COOKIE_MISSING("required cookie must be set in the request", "cookie"),
    NOT_IGNORING("You are not ignoring", "login"),
    NOT_FOLLOWING("You are not following", "login"),
    IGNORE_ALREADY_EXIST("you already ignore", "login"),
    LOGIN_NOT_EXIST("login not exist", "login"),
    LOGIN_ALREADY_EXIST("login already exist", "login"),
    FOLLOWING_ALREADY_EXIST("you already following", "login"),
    SECTION_NAME_ALREADY_EXIST("Section name already exist", "name"),
    CAN_NOT_IGNORE("you can't ignore to yourself", "login"),
    CAN_NOT_RATE("you can't rate your note", "noteId"),
    CAN_NOT_SUBSCRIBE("you can't subscribe to yourself", "login"),
    WRONG_COMMENT_ID("wrong comment id", "commentId"),
    WRONG_NOTE_ID("wrong note id", "noteId"),
    WRONG_SECTION_ID("wrong section id", "sectionId"),
    WRONG_ID("wrong id", "id");

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
