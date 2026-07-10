package com.yjx.gymmanager.service;

import com.yjx.gymmanager.common.BusinessException;
import com.yjx.gymmanager.dto.PasswordRequest;
import com.yjx.gymmanager.entity.Member;
import com.yjx.gymmanager.mapper.MemberMapper;
import com.yjx.gymmanager.mapper.SysUserMapper;
import com.yjx.gymmanager.util.MD5Util;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class ProfileService {
    @Resource
    private MemberMapper memberMapper;
    @Resource
    private SysUserMapper sysUserMapper;
    public Member selectMemberById(long userId) {
        if (userId <= 0){
            throw new BusinessException("用户ID错误");
        }
        //通过userId查询sys_user表，获取relatedId，即会员ID
        long id = sysUserMapper.selectUserByUserId(userId).getRelatedId();
        if (memberMapper.selectMemberById(id) == null){
            throw new BusinessException("会员不存在");
        }
        return memberMapper.selectMemberById(id);
    }

    public void updateMemberById(long userId, Member member) {
        if (userId <= 0){
            throw new BusinessException("用户ID错误");
        }
        long id = sysUserMapper.selectUserByUserId(userId).getRelatedId();
        if (memberMapper.selectMemberById(id) == null){
            throw new BusinessException("会员不存在");
        }
        if (memberMapper.updateById(id, member) < 1){
            throw new BusinessException("会员更新失败");
        }
    }

    public void updatePassword(long userId, PasswordRequest request) {
        if (userId <= 0){
            throw new BusinessException("用户ID错误");
        }
        if (sysUserMapper.selectUserByUserId(userId) == null){
            throw new BusinessException("用户不存在");
        }
        if (request.getOldPassword() == null){
            throw new BusinessException("请输入旧密码");
        } else if (request.getNewPassword() == null) {
            throw new BusinessException("请输入新密码");
        }else if (!MD5Util.md5(request.getOldPassword()).equals(sysUserMapper.selectUserByUserId(userId).getPassword())){
            throw new BusinessException("旧密码错误");
        } else if (request.getNewPassword().equals(request.getOldPassword())) {
            throw new BusinessException("新密码不能与旧密码相同");
        }
        String newPassword = MD5Util.md5(request.getNewPassword());
        if (sysUserMapper.updateUserPassword(userId, newPassword) < 1){
            throw new BusinessException("密码更新失败");
        }
    }
}
