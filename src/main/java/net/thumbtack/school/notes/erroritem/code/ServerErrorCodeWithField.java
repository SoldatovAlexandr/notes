package net.thumbtack.school.notes.erroritem.code;

public enum ServerErrorCodeWithField {
    WRONG_COMMENT_ID("wrong comment id", "commentId"),
    WRONG_NOTE_ID("wrong note id", "noteId"),
    WRONG_SECTION_ID("wrong section id", "sectionId"),
    SECTION_NAME_ALREADY_EXIST("Section name already exist", "name"),
    NOT_IGNORING("You are not ignoring", "login"),
    NOT_FOLLOWING("You are not following", "login"),
    IGNORE_ALREADY_EXIST("you already ignore", "login"),
    CAN_NOT_IGNORE("you can't ignore to yourself", "login"),
    CAN_NOT_RATE("you can't rate your note", "noteId"),
    FOLLOWING_ALREADY_EXIST("you already following", "login"),
    WRONG_ID("wrong id", "id"),
    NO_PERMISSIONS("you are not allowed to perform this action", "cookie"),
    UNAUTHORIZED_ACCESS("login required", "cookie"),
    LOGIN_NOT_EXIST("login not exist", "login"),
    CAN_NOT_SUBSCRIBE("you can't subscribe to yourself", "login"),
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
