package net.thumbtack.school.notes.dao;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class TestBaseDao {
    @Autowired
    protected CommentDao commentDao;

    @Autowired
    protected CommonDao commonDao;

    @Autowired
    protected NoteDao noteDao;

    @Autowired
    protected SectionDao sectionDao;

    @Autowired
    protected UserDao userDao;

    @BeforeEach
    public void clear() {
        commonDao.clear();
    }

    protected LocalDateTime getCurrentDateTime() {
        return LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
    }
}
