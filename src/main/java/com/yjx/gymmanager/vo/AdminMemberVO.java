package com.yjx.gymmanager.vo;

import com.yjx.gymmanager.entity.Member;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AdminMemberVO {
    private Long id;
    private String name;
    private String phone;
    private String gender;
    private Integer age;
    private String cardType;
    private LocalDate expireTime;
    private Integer status;
    private String username;

    public static AdminMemberVO from(Member member, String username) {
        AdminMemberVO vo = new AdminMemberVO();
        vo.setId(member.getId());
        vo.setName(member.getName());
        vo.setPhone(member.getPhone());
        vo.setGender(member.getGender());
        vo.setAge(member.getAge());
        vo.setCardType(member.getCardType());
        vo.setExpireTime(member.getExpireTime());
        vo.setStatus(member.getStatus());
        vo.setUsername(username);
        return vo;
    }
}
