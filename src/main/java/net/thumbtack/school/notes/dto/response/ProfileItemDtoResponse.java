package net.thumbtack.school.notes.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProfileItemDtoResponse {
    private int id;
    private String firstName;
    private String lastName;
    private String patronymic;
    private String login;
    private String timeRegistered;
    private boolean online;
    private boolean deleted;
    private double rating;
}
