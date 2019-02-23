package com.wwj.examonline.mapper;

import com.github.pagehelper.Page;
import com.wwj.examonline.entity.PaperGroup;
import com.wwj.examonline.entity.PaperInfo;
import com.wwj.examonline.entity.Question;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.FetchType;

import java.util.List;

public interface PaperInfoMapper {

    @Select("select * from paperinfo where papergroup=#{0}")
    List<PaperInfo> selectPaperInfoByPaperGroupId(int paperGroupId);

    @Delete("delete from paperinfo where paperid=#{0}")
    int deletePaperInfoByPaperId(int paperId);

    @Select("select count(*) from paperinfo where papername like concat(concat('%',#{0}),'%') and papergroup=#{1}")
    int selectAllSearchPaperNum(String paperName,int paperGroupId);

    @Select("select count(*) from paperinfo where papergroup=#{0}")
    int selectAllPaperNum(int paperGroupId);

    @Select("select * from paperinfo where papername like concat(concat('%',#{0}),'%') and papergroup=#{1}")
    Page<PaperInfo> selectPaperByPaperNameAndGroupIdFuzzySearch(String paperName,int paperGroupId);

    @Select("select * from paperinfo where papergroup=#{0}")
    Page<PaperInfo> selectPaperByGroupId(int paperGroupId);

    @Select("select count(*) from paperinfo where papergroup=#{0} and papername=#{1}")
    int selectPaperNumByGroupAndPaperName(int paperGroupId,String paperName);

    @Insert("insert into paperinfo(papername,papergroup,oneoptioncheck,moreoptioncheck,trueorfalsecheck,fillinblankcheck) " +
            "values(#{0},#{1},1,1,1,1)")
    int insetPaper(String paperName,int paperGroupId);

    @Select("select * from paperinfo where papername=#{0} and papergroup=#{1}")
    PaperInfo selectPaperByPaperNameAndGroupId(String paperName,int paperGroupId);

//    @Update("update paperinfo set oneoptioncheck=#{0},moreoptioncheck=#{1},trueorfalsecheck=#{2},fillinblankcheck=#{3}" +
//            ",oneoptionmark=#{4},moreoptionmark=#{5},trueorfalsemark=#{6},fillinblankmark=#{7}" +
//            ",shortanswerquestionmark=#{8} where paperid=#{9}")
//    int updatePaperMarkAndCheckByPaperId(int oneOptionCheck,int moreOptionCheck,int trueOrFalseCheck,int fillInBlankCheck
//            ,float oneOptionMark,float moreOptionMark,float trueOrFalseMark,float fillInBlankMark
//            ,float shortAnswerQuestionMark,int paperId);

    @Select("select * from paperinfo where paperid=#{0}")
    PaperInfo selectPaperByPaperId(int paperId);

    @Update("update paperinfo set papername=#{0},oneoptioncheck=#{1},moreoptioncheck=#{2},trueorfalsecheck=#{3}" +
            ",fillinblankcheck=#{4},oneoptionmark=#{5},moreoptionmark=#{6},trueorfalsemark=#{7},fillinblankmark=#{8}" +
            ",shortanswerquestionmark=#{9} where paperid=#{10}")
    int updatePaperByPaperId(String paperName,int oneOptionCheck,int moreOptionCheck,int trueOrFalseCheck,int fillInBlankCheck
            ,float oneOptionMark,float moreOptionMark,float trueOrFalseMark,float fillInBlankMark
            ,float shortAnswerQuestionMark,int paperId);

    @Update("update paperinfo set paperstart=#{0},paperend=#{1} where paperid=#{2}")
    int updatePaperTime(String paperStart,String paperEnd,int paperId);

    @Select("select * from paperinfo where paperid=#{0}")
    @Results({
            @Result(id=true,column = "paperid",property = "paperId"),
            @Result(column = "paperstart",property = "paperStart"),
            @Result(column = "paperend",property = "paperEnd"),
            @Result(column = "papername",property = "paperName"),
            @Result(column="papergroup",property="paperGroup",javaType= PaperGroup.class
                    ,one=@One(select="com.wwj.examonline.mapper.PaperGroupMapper.selectPaperGroupByGroupIdTwo",fetchType= FetchType.EAGER))
    })
    PaperInfo selectPaperByPaperIdTwo(int paperId);

}
