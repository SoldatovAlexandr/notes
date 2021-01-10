package net.thumbtack.school.notes.views;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentView {
    private int id;
    private String body;
    private int authorId;
    private int revisionId;
    private LocalDateTime created;
}
