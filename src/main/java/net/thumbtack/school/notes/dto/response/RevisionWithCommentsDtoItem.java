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
public class RevisionWithCommentsDtoItem extends RevisionDtoItem {
    private List<CommentDtoItem> comments;

    public RevisionWithCommentsDtoItem(int id, String body, String created, List<CommentDtoItem> comments) {
        super(id, body, created);
        this.comments = comments;
    }
}
