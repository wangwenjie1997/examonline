package com.wwj.examonline.serviceImpl;

import com.wwj.examonline.entity.QuestionKind;
import com.wwj.examonline.mapper.QuestionKindMapper;
import com.wwj.examonline.service.QuestionKindService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
public class QuestionKindServiceImpl implements QuestionKindService {

    @Autowired
    private QuestionKindMapper questionKindMapper;

    @Override
    public List<QuestionKind> getQuestionKind() {
        return questionKindMapper.selectQuestionKind();
    }
}
