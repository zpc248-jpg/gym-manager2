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
import com.yjx.gymmanager.service.ProfileService;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/member/profile")
public class MemberProfileController {
    @Resource
    private MemberMapper memberMapper;
    @Resource
    private SysUserMapper sysUserMapper;
    @Resource
    private ProfileService profileService;

    @GetMapping
    public Result<Member> profile() {
      CurrentUser user = UserContext.get();
      long userId = user.getUserId();
      Member member = profileService.selectMemberById(userId);
      return Result.ok(member);
    }

    @PutMapping
    public Result<Void> update(@RequestBody Member member) {
        CurrentUser user = UserContext.get();
       long id = user.getUserId();
       profileService.updateMemberById(id,member);
        return Result.ok();
    }

    @PutMapping("/password")
    public Result<Void> updatePassword(@RequestBody PasswordRequest request) {
       CurrentUser user = UserContext.get();
       long userId = user.getUserId();
       profileService.updatePassword(userId,request);
        return Result.ok();
    }
}
