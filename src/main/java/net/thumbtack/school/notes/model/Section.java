package net.thumbtack.school.notes.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Section {
    private int id;
    private int userId;
    private String name;

    public Section(int userId, String name) {
        this.userId = userId;
        this.name = name;
    }
}
