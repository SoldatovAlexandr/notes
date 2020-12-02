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

    private static final int MAX_NAME_LENGTH = 50;
    private static final int MIN_PASSWORD_LENGTH = 10;
    private static final int IDLE_TIMEOUT = 1000;

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

        when(config.getMaxNameLength()).thenReturn(MAX_NAME_LENGTH);

        when(config.getMinPasswordLength()).thenReturn(MIN_PASSWORD_LENGTH);

        when(config.getUserIdleTimeout()).thenReturn(IDLE_TIMEOUT);

        SettingsDtoResponse expectedResponse = new SettingsDtoResponse(MAX_NAME_LENGTH, MIN_PASSWORD_LENGTH, IDLE_TIMEOUT);

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
