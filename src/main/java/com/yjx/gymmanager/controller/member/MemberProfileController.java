package com.yjx.gymmanager.controller.member;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yjx.gymmanager.common.BusinessException;
import com.yjx.gymmanager.common.CurrentUser;
import com.yjx.gymmanager.common.Result;
import com.yjx.gymmanager.common.UserContext;
import com.yjx.gymmanager.dto.PasswordRequest;
import com.yjx.gymmanager.entity.Member;
import com.yjx.gymmanager.entity.SysUser;
import com.yjx.gymmanager.mapper.MemberMapper;
import com.yjx.gymmanager.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/member/profile")
@RequiredArgsConstructor
public class MemberProfileController {
    private final MemberMapper memberMapper;
    private final SysUserMapper sysUserMapper;

    @GetMapping
    public Result<Member> profile() {
        CurrentUser user = UserContext.get();
        return Result.ok(memberMapper.selectById(user.getRelatedId()));
    }

    @PutMapping
    public Result<Void> update(@RequestBody Member member) {
        CurrentUser user = UserContext.get();
        Member update = new Member();
        update.setId(user.getRelatedId());
        update.setPhone(member.getPhone());
        update.setGender(member.getGender());
        update.setAge(member.getAge());
        memberMapper.updateById(update);
        return Result.ok();
    }

    @PutMapping("/password")
    public Result<Void> updatePassword(@RequestBody PasswordRequest request) {
        CurrentUser user = UserContext.get();
        SysUser sysUser = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getId, user.getUserId())
                .eq(SysUser::getRole, "member"));
        if (sysUser == null) {
            throw new BusinessException("用户不存在");
        }
        if (request.getOldPassword() == null || !request.getOldPassword().equals(sysUser.getPassword())) {
            throw new BusinessException("旧密码不正确");
        }
        if (request.getNewPassword() == null || request.getNewPassword().isBlank()) {
            throw new BusinessException("请输入新密码");
        }
        sysUser.setPassword(request.getNewPassword());
        sysUserMapper.updateById(sysUser);
        return Result.ok();
    }
}
