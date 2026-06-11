package com.udacity.jwdnd.course1.cloudstorage.mapper;

import com.udacity.jwdnd.course1.cloudstorage.model.Credential;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface CredentialMapper {

    @Select("SELECT credentialid, url, username, credentialkey, password, userid FROM CREDENTIALS WHERE userid = #{userId} ORDER BY credentialid")
    @Results(id = "credentialResultMap", value = {
            @Result(property = "credentialId", column = "credentialid"),
            @Result(property = "key", column = "credentialkey"),
            @Result(property = "userId", column = "userid")
    })
    List<Credential> getCredentialsByUserId(Integer userId);

    @Select("SELECT credentialid, url, username, credentialkey, password, userid FROM CREDENTIALS WHERE credentialid = #{credentialId}")
    @Results(value = {
            @Result(property = "credentialId", column = "credentialid"),
            @Result(property = "key", column = "credentialkey"),
            @Result(property = "userId", column = "userid")
    })
    Credential getCredentialById(Integer credentialId);

    @Insert("INSERT INTO CREDENTIALS (url, username, credentialkey, password, userid) " +
            "VALUES (#{url}, #{username}, #{key}, #{password}, #{userId})")
    @Options(useGeneratedKeys = true, keyProperty = "credentialId")
    int insert(Credential credential);

    @Update("UPDATE CREDENTIALS SET url = #{url}, username = #{username}, credentialkey = #{key}, password = #{password} " +
            "WHERE credentialid = #{credentialId} AND userid = #{userId}")
    int update(Credential credential);

    @Delete("DELETE FROM CREDENTIALS WHERE credentialid = #{credentialId} AND userid = #{userId}")
    int delete(@Param("credentialId") Integer credentialId, @Param("userId") Integer userId);
}
