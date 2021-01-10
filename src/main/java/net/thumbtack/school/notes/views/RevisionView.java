package net.thumbtack.school.notes.views;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RevisionView {
    private int id;
    private String body;
    private LocalDateTime created;
    private List<CommentView> comments;
}
