package net.thumbtack.school.notes.views;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserView {
    private int id;
    private String firstName;
    private String lastName;
    private String patronymic;
    private String login;
    private boolean deleted;
    private boolean isSuper;
    private float userRating;
    private boolean online;
    private LocalDateTime registered;
}
