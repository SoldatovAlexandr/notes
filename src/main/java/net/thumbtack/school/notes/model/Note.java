package net.thumbtack.school.notes.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Note {
    private int id;
    private String subject;
    private int sectionId;
    private int authorId;
    private LocalDate created;
    private NoteVersion noteVersion;
    private List<Comment> comments;


    public Note(String subject, int sectionId, NoteVersion noteVersion) {
        this.subject = subject;
        this.sectionId = sectionId;
        this.noteVersion =noteVersion;
    }
}
