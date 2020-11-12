package net.thumbtack.school.notes.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NoteVersion {
    private int id;
    private int revisionId;
    private String body;

    public NoteVersion(String body) {
        this.body = body;
    }
}
