package com.yjx.gymmanager.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yjx.gymmanager.common.BusinessException;
import com.yjx.gymmanager.common.PageResult;
import com.yjx.gymmanager.common.Result;
import com.yjx.gymmanager.dto.AdminMemberRequest;
import com.yjx.gymmanager.entity.Member;
import com.yjx.gymmanager.entity.SysUser;
import com.yjx.gymmanager.mapper.MemberMapper;
import com.yjx.gymmanager.mapper.SysUserMapper;
import com.yjx.gymmanager.service.AdminDeleteService;
import com.yjx.gymmanager.service.MemberService;
import com.yjx.gymmanager.vo.AdminMemberVO;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/members")
public class AdminMemberController {
    @Resource
    private MemberMapper memberMapper;
    @Resource
    private SysUserMapper sysUserMapper;
    @Resource
    private AdminDeleteService adminDeleteService;
    @Resource
    private MemberService memberService;

    @GetMapping
    public Result<List<AdminMemberVO>> list() {
        List<AdminMemberVO> members = new ArrayList<>();
        members = memberService.getAllMember();
        return Result.ok(members);
    }

    @GetMapping("/page")
    public Result<PageResult<AdminMemberVO>> page(
            @RequestParam(defaultValue = "1") Long pageNum,
            @RequestParam(defaultValue = "10") Long pageSize,
            @RequestParam(required = false) String keyword) {
        return Result.ok(memberService.pageMember(pageNum, pageSize, keyword));
    }

    @PostMapping
    public Result<Void> create(@RequestBody AdminMemberRequest request) {
       memberService.addMember(request);
        return Result.ok();
    }

    @PutMapping("/{id}")
    @Transactional
    public Result<Void> update(@PathVariable Long id, @RequestBody AdminMemberRequest request) {
        memberService.updateMember(id, request);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        memberService.deleteMember(id);
        return Result.ok();
    }

    private void saveMemberAccount(Long memberId, String username, String password, boolean creatingMember) {
        SysUser currentUser = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getRole, "member")
                .eq(SysUser::getRelatedId, memberId)
                .last("limit 1"));
        boolean hasUsername = username != null && !username.isBlank();
        boolean hasPassword = password != null && !password.isBlank();

        if (!hasUsername && !hasPassword) {
            return;
        }
        if (!hasUsername && currentUser == null) {
            throw new BusinessException("请输入登录账号");
        }
        if (hasUsername) {
            Long duplicateCount = sysUserMapper.selectCount(new LambdaQueryWrapper<SysUser>()
                    .eq(SysUser::getUsername, username)
                    .ne(currentUser != null, SysUser::getId, currentUser == null ? null : currentUser.getId()));
            if (duplicateCount > 0) {
                throw new BusinessException("登录账号已存在");
            }
        }

        if (currentUser == null) {
            SysUser user = new SysUser();
            user.setUsername(username);
            user.setPassword(hasPassword ? password : "123456");
            user.setRole("member");
            user.setRelatedId(memberId);
            user.setStatus(1);
            sysUserMapper.insert(user);
            return;
        }

        if (hasUsername) {
            currentUser.setUsername(username);
        }
        if (hasPassword) {
            currentUser.setPassword(password);
        } else if (creatingMember && currentUser.getPassword() == null) {
            currentUser.setPassword("123456");
        }
        sysUserMapper.updateById(currentUser);
    }
}
