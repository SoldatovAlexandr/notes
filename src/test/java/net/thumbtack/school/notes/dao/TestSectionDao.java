package net.thumbtack.school.notes.dao;

import net.thumbtack.school.notes.model.Section;
import net.thumbtack.school.notes.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class TestSectionDao extends TestBaseDao {

    @Test
    public void testInsertAndGetSection() {
        User user = new User("login", "password",
                "firstName", "lastName", "patronymic");
        userDao.insert(user);

        Section section = new Section(user, "Some section name");

        sectionDao.insert(section);

        Section sectionFromDb = sectionDao.getById(section.getId());

        Assertions.assertEquals(section, sectionFromDb);
    }

    @Test
    public void testInsertAndUpdateSection() {
        User user = new User("login", "password",
                "firstName", "lastName", "patronymic");
        userDao.insert(user);

        Section section = new Section(user, "Some section name");

        sectionDao.insert(section);

        section.setName("New some section name");

        sectionDao.update(section);

        Section sectionFromDb = sectionDao.getById(section.getId());

        Assertions.assertEquals(section, sectionFromDb);
    }

    @Test
    public void testInsertAndDeleteSection() {
        User user = new User("login", "password",
                "firstName", "lastName", "patronymic");
        userDao.insert(user);

        Section section = new Section(user, "Some section name");

        sectionDao.insert(section);

        sectionDao.deleteSection(section);

        Section sectionFromDb = sectionDao.getById(section.getId());

        Assertions.assertNull(sectionFromDb);
    }

    @Test
    public void testInsertAndGetAllSections() {
        User user = new User("login", "password",
                "firstName", "lastName", "patronymic");

        userDao.insert(user);

        List<Section> sections = new ArrayList<>();

        sections.add(new Section(user, "Some section name1"));
        sections.add(new Section(user, "Some section name2"));
        sections.add(new Section(user, "Some section name3"));
        sections.add(new Section(user, "Some section name4"));

        for (Section section : sections) {
            sectionDao.insert(section);
        }

        List<Section> sectionsFromDb = sectionDao.getAllSections();

        Assertions.assertEquals(sections, sectionsFromDb);
    }
}
