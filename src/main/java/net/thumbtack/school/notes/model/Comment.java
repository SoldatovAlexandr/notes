package net.thumbtack.school.notes.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Comment {
    private int id;
    private String body;
    @EqualsAndHashCode.Exclude
    private Note note;
    @EqualsAndHashCode.Exclude
    private User author;
    private int revisionId;
    private LocalDateTime created;

    public Comment(String body, Note note) {
        this.body = body;
        this.note = note;
    }
}
