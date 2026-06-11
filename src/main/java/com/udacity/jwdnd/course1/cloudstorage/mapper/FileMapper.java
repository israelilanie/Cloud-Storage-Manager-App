package com.udacity.jwdnd.course1.cloudstorage.mapper;

import com.udacity.jwdnd.course1.cloudstorage.model.FileRecord;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FileMapper {

    @Select("SELECT fileid, filename, contenttype, filesize, userid, filedata FROM FILES WHERE userid = #{userId} ORDER BY fileid")
    @Results(id = "fileResultMap", value = {
            @Result(property = "fileId", column = "fileid"),
            @Result(property = "contentType", column = "contenttype"),
            @Result(property = "fileSize", column = "filesize"),
            @Result(property = "userId", column = "userid"),
            @Result(property = "fileData", column = "filedata")
    })
    List<FileRecord> getFilesByUserId(Integer userId);

    @Select("SELECT fileid, filename, contenttype, filesize, userid, filedata FROM FILES WHERE fileid = #{fileId}")
    @Results(value = {
            @Result(property = "fileId", column = "fileid"),
            @Result(property = "contentType", column = "contenttype"),
            @Result(property = "fileSize", column = "filesize"),
            @Result(property = "userId", column = "userid"),
            @Result(property = "fileData", column = "filedata")
    })
    FileRecord getFileById(Integer fileId);

    @Select("SELECT fileid, filename, contenttype, filesize, userid, filedata FROM FILES WHERE userid = #{userId} AND filename = #{filename}")
    @Results(value = {
            @Result(property = "fileId", column = "fileid"),
            @Result(property = "contentType", column = "contenttype"),
            @Result(property = "fileSize", column = "filesize"),
            @Result(property = "userId", column = "userid"),
            @Result(property = "fileData", column = "filedata")
    })
    FileRecord getFileByName(@Param("userId") Integer userId, @Param("filename") String filename);

    @Insert("INSERT INTO FILES (filename, contenttype, filesize, userid, filedata) " +
            "VALUES (#{filename}, #{contentType}, #{fileSize}, #{userId}, #{fileData})")
    @Options(useGeneratedKeys = true, keyProperty = "fileId")
    int insert(FileRecord fileRecord);

    @Delete("DELETE FROM FILES WHERE fileid = #{fileId} AND userid = #{userId}")
    int delete(@Param("fileId") Integer fileId, @Param("userId") Integer userId);
}
