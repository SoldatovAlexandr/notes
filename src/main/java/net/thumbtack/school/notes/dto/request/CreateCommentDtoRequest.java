package net.thumbtack.school.notes.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateCommentDtoRequest {

    @NotNull(message = "BODY_NOT_SET")
    private String body;

    @NotNull(message = "NOTE_ID_NOT_SET")
    private Integer noteId;
}
