package com.wwj.examonline.mapper;

import com.github.pagehelper.Page;
import com.wwj.examonline.entity.*;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.mapping.FetchType;

import java.util.List;

public interface ExamUserMapper {

    @Insert("insert into examuser(examuser,exampaper,joinexam) values(#{0},#{1},0)")
    int insertExamUser(int userId,int paperId);

    @Select("select count(*) from examuser where examuser=#{0} and exampaper=#{1}")
    int selectExamUserByUserIdAndPaperId(int userId,int paperId);

    @Delete("delete from examuser where exampaper=#{0}")
    int deleteExamUserByPaperId(int paperId);

    @Select("select count(*) from examuser where exampaper=#{0}")
    int selectAllExamUserNum(int paperId);

    @Select("select * from examuser where exampaper=#{0}")
    @Results({
            @Result(column="examuser",property="user",javaType= User.class
                    ,one=@One(select="com.wwj.examonline.mapper.UserMapper.selectUserByUserId",fetchType= FetchType.EAGER))
    })
    Page<ExamUser> selectAllExamUser(int paperId);

    @Delete("delete from examuser where examuser=#{0} and exampaper=#{1}")
    int deleteExamUserByUserIdAndPaperId(int userId,int paperId);

    @Select("select * from examuser where examuser=#{0}")
    @Results({
            @Result(column = "joinexam",property = "joinExam"),
            @Result(column = "examgrade",property = "examGrade"),
            @Result(column="exampaper",property="papeInfo",javaType= PaperInfo.class
                    ,one=@One(select="com.wwj.examonline.mapper.PaperInfoMapper.selectPaperByPaperId",fetchType= FetchType.EAGER))
    })
    Page<ExamUser> selectExamUserByUserId(int userId);

    @Select("select * from examuser where examuser=#{0}")
    @Results({
            @Result(column = "joinexam",property = "joinExam"),
            @Result(column = "examgrade",property = "examGrade"),
            @Result(column="exampaper",property="papeInfo",javaType= PaperInfo.class
                    ,one=@One(select="com.wwj.examonline.mapper.PaperInfoMapper.selectPaperByPaperId",fetchType= FetchType.EAGER))
    })
    List<ExamUser> selectExamUserByUserIdTwo(int userId);

    @Select("select * from examuser where examuser=#{0} and exampaper=#{1}")
    ExamUser getExamUserByUserIdAndPaperId(int userId,int paperId);

    @Select("select examgrade from examuser where examuser=#{0} and exampaper=#{1}")
    Float getExamGrade(int userId,int paperId);

    @Update("update examuser set joinexam=1 where examuser=#{0} and exampaper=#{1}")
    int updateJoinExam(int userId,int paperId);

    @Update("update examuser set joinexam=2 where examuser=#{0} and exampaper=#{1}")
    int updateSubmitExam(int userId,int paperId);

    @Update("update examuser set examgrade=#{0} where examuser=#{1} and exampaper=#{2}")
    int updateGrade(Float examGrade,int userId,int paperId);

    @Select("select count(*) from examuser where exampaper=#{0} and joinexam!=0 and examgrade!=-1")
    int selectNotCheckNum(int paperId);

    @Select("select count(*) from examuser where exampaper=#{0}")
    int selectExamUserNum(int paperId);

}
