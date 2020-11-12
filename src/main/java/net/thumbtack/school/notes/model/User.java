package net.thumbtack.school.notes.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    public User(String login, String password, String firstName,
                String lastName, String patronymic) {
        this.login = login;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.patronymic = patronymic;
    }

    public User(String password, String firstName, String lastName, String patronymic) {
        this(null, password, firstName, lastName, patronymic);
    }
}
