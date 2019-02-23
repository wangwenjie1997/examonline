package com.wwj.examonline.mapper;

import com.wwj.examonline.entity.PaperGroup;
import com.wwj.examonline.entity.User;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.FetchType;

import java.util.List;

public interface PaperGroupMapper {

    @Select("select * from papergroup where papergroupcreator=#{0} and papergroupname=#{1}")
    PaperGroup selectPaperGroupByCreatorAndGroupName(int papergroupcreator,String paperGroupName);

    @Insert("insert into papergroup(papergroupcreator,papergroupname) values(#{0},#{1})")
    int insertPaperGroup(int papergroupcreator,String paperGroupName);

    @Select("select * from papergroup where papergroupcreator=#{0}")
    List<PaperGroup> selectPaperGroupByCreator(int papergroupcreator);

    @Select("select * from papergroup where papergroupname like concat(concat('%',#{0}),'%') and papergroupcreator=#{1}")
    List<PaperGroup> selectGroupByGroupNameAndCreatorFuzzySearch(String paperGroupName,int groupCreator);

    @Update("update papergroup set papergroupname=#{0} where papergroupid=#{1}")
    int updatePaperGroupName(String paperGroupName,int paperGroupId);

    @Delete("delete from papergroup where papergroupid=#{0}")
    int deletePaperGroupByGroupId(int paperGroupId);

    @Select("select * from papergroup where papergroupid=#{0}")
    PaperGroup selectPaperGroupByGroupId(int paperGroupId);

    @Select("select * from papergroup where papergroupid=#{0}")
    @Results({
            @Result(column="papergroupcreator",property="user",javaType= User.class
                    ,one=@One(select="com.wwj.examonline.mapper.UserMapper.selectUserByUserId",fetchType= FetchType.EAGER))
    })
    PaperGroup selectPaperGroupByGroupIdTwo(int paperGroupId);


}
