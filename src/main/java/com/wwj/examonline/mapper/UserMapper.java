package com.wwj.examonline.mapper;

import com.wwj.examonline.entity.User;
import org.apache.ibatis.annotations.*;


public interface UserMapper {

    @Select("select * from user where mail=#{0} and password=#{1}")
    @Results({
            @Result(id=true,column = "userid",property = "userid"),
            @Result(column = "mail",property = "mail"),
            @Result(column = "password",property = "password"),
            @Result(column = "name",property = "name")
    })
    User selectUserByMailAndPassword(String mail, String password);

    @Insert("insert into user(mail,password,name,picpath) values(#{mail},#{password},#{name},#{picpath})")
    int insertUser(User user);

    @Select("select * from user where mail=#{0}")
    @Results({
            @Result(id=true,column = "userid",property = "userid"),
            @Result(column = "mail",property = "mail"),
            @Result(column = "password",property = "password"),
            @Result(column = "name",property = "name")
    })
    User selectUserByMail(String mail);

    @Update("update user set password=#{0} where userid=#{1}")
    int updatePassword(String password,int id);

    @Update("update user set name=#{0} where userid=#{1}")
    int updateName(String name,int id);

    @Update("update user set mail=#{0} where userid=#{1}")
    int updateMail(String mail,int id);

}
