package com.wwj.examonline.mapper;

import com.wwj.examonline.entity.QuestionKind;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface QuestionKindMapper {

    @Select("select * from questionkind")
    List<QuestionKind> selectQuestionKind();

    @Select("select * from questionkind where kindid=#{0}")
    QuestionKind selectQuestionKindByKindId(int kindId);

}
