package net.thumbtack.school.notes.dao;

import net.thumbtack.school.notes.model.Section;

import java.util.List;

public interface SectionDao {
    void insert(Section section);

    void update(Section section);

    Section getById(int id);

    int deleteSection(Section section);

    List<Section> getAllSections();
}
