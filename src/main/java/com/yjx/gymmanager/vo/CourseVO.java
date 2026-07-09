package com.yjx.gymmanager.vo;

import com.yjx.gymmanager.entity.Course;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

@Data
public class CourseVO {
    private Long id;
    private String name;
    private String type;
    private Long coachId;
    private String coachName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer capacity;
    private Integer bookedCount;
    private Integer status;
    private String timeStatus;

    public static CourseVO from(Course course, String coachName) {
        CourseVO vo = new CourseVO();
        BeanUtils.copyProperties(course, vo);
        vo.setCoachName(coachName);
        return vo;
    }

    public void fillTimeStatus() {
        if (status != null && status == 0) {
            timeStatus = "停课";
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        if (startTime != null && now.isBefore(startTime)) {
            timeStatus = "未开始";
        } else if (endTime != null && now.isAfter(endTime)) {
            timeStatus = "已结束";
        } else {
            timeStatus = "进行中";
        }
    }
}
