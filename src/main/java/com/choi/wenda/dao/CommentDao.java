package com.choi.wenda.dao;

import com.choi.wenda.model.Comment;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CommentDao {
    String TABLE_NAME = " comment ";
    String INSERT_FIELDS = " userId, content, createdDate, entityId, entityType, status ";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    @Insert(
            {"insert into ", TABLE_NAME, "(", INSERT_FIELDS,") " +
                    "values (#{userId},#{content},#{createdDate},#{entityId},#{entityType},#{status})"}
                    )
    int addComment(Comment comment);

    @Update(
            {"update ", TABLE_NAME, " set status = #{status} where entityId = #{entityId} and entityType = #{entityType}"}
    )
    void updateStstus(@Param("entityId") int entityId,
                      @Param("entityType") int entityType,
                      @Param("status") int status);

    @Select(
            {"select ",SELECT_FIELDS, " from ", TABLE_NAME,
            " where entityId = #{entityId} and entityType = #{entityType} order by id desc"}
    )
    List<Comment> selectByEntity(@Param("entityId") int entityId,
                                 @Param("entityType") int entityType);

    @Select(
            {"select count(id) from ", TABLE_NAME, " where entityId = #{entityId} and entityType = #{entityType}"}
    )
    int getCommentCount(@Param("entityId") int entityId,
                        @Param("entityType") int entityType);

    @Select(
            {"select ", SELECT_FIELDS, " from ",TABLE_NAME, " where id = #{id}"}
    )
    Comment getCommentById(int id);

    @Select(
            {"select count(id) from",TABLE_NAME, " where userId = #{userId}" }
    )
    int getUserCommentCount(int userId);
}
