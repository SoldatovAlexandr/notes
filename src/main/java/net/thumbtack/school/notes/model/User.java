package net.thumbtack.school.notes.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private int id;
    private String firstName;
    private String lastName;
    private String patronymic;
    private String login;
    private String password;
    private boolean deleted;
    private UserType type;
    private List<User> followers;
    private List<User> followings;
    private List<User> ignore;
    private List<User> ignoredBy;

    public User(String login, String password, String firstName,
                String lastName, String patronymic, UserType type) {
        this.login = login;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.patronymic = patronymic;
        this.type = type;
        this.followers = new ArrayList<>();
        this.followings = new ArrayList<>();
        this.ignore = new ArrayList<>();
        this.ignoredBy = new ArrayList<>();
    }

    public User(String login, String password, String firstName,
                String lastName, String patronymic) {
        this(login, password, firstName, lastName, patronymic, UserType.USER);
    }

    public User(String password, String firstName, String lastName, String patronymic) {
        this(null, password, firstName, lastName, patronymic);
    }
}
