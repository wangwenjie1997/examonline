package com.wwj.examonline.mapper;

import com.wwj.examonline.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;


public interface UserMapper {

    @Select("select * from user where mail=#{0} and password=#{1}")
    @Results({
            @Result(id=true,column = "userid",property = "userId"),
            @Result(column = "mail",property = "mail"),
            @Result(column = "password",property = "password"),
            @Result(column = "name",property = "name")
    })
    User selectUserByMailAndPassword(String mail, String password);

    @Insert("insert into user(mail,password,name,picpath) values(#{mail},#{password},#{name},#{picPath})")
    int insertUser(User user);

    @Select("select * from user where mail=#{0}")
    @Results({
            @Result(id=true,column = "userid",property = "userId"),
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

    @Update("update user set picpath=#{0} where userid=#{1}")
    int updatePicaPath(String mail,int id);

    @Select("select * from user where userid=#{0}")
    User selectUserByUserId(int userId);

    @Select("select * from user where mail like concat(concat('%',#{0}),'%') or name like concat(concat('%',#{1}),'%')")
    List<User> getUserFuzzySearch(String mail,String name);

}
