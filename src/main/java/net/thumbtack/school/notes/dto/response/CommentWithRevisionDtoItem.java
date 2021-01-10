package net.thumbtack.school.notes.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommentWithRevisionDtoItem extends CommentDtoItem {
    private int revisionId;

    public CommentWithRevisionDtoItem(int id, String body, int authorId, String format, int revisionId) {
        super(id, body, authorId, format);
        this.revisionId = revisionId;
    }
}
