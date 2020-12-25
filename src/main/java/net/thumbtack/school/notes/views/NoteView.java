package net.thumbtack.school.notes.views;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NoteView {
    private int id;
    private int authorId;
    private int sectionId;
    private String subject;
    private String body;
    private LocalDateTime created;
}
