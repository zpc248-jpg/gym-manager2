package com.yjx.gymmanager.dto;

import com.yjx.gymmanager.entity.Member;
import lombok.Data;

@Data
public class AdminMemberRequest {
    private Member member;
    private String username;
    private String password;
}
