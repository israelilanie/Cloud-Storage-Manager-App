package com.udacity.jwdnd.course1.cloudstorage.mapper;

import com.udacity.jwdnd.course1.cloudstorage.model.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    @Select("SELECT userid, username, salt, password, firstname, lastname FROM USERS WHERE username = #{username}")
    @Results(id = "userResultMap", value = {
            @Result(property = "userId", column = "userid"),
            @Result(property = "firstName", column = "firstname"),
            @Result(property = "lastName", column = "lastname")
    })
    User getUser(String username);

    @Select("SELECT userid, username, salt, password, firstname, lastname FROM USERS WHERE userid = #{userId}")
    @Results(value = {
            @Result(property = "userId", column = "userid"),
            @Result(property = "firstName", column = "firstname"),
            @Result(property = "lastName", column = "lastname")
    })
    User getUserById(Integer userId);

    @Insert("INSERT INTO USERS (username, salt, password, firstname, lastname) " +
            "VALUES (#{username}, #{salt}, #{password}, #{firstName}, #{lastName})")
    @Options(useGeneratedKeys = true, keyProperty = "userId")
    int insert(User user);
}
