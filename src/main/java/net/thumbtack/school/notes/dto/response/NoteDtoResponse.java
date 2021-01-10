package net.thumbtack.school.notes.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NoteDtoResponse {
    private int id;
    private int authorId;
    private int sectionId;
    private String subject;
    private String body;
    private String created;
}
