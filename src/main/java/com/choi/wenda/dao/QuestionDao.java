package com.choi.wenda.dao;

import com.choi.wenda.model.Question;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface QuestionDao {

    String TABLE_NAME = " question ";
    String INSERT_FIELDS = " title, content, createdDate, userId, commentCount ";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    @Insert(
            {"insert into ", TABLE_NAME, "(", INSERT_FIELDS, ") values (#{title},#{content},#{createdDate},#{userId},#{commentCount})"}
    )
    int addQuestion(Question question);

    List<Question> selectLatestQuestions(@Param("userId") int userId,
                                         @Param("offset") int offset,
                                         @Param("limit") int limit);

    @Select({"select ", SELECT_FIELDS, "from ", TABLE_NAME, "where id = #{id}"})
    Question selectById(int id);

    @Update(
            {"update ", TABLE_NAME, " set commentCount = #{commentCount} where id = #{id}"}
    )
    int updateCommentCount(@Param("id") int id,
                           @Param("commentCount") int commentCount);



}
