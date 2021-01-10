package net.thumbtack.school.notes.dto.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NoteWithAllVersionsDtoResponse extends NoteDtoResponse {
    private List<RevisionDtoItem> revisions;

    public NoteWithAllVersionsDtoResponse(Integer id,
                                          int authorId,
                                          int sectionId,
                                          String subject,
                                          String body,
                                          String created,
                                          List<RevisionDtoItem> revisions) {
        super(id, authorId, sectionId, subject, body, created);
        this.revisions = revisions;
    }
}
