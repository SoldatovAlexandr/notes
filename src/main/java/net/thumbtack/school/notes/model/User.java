package net.thumbtack.school.notes.model;

import lombok.*;

import java.time.LocalDateTime;
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
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private LocalDateTime registered;
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<User> followers;
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<User> followings;
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<User> ignore;
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<User> ignoredBy;

    public User(String login, String password, String firstName,
                String lastName, String patronymic, UserType type, LocalDateTime registered) {
        this.login = login;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.patronymic = patronymic;
        this.type = type;
        this.registered = registered;
        this.followers = new ArrayList<>();
        this.followings = new ArrayList<>();
        this.ignore = new ArrayList<>();
        this.ignoredBy = new ArrayList<>();
    }

    public User(String login, String password, String firstName,
                String lastName, String patronymic, LocalDateTime registered) {
        this(login, password, firstName, lastName, patronymic, UserType.USER, registered);
    }

    public User(String password, String firstName, String lastName, String patronymic) {
        this(null, password, firstName, lastName, patronymic, null);
    }
}
