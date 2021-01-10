package net.thumbtack.school.notes.model;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Note {
    private int id;
    private String subject;
    private Section section;
    private User author;
    private LocalDateTime created;
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<NoteVersion> noteVersions;
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<Comment> comments;


    public Note(String subject, Section section, List<NoteVersion> noteVersions) {
        this(0, subject, section, noteVersions, null, null);
    }

    public Note(int id, String subject, Section section, List<NoteVersion> noteVersions, User author, LocalDateTime created) {
        this.id = id;
        this.subject = subject;
        this.section = section;
        this.noteVersions = noteVersions;
        this.author = author;
        this.created = created;
    }

    public NoteVersion getCurrentVersion() {
        return getNoteVersions().stream().max(Comparator.comparingInt(NoteVersion::getRevisionId)).get();
    }
}
