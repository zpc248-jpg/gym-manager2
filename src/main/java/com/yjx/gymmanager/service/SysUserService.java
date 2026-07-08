package com.yjx.gymmanager.service;

import com.yjx.gymmanager.common.BusinessException;
import com.yjx.gymmanager.dto.LoginRequest;
import com.yjx.gymmanager.entity.SysUser;
import com.yjx.gymmanager.mapper.SysUserMapper;
import com.yjx.gymmanager.util.MD5Util;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class SysUserService {
    @Resource
    private SysUserMapper sysUserMapper;
    public SysUser loginUser(LoginRequest loginRequest) {
       SysUser sysUser = sysUserMapper.selectByUsername(loginRequest.getUsername());
       if (sysUser  ==  null){
           throw new BusinessException("用户不存在");
       }else if (!sysUser.getPassword().equals(MD5Util.md5(loginRequest.getPassword()))){
           throw new BusinessException("密码错误");
       } else if (sysUser.getStatus() != 1||sysUser == null) {
           throw new BusinessException("用户被禁用");
       }
        return sysUser;
    }
}
