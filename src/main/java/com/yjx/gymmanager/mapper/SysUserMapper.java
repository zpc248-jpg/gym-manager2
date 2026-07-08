package com.yjx.gymmanager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yjx.gymmanager.entity.SysUser;
import org.apache.ibatis.annotations.*;

@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
    @Select("select * from sys_user where username = #{username}")
    SysUser selectByUsername(String username);
    @Select("select * from sys_user where related_id = #{id}")
    SysUser selectOne(long  id);
    @Insert("insert into sys_user(username,password,role,related_id,status,create_time,update_time) values(#{username},#{password},#{role},#{relatedId},#{status},#{createTime},#{updateTime})")
    Integer addUser(SysUser sysUser);
    @Update("update sys_user set username = #{user.username},password = #{user.password},status = #{user.status},update_time = #{user.updateTime} where related_id = #{id}")
    void updateUser(@Param("id") long id, @Param("user") SysUser user);
    @Select("select * from sys_user where related_id = #{id}")
    SysUser selectUserById(Long id);
    @Delete("delete from sys_user where related_id = #{id}")
    Integer deleteUserById(@Param("id") Long id);
}
