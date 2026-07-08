package com.yjx.gymmanager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yjx.gymmanager.entity.Course;
import com.yjx.gymmanager.vo.CourseVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CourseMapper extends BaseMapper<Course> {

	@Select("select * from course where id = #{id}")
	Course getCourseById(@Param("id") Long id);

	@Select("""
			select
			  c.id,
			  c.name,
			  c.type,
			  c.coach_id as coachId,
			  co.name as coachName,
			  c.start_time as startTime,
			  c.end_time as endTime,
			  c.capacity,
			  c.booked_count as bookedCount,
			  c.status
			from course c
			left join coach co on c.coach_id = co.id
			order by c.start_time asc, c.id desc
			""")
	List<CourseVO> getCourseList();

	@Insert("insert into course (name, type, coach_id, start_time, end_time, capacity, booked_count, status) " +
			"values (#{name}, #{type}, #{coachId}, #{startTime}, #{endTime}, #{capacity}, #{bookedCount}, #{status})"	)
	void addCourse(Course course);


	@Update("""
			update course
			set
			  name = #{course.name},
			  type = #{course.type},
			  coach_id = #{course.coachId},
			  start_time = #{course.startTime},
			  end_time = #{course.endTime},
			  capacity = #{course.capacity},
			  booked_count = #{course.bookedCount},
			  status = #{course.status}
			where id = #{id}
			""")
	void updateCourse(@Param("id") Long id, @Param("course") Course course);


	@Delete("delete from course where id = #{id}")
	void deleteCourse(Long id);
}
