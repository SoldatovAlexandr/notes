package net.thumbtack.school.notes.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Comment {
    private int id;
    private String body;
    private int noteId;
    private int authorId;
    private int revisionId;
    private LocalDateTime created;

    public Comment(String body, int noteId) {
        this.body = body;
        this.noteId = noteId;
    }

}
