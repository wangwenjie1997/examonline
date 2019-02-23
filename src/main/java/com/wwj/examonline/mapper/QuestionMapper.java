package com.wwj.examonline.mapper;

import com.github.pagehelper.Page;
import com.wwj.examonline.entity.Question;
import com.wwj.examonline.entity.QuestionBank;
import com.wwj.examonline.entity.QuestionKind;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.FetchType;

import java.util.List;

public interface QuestionMapper {

    @Insert("insert into question" +
            "(questioncontent,questiontype,questionansewer,questionbank,optiona,optionb,optionc,optiond,usenum,statu) " +
            "values(#{0},#{1},#{2},#{3},#{4},#{5},#{6},#{7},#{8},#{9})")
    int insertQuestion(String questionContent,int questionKindId,String questionAnsewer,int questionBankId
            ,String optionA,String optionB,String optionC,String optionD,int useNum,int statu);

    //删除题库时的题目删除--开始
    @Update("update question set statu=0 where questionbank=#{0}")
    int deleteUsedQuestionByBankId(int bankId);

    @Delete("delete from question where questionbank=#{0} and usenum=0 and statu=0")
    int deleteQuestionFromDBByBankId(int bankId);
    //删除题库时的题目删除--结束

    /**
     *column="questiontype"的id为数据库question表的questiontype字段,
     * 把question表questiontype字段的值传给selectQuestionKindByKindId()方法,
     * property="questionKind"的questionKind为Question类的questionKind名称
     */
    @Select("select * from question where questionbank=#{0} and statu=1")
    @Results({
            @Result(id=true,column = "questionid",property = "questionId"),
            @Result(column = "questioncontent",property = "questionContent"),
            @Result(column="questiontype",property="questionKind",javaType= QuestionKind.class
                    ,one=@One(select="com.wwj.examonline.mapper.QuestionKindMapper.selectQuestionKindByKindId",fetchType= FetchType.EAGER))
    })
    Page<Question> selectQuestionByBankId(int bankId);

    @Select("select count(*) from question where questionbank=#{0} and statu=1")
    int selectAllQuestionNumByBankId(int bankId);

    @Select("select count(*) from question where questioncontent like concat(concat('%',#{0}),'%') and questionbank=#{1} and statu=1")
    int selectAllSearchQuestion(String questionContent,int bankId);

    @Select("select * from question where questioncontent like concat(concat('%',#{0}),'%') and questionbank=#{1} and statu=1")
    @Results({
            @Result(id=true,column = "questionid",property = "questionId"),
            @Result(column = "questioncontent",property = "questionContent"),
            @Result(column="questiontype",property="questionKind",javaType= QuestionKind.class
                    ,one=@One(select="com.wwj.examonline.mapper.QuestionKindMapper.selectQuestionKindByKindId",fetchType= FetchType.EAGER))
    })
    Page<Question> selectQuestionByBankIdAndQuestioContentFuzzySearch(String questionContent,int bankId);

    @Select("select * from question where questionid=#{0} and statu=1")
    @Results({
            @Result(id=true,column = "questionid",property = "questionId"),
            @Result(column = "questioncontent",property = "questionContent"),
            @Result(column="questiontype",property="questionKind",javaType= QuestionKind.class
                    ,one=@One(select="com.wwj.examonline.mapper.QuestionKindMapper.selectQuestionKindByKindId",fetchType= FetchType.EAGER))
    })
    Question selectQuestionByQuestionId(int questionId);

    @Select("select * from question where questionid=#{0} and statu=1")
    @Results({
            @Result(id=true,column = "questionid",property = "questionId"),
            @Result(column = "questioncontent",property = "questionContent"),
            @Result(column="questiontype",property="questionKind",javaType= QuestionKind.class
                    ,one=@One(select="com.wwj.examonline.mapper.QuestionKindMapper.selectQuestionKindByKindId",fetchType= FetchType.EAGER)),
            @Result(column="questionbank",property="questionBank",javaType= QuestionBank.class
                    ,one=@One(select="com.wwj.examonline.mapper.QuestionBankMapper.selectQuestionBankByBankId",fetchType= FetchType.EAGER))
    })
    Question selectQuestionByQuestionIdTwo(int questionId);

    //删除题目--开始
    @Update("update question set statu=0 where questionid=#{0}")
    int deleteUsedQuestionByQuestionId(int questionId);

    @Delete("delete from question where questionid=#{0} and usenum=0 and statu=0")
    int deleteQuestionFromDBByQuestionId(int questionId);
    //删除题目--结束

    @Update("update question set questioncontent=#{0},questionansewer=#{1}" +
            ",optiona=#{2},optionb=#{3},optionc=#{4},optiond=#{5} where questionid=#{6}")
    int updateQuestion(String questionContent,String questionAnsewer,String optionA
            ,String optionB,String optionC,String optionD,int questionId);

    //删除试卷时删除题目--开始
    @Update("update question set usenum=#{0} where questionid=#{1}")
    int updateUseNumByQuestionId(int useNum,int questionId);

    @Delete("delete from question where questionid=#{0}")
    int deleteQuestionByQuestionId(int questionId);


    /**
     * collection 遍历的类型,(集合为list,数组为array,如果方法参数是对象的某个属性,而这个属性是list,或array类型,就可以写形参的名字)
     * open 条件的开始
     * close 条件的结束
     * item  遍历集合时候定义的临时变量，存储当前遍历的每一个值
     * separator 多个值之间用逗号拼接
     * #{bankId} 获取遍历的每一个值,与item定义的临时变量一致
     */
//    @Select("select * from question where statu=1 and questionbank in "
//            + "<foreach item='item' collection='list' open='(' separator=',' close=')'>"
//            + "#{item}"
//            + "</foreach>")
//    List<Question> getPageAllBankQuestion(List<Integer> bankIdList);

    @Select("select questionid from question where questionbank=#{0} and statu=1")
    List<Integer> selectAllQuestionIdByBankId(int bankId);

    @Select("select questionid from question where questionbank=#{0} and questioncontent like concat(concat('%',#{1}),'%') and statu=1")
    List<Integer> selectAllQuestionIdByBankIdFuzzySearch(int bankId,String questionContent);

    @Select("select questionid from question where questionbank=#{0} and questiontype=#{1} and statu=1")
    List<Integer> selectAllQuestionIdByBankIdAndKind(int bankId,int questionKindId);

    @Select("select questionid from question where questionbank=#{0} and questiontype=#{1} and questioncontent like concat(concat('%',#{2}),'%') and statu=1")
    List<Integer> selectAllQuestionIdByBankIdAndKindFuzzySearch(int bankId,int questionKindId,String questionContent);


}
