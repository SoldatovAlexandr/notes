package net.thumbtack.school.notes.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SuperProfileItemDtoResponse extends ProfileItemDtoResponse {
    private boolean isSuper;

    public SuperProfileItemDtoResponse(int id, String firstName, String lastName, String patronymic,
                                       String login, String timeRegistered, boolean online,
                                       boolean deleted, double rating, boolean isSuper) {
        super(id, firstName, lastName, patronymic, login, timeRegistered, online, deleted, rating);
        this.isSuper = isSuper;
    }
}
