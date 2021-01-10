package net.thumbtack.school.notes.model;


import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Section {
    private int id;
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private User author;
    private String name;

    public Section(int id) {
        this(id, null, null);
    }

    public Section(User author, String name) {
        this(0, author, name);
    }
}
