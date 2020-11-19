package net.thumbtack.school.notes.service;

import net.thumbtack.school.notes.Config;
import net.thumbtack.school.notes.dao.*;
import net.thumbtack.school.notes.dto.response.EmptyDtoResponse;
import net.thumbtack.school.notes.dto.response.SettingsDtoResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class TestDebugService {
    @MockBean
    private UserDao userDao;

    @MockBean
    private CommentDao commentDao;

    @MockBean
    private NoteDao noteDao;

    @MockBean
    private SectionDao sectionDao;

    @MockBean
    private CommonDao commonDao;

    @MockBean
    private Config config;


    @Test
    public void testGetServerSettings() {
        DebugService debugService = new DebugService(userDao, sectionDao, noteDao, commentDao, config, commonDao);

        when(config.getMaxNameLength()).thenReturn(50);

        when(config.getMinPasswordLength()).thenReturn(10);

        when(config.getUserIdleTimeout()).thenReturn(1000);

        SettingsDtoResponse expectedResponse = new SettingsDtoResponse(50, 10,
                1000);

        SettingsDtoResponse response = debugService.getServerSettings();

        Assertions.assertEquals(expectedResponse, response);
    }

    @Test
    public void testClear() {
        DebugService debugService = new DebugService(userDao, sectionDao, noteDao, commentDao, config, commonDao);

        EmptyDtoResponse response = debugService.clear();

        verify(commonDao).clear();
    }
}
