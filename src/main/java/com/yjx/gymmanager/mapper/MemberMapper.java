package com.yjx.gymmanager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yjx.gymmanager.entity.Member;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MemberMapper extends BaseMapper<Member> {
}
