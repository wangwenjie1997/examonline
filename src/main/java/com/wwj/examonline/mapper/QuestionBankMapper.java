package com.wwj.examonline.mapper;

import com.github.pagehelper.Page;
import com.wwj.examonline.entity.QuestionBank;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface QuestionBankMapper {

    @Insert("insert into questionbank(bankcreator,bankname) values(#{0},#{1})")
    int insertQuestionBank(int userId,String bankName);

    @Select("select * from questionbank where bankcreator=#{0} and bankname=#{1}")
    @Results({
            @Result(id=true,column = "bankid",property = "bankId"),
            @Result(column = "bankname",property = "bankName"),
    })
    QuestionBank selectBankByCreatorAndBankName(int bankcreator, String bankname);

    @Select("select * from questionbank where bankcreator=#{0}")
    @Results({
            @Result(id=true,column = "bankid",property = "bankId"),
            @Result(column = "bankname",property = "bankName"),
    })
    List<QuestionBank> selectBankByCreator(int bankcreator);

    @Delete("delete from questionbank where bankid=#{0}")
    int deleteQuestionBankByBankId(int bankId);

    @Update("update questionbank set bankname=#{0} where bankid=#{1}")
    int updateQuestionBankName(String bankName,int bankId);

    @Select("select * from questionbank where bankname like concat(concat('%',#{0}),'%') and bankcreator=#{1}")
    List<QuestionBank> selectBankByBankNameAndCreatorFuzzySearch(String bankName,int bankCreator);

    @Select("select * from questionbank where bankid=#{0}")
    QuestionBank selectQuestionBankByBankId(int bankId);

    @Select("select bankid from questionbank where bankcreator=#{0}")
    List<Integer> selectBankIdByCreator(int bankCreator);

}
