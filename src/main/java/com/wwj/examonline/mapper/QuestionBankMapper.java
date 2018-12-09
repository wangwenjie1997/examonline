package com.wwj.examonline.mapper;

import com.wwj.examonline.entity.QuestionBank;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

public interface QuestionBankMapper {

    @Insert("insert into questionbank(bankcreator,bankname) values(#{0},#{1})")
    int insertQuestionBank(int userid,String bankname);

    @Select("select * from questionbank where bankcreator=#{0} and bankname=#{1}")
    @Results({
            @Result(id=true,column = "bankid",property = "bankid"),
            @Result(column = "bankname",property = "bankname"),
    })
    QuestionBank selectBankByCreatorAndBankName(int bankcreator, String bankname);

}
