package com.jm.online_store.service.impl;

import com.jm.online_store.exception.constants.ExceptionConstants;
import com.jm.online_store.enums.ExceptionEnums;
import com.jm.online_store.exception.TopicNotFoundException;
import com.jm.online_store.exception.TopicCategoryAlreadyExists;
import com.jm.online_store.exception.TopicCategoryNotFoundException;
import com.jm.online_store.model.TopicsCategory;
import com.jm.online_store.repository.TopicsCategoryRepository;
import com.jm.online_store.service.interf.TopicsCategoryService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class TopicsCategoryServiceImpl implements TopicsCategoryService {

    private final TopicsCategoryRepository topicsCategoryRepository;

    @Override
    @Transactional
    public TopicsCategory create(TopicsCategory topicsCategory) {
        if (topicsCategoryRepository.existsByCategoryName(topicsCategory.getCategoryName()))
            throw new TopicCategoryAlreadyExists(ExceptionEnums.TOPIC_CATEGORY.getText() + String.format(ExceptionConstants.ALREADY_EXISTS, topicsCategory.getCategoryName()));
        return topicsCategoryRepository.saveAndFlush(topicsCategory);
    }

    @Override
    public List<TopicsCategory> findAll() {
        return topicsCategoryRepository.findAll();
    }

    @Override
    public List<TopicsCategory> findAllByActualIsTrue() {
        return topicsCategoryRepository.findAllByActualIsTrue();
    }

    @Override
    public TopicsCategory findById(Long id) {
        return topicsCategoryRepository.findById(id).orElseThrow(() ->
                new TopicNotFoundException(ExceptionEnums.TOPIC_CATEGORY.getText()
                + String.format(ExceptionConstants.WITH_SUCH_ID_NOT_FOUND, id)));
    }

    @Override
    public TopicsCategory findByCategoryName(String categoryName) {
        return topicsCategoryRepository.findByCategoryName(categoryName);
    }

    @Override
    public boolean existsById(long id) {
        return topicsCategoryRepository.existsById(id);
    }

    @Override
    public boolean existsByCategoryName(String categoryName) {

        return topicsCategoryRepository.existsByCategoryName(categoryName);
    }

    @Override
    @Transactional
    public TopicsCategory update(TopicsCategory topicsCategory) {
        return topicsCategoryRepository.saveAndFlush(topicsCategory);
    }

    @Override
    @Transactional
    public TopicsCategory updateById(Long id, TopicsCategory topicsCategory) {
        TopicsCategory topicCat = topicsCategoryRepository.findById(id).orElseThrow(()
                -> new TopicCategoryNotFoundException(ExceptionEnums.TOPIC_CATEGORY.getText() +
                String.format(ExceptionConstants.WITH_SUCH_ID_NOT_FOUND, id)));
       if (topicCat.getCategoryName().equals(topicsCategory.getCategoryName())) {
           return topicsCategoryRepository.saveAndFlush(topicsCategory);
       } else {
           throw new TopicCategoryNotFoundException(ExceptionEnums.TOPIC_CATEGORY.getText() + ExceptionConstants.NOT_FOUND);
       }
    }

    @Override
    @Transactional
    public TopicsCategory archive(Long id) {
        // ???????????????? ?????????? (????. ????????) ?????????????? ???????? ???? id, ???? ???????????? ?????? ???????????? ???????????????????? TopicNotFoundException
        TopicsCategory toSave = findById(id);
        // ???? ???????????? ???????????? ???????????????? ???? null
        if (null != toSave) {
            //?????????????????? ???????????????? ???? ?????????????????? ???? ????????????????
            if (toSave.getActual()) {
                //???????????????????? , ?????????? actual ?? true ???? false
                toSave.setActual(false);
                //???????????? save
                topicsCategoryRepository.saveAndFlush(toSave);
            } else {
                //?????????????? ???????????????????? ???????? ???????????????? ???????????????? actual ?????? ??????????????????????????
                throw new TopicCategoryAlreadyExists(ExceptionEnums.TOPIC_CATEGORY.getText()  +
                        ExceptionConstants.ALREADY_ARCHIVED);
            }

        }
        return toSave;
    }

    @Override
    @Transactional
    public TopicsCategory unarchive(Long id) {
        // ???????????????? ?????????? (????. ????????) ?????????????? ???????? ???? id, ???? ???????????? ?????? ???????????? ???????????????????? TopicNotFoundException
        TopicsCategory toSave = findById(id);
        // ???? ???????????? ???????????? ???????????????? ???? null
        if (null != toSave) {
            //?????????????????? ???????????????? ???? ?????????????????? ????????????????
            if (!toSave.getActual()) {
                //???????????????????????? , ?????????? actual ?? false ???? true
                toSave.setActual(true);
                //???????????? save
                topicsCategoryRepository.saveAndFlush(toSave);
            } else {
                //?????????????? ???????????????????? ???????? ???????????????? ???????????????? actual ?????? ??????????????????????????
                throw new TopicCategoryAlreadyExists(ExceptionEnums.TOPIC_CATEGORY.getText() +
                        ExceptionConstants.ALREADY_UNARCHIVED);
            }

        }
        return toSave;
    }

}
