package com.yjx.gymmanager.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yjx.gymmanager.common.BusinessException;
import com.yjx.gymmanager.common.CurrentUser;
import com.yjx.gymmanager.common.Result;
import com.yjx.gymmanager.dto.LoginRequest;
import com.yjx.gymmanager.entity.SysUser;
import com.yjx.gymmanager.mapper.SysUserMapper;
import com.yjx.gymmanager.service.SysUserService;
import com.yjx.gymmanager.util.JwtUtil;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Resource
    private SysUserMapper sysUserMapper;
    @Resource
    private JwtUtil jwtUtil;
    @Resource
    private SysUserService sysUserService;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginRequest request) {
        SysUser user = sysUserService.loginUser(request);
        CurrentUser currentUser = new CurrentUser(user.getId(), user.getUsername(), user.getRole(), user.getRelatedId());
        String token = jwtUtil.createToken(currentUser);
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("userId", user.getId());
        data.put("username", user.getUsername());
        data.put("role", user.getRole());
        data.put("relatedId", user.getRelatedId());
        return Result.ok(data);
    }
}
