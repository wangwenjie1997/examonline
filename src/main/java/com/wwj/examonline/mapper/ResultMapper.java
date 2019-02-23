package com.wwj.examonline.mapper;

import com.wwj.examonline.entity.PaperContent;
import com.wwj.examonline.entity.QuestionKind;
import com.wwj.examonline.entity.Result;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.FetchType;

import java.util.List;

public interface ResultMapper {

    @Delete("delete from result where resultquestion=#{0}")
    int deleteResultByResultQuestion(int paperContentId);

    @Update("update result set resultansewer=#{0} where resultuser=#{1} and resultquestion=#{2} and resultpaperinfo=#{3}")
    int updateResult(String resultAnsewer,int userId,int paperContentId,int paperId);

    @Insert("insert into result(resultuser,resultquestion,resultpaperinfo) values(#{0},#{1},#{2})")
    int insertResult(int userId,int paperContentId,int paperId);

    @Select("select * from result where resultuser=#{0} and resultquestion=#{1} and resultpaperinfo=#{2}")
    Result selectResultByUserIdAndContentIdAndPaperId(int userId,int paperContentId,int paperInfoId);

    @Update("update result set resultmark=#{0},resultcheck=1 where resultid=#{1}")
    int updateResultGrade(Float resultMark,int resultId);

    @Select("select * from result where resultid=#{0}")
    @Results({
            @org.apache.ibatis.annotations.Result(column = "resultansewer",property = "resultAnsewer"),
            @org.apache.ibatis.annotations.Result(column = "resultmark",property = "resultMark"),
            @org.apache.ibatis.annotations.Result(column = "resultcheck",property = "resultCheck"),
            @org.apache.ibatis.annotations.Result(column="resultquestion",property="paperContent",javaType= PaperContent.class
                    ,one=@One(select="com.wwj.examonline.mapper.PaperContentMapper.selectPagePaperContentByPaperContentIdTwo",fetchType= FetchType.EAGER))
    })
    Result selectResultByResultId(int resultId);

    @Select("select sum(resultmark) from result where resultuser=#{0} and resultpaperinfo=#{1}")
    Float getUserAllGrade(int userId,int paperInfoId);

    @Update("update result set resultcheck=1 where resultuser=#{0} and resultpaperinfo=#{1}")
    int updateAllResultCheck(int userId,int paperInfoId);

}
