package net.thumbtack.school.notes.daoimpl;

import net.thumbtack.school.notes.dao.SectionDao;
import net.thumbtack.school.notes.mappers.SectionMapper;
import net.thumbtack.school.notes.model.Section;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SectionDaoImpl implements SectionDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(SectionDaoImpl.class);
    private final SectionMapper sectionMapper;

    @Autowired
    public SectionDaoImpl(SectionMapper sectionMapper) {
        this.sectionMapper = sectionMapper;
    }

    @Override
    public void insert(Section section) {
        LOGGER.debug("DAO insert section: {}", section);
        sectionMapper.insertSection(section);
    }

    @Override
    public void update(Section section) {
        LOGGER.debug("DAO update section: {}", section);
        sectionMapper.update(section);
    }

    @Override
    public Section getById(int id) {
        LOGGER.debug("DAO get section by id: {}", id);
        return sectionMapper.getById(id);
    }

    @Override
    public int deleteSection(Section section) {
        LOGGER.debug("DAO delete section: {}", section);
        return sectionMapper.deleteSection(section);
    }

    @Override
    public List<Section> getAllSections() {
        return sectionMapper.getAllSections();
    }
}
