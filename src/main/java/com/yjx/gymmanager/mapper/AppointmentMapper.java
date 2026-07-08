package com.yjx.gymmanager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yjx.gymmanager.entity.Appointment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AppointmentMapper extends BaseMapper<Appointment> {

	@Select("select * from appointment where course_id = #{id};")
	List<Appointment> selectByCourseId(@Param("id") Long id);
}
