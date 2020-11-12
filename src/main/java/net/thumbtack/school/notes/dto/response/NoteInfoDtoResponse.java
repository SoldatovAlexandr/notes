package net.thumbtack.school.notes.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NoteInfoDtoResponse {
    private int id;
    private String subject;
    private String body;
    private int sectionId;
    private int authorId;
    private String created;
    private int revisionId;
}
