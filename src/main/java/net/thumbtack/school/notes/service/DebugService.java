package net.thumbtack.school.notes.service;

import net.thumbtack.school.notes.Config;
import net.thumbtack.school.notes.dao.*;
import net.thumbtack.school.notes.dto.response.EmptyDtoResponse;
import net.thumbtack.school.notes.dto.response.SettingsDtoResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DebugService extends ServiceBase {

    private final CommonDao commonDao;

    @Autowired
    public DebugService(UserDao userDao, SectionDao sectionDao, NoteDao noteDao,
                        CommentDao commentDao, Config config, CommonDao commonDao) {
        super(userDao, sectionDao, noteDao, commentDao, config);
        this.commonDao = commonDao;
    }


    public SettingsDtoResponse getServerSettings() {
        return new SettingsDtoResponse(
                config.getMaxNameLength(),
                config.getMinPasswordLength(),
                config.getUserIdleTimeout()
        );
    }

    public EmptyDtoResponse clear() {
        commonDao.clear();
        return new EmptyDtoResponse();
    }
}
