package com.yjx.gymmanager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yjx.gymmanager.entity.Member;
import com.yjx.gymmanager.vo.AdminMemberVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface MemberMapper extends BaseMapper<Member>{
   @Select("select m.*,u.username from member m LEFT JOIN sys_user u ON m.id = u.related_id ")
   List<AdminMemberVO> selectAll();
   @Select("select * from member where id = #{id}")
   Member selectMemberById(long id);
   @Insert("insert into member(name,phone,gender,age,card_type,expire_time,status) values(#{name},#{phone},#{gender},#{age},#{cardType},#{expireTime},#{status})")
   Integer addMember(Member member);
   @Select("select id from member where name = #{name}")
   Integer selectIdByName(String name);
   @Update("update member set name = #{member.name},phone = #{member.phone},gender = #{member.gender},age = #{member.age},card_type = #{member.cardType},expire_time = #{member.expireTime},status = #{member.status} where id = #{id}")
   Integer updateById(@Param("id") long id, @Param("member") Member member);
   @Delete("delete from member where id = #{id}")
   Integer deleteMemberById(long id);
}
