package com.yjx.gymmanager.mapper;

import com.yjx.gymmanager.entity.Appointment;
import com.yjx.gymmanager.vo.AppointmentVO;
import com.yjx.gymmanager.vo.CourseVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface MemberAppointmentMapper {
    @Select("SELECT " +
            "a.id, " +
            "a.member_id AS memberId, " +
            "m.name AS memberName, " +
            "a.course_id AS courseId, " +
            "c.name AS courseName, " +
            "c.start_time AS courseTime, " +
            "ch.name AS coachName, " +
            "a.status, " +
            "a.create_time AS createTime " +
            "FROM appointment a " +
            "INNER JOIN course c ON a.course_id = c.id AND a.member_id = #{memberId} " +
            "LEFT JOIN coach ch ON c.coach_id = ch.id " +
            "LEFT JOIN member m ON a.member_id = m.id")
    List<AppointmentVO> selectCourseByMemberId(@Param("memberId") Long memberId);
    @Insert("INSERT INTO appointment(member_id, course_id, status, create_time,update_time) VALUES(#{memberId}, #{courseId}, #{status}, #{createTime},#{updateTime})")
    Integer insert(Appointment appointment);
   @Select("SELECT * FROM appointment WHERE course_id = #{courseId} AND member_id = #{memberId}")
    Appointment courseCount(@Param("courseId") Long courseId, @Param("memberId") Long memberId);
   @Update("UPDATE appointment SET status = 'reserved' WHERE member_id = #{memberId} AND course_id = #{courseId}")
    void updateAppointment(long memberId, Long courseId);
}
