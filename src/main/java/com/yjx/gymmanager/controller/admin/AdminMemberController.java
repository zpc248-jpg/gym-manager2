package com.yjx.gymmanager.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yjx.gymmanager.common.BusinessException;
import com.yjx.gymmanager.common.Result;
import com.yjx.gymmanager.dto.AdminMemberRequest;
import com.yjx.gymmanager.entity.Member;
import com.yjx.gymmanager.entity.SysUser;
import com.yjx.gymmanager.mapper.MemberMapper;
import com.yjx.gymmanager.mapper.SysUserMapper;
import com.yjx.gymmanager.service.AdminDeleteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/members")
@RequiredArgsConstructor
public class AdminMemberController {
    private final MemberMapper memberMapper;
    private final SysUserMapper sysUserMapper;
    private final AdminDeleteService adminDeleteService;

    @GetMapping
    public Result<List<Member>> list() {
        return Result.ok(memberMapper.selectList(null));
    }

    @PostMapping
    public Result<Void> create(@RequestBody AdminMemberRequest request) {
        Member member = request.getMember();
        memberMapper.insert(member);
        if (request.getUsername() != null && !request.getUsername().isBlank()) {
            Long count = sysUserMapper.selectCount(new LambdaQueryWrapper<SysUser>()
                    .eq(SysUser::getUsername, request.getUsername()));
            if (count > 0) {
                throw new BusinessException("Username already exists");
            }
            SysUser user = new SysUser();
            user.setUsername(request.getUsername());
            user.setPassword(request.getPassword() == null || request.getPassword().isBlank() ? "123456" : request.getPassword());
            user.setRole("member");
            user.setRelatedId(member.getId());
            user.setStatus(1);
            sysUserMapper.insert(user);
        }
        return Result.ok();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Member member) {
        member.setId(id);
        memberMapper.updateById(member);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        adminDeleteService.deleteMember(id);
        return Result.ok();
    }
}
