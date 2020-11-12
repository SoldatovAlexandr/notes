package net.thumbtack.school.notes.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommentInfoDtoResponse {
    private int id;
    private String body;
    private int noteId;
    private int authorId;
    private int revisionId;
    private String created;
}
