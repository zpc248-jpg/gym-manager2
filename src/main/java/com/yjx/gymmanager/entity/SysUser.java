package com.yjx.gymmanager.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("sys_user")
public class SysUser {
    private Long id;
    private String username;
    private String password;
    private String role;
    private Long relatedId;
    private Integer status;
}
