package net.thumbtack.school.notes.daoimpl;

import net.thumbtack.school.notes.dao.CommonDao;
import net.thumbtack.school.notes.mappers.CommentMapper;
import net.thumbtack.school.notes.mappers.NoteMapper;
import net.thumbtack.school.notes.mappers.SectionMapper;
import net.thumbtack.school.notes.mappers.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class CommonDaoImpl implements CommonDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommonDaoImpl.class);
    private final CommentMapper commentMapper;
    private final UserMapper userMapper;
    private final NoteMapper noteMapper;
    private final SectionMapper sectionMapper;

    @Autowired
    public CommonDaoImpl(CommentMapper commentMapper, UserMapper userMapper,
                         NoteMapper noteMapper, SectionMapper sectionMapper) {
        this.commentMapper = commentMapper;
        this.userMapper = userMapper;
        this.noteMapper = noteMapper;
        this.sectionMapper = sectionMapper;
    }

    @Override
    public void clear() {
        LOGGER.debug("DAO clear all");
        commentMapper.clear();
        userMapper.clear();
        noteMapper.clear();
        sectionMapper.clear();
    }
}
