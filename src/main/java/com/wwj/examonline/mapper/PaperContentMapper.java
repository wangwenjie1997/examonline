package com.wwj.examonline.mapper;

import com.github.pagehelper.Page;
import com.wwj.examonline.entity.PaperContent;
import com.wwj.examonline.entity.Question;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.FetchType;

import java.util.List;

public interface PaperContentMapper {

    @Select("select * from papercotent where paperid=#{0}")
    @Results({
            @Result(id=true,column = "papercotentid",property = "paperCotentId"),
            @Result(column="questionid",property="question",javaType= Question.class
                    ,one=@One(select="com.wwj.examonline.mapper.QuestionMapper.selectQuestionByQuestionId",fetchType= FetchType.EAGER))
    })
    List<PaperContent> selectPaperContentByPaperId(int paperId);

    @Select("select * from papercotent where paperid=#{0}")
    List<PaperContent> selectPaperContentByPaperIdTwo(int paperId);

    @Select("select * from papercotent where paperid=#{0}")
    @Results({
            @Result(id=true,column = "papercotentid",property = "paperCotentId"),
            @Result(column="questionid",property="question",javaType= Question.class
                    ,one=@One(select="com.wwj.examonline.mapper.QuestionMapper.selectQuestionByQuestionIdTwo",fetchType= FetchType.EAGER))
    })
    Page<PaperContent> selectPagePaperContentByPaperId(int paperId);

    @Select("select * from papercotent where papercotentid=#{0}")
    @Results({
            @Result(id=true,column = "papercotentid",property = "paperCotentId"),
            @Result(column="questionid",property="question",javaType= Question.class
                    ,one=@One(select="com.wwj.examonline.mapper.QuestionMapper.selectQuestionByQuestionIdTwo",fetchType= FetchType.EAGER))
    })
    PaperContent selectPagePaperContentByPaperContentId(int paperCotentId);

    @Select("select * from papercotent where papercotentid=#{0}")
    @Results({
            @Result(id=true,column = "papercotentid",property = "paperCotentId"),
            @Result(column="questionid",property="question",javaType= Question.class
                    ,one=@One(select="com.wwj.examonline.mapper.QuestionMapper.selectQuestionByQuestionId",fetchType= FetchType.EAGER))
    })
    PaperContent selectPagePaperContentByPaperContentIdTwo(int paperCotentId);

    @Delete("delete from papercotent where papercotentid=#{0}")
    int deletePaperContentByPaperContentId(int paperCotentId);

    @Update("update papercotent set questionmark=#{0} where papercotentid=#{1}")
    int updatePaperContentMark(float questionMark, int paperCotentId);

    @Select("select count(*) from papercotent where paperid=#{0} and questionid=#{1}")
    int selectQuestionNumByPaperIdAndQuestionId(int paperId,int questionId);

    @Insert("insert into papercotent(paperid,questionid,questionmark) values(#{0},#{1},#{2})")
    int insertPaperContent(int paperId,int questionId,float questionMark);

    @Delete("delete from papercotent where paperid=#{0} and questionid=#{1}")
    int deletePaperContentByPaperContentIdAndQuestionId(int paperId,int questionId);

    @Select("select * from papercotent where paperid=#{0}")
    List<PaperContent> selectQuestionByPaperId(int paperId);

    @Delete("delete from papercotent where paperid=#{0}")
    int deletePaperContentByPaperId(int paperId);

    @Select("select count(*) from papercotent where paperid=#{0}")
    int selectQuestionNumByPaperId(int paperId);

    @Select("select sum(questionmark) from papercotent where paperid=#{0}")
    Float getPaperAllGrade(int paperId);

}
