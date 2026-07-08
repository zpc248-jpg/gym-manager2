package com.yjx.gymmanager.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate expireTime;
    private Integer status;
    private String username;
    private String password;
}
