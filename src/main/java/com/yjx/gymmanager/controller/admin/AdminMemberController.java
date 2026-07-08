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
import com.yjx.gymmanager.vo.AdminMemberVO;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/members")
@RequiredArgsConstructor
public class AdminMemberController {
    private final MemberMapper memberMapper;
    private final SysUserMapper sysUserMapper;
    private final AdminDeleteService adminDeleteService;

    @GetMapping
    public Result<List<AdminMemberVO>> list() {
        List<Member> members = memberMapper.selectList(null);
        List<Long> memberIds = members.stream()
                .map(Member::getId)
                .filter(Objects::nonNull)
                .toList();
        Map<Long, SysUser> userMap = memberIds.isEmpty() ? Map.of() : sysUserMapper.selectList(new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getRole, "member")
                        .in(SysUser::getRelatedId, memberIds))
                .stream()
                .collect(Collectors.toMap(SysUser::getRelatedId, Function.identity(), (left, right) -> left));
        List<AdminMemberVO> rows = members.stream()
                .map(member -> AdminMemberVO.from(member, userMap.get(member.getId()) == null ? "" : userMap.get(member.getId()).getUsername()))
                .toList();
        return Result.ok(rows);
    }

    @PostMapping
    @Transactional
    public Result<Void> create(@RequestBody AdminMemberRequest request) {
        Member member = request.getMember();
        memberMapper.insert(member);
        saveMemberAccount(member.getId(), request.getUsername(), request.getPassword(), true);
        return Result.ok();
    }

    @PutMapping("/{id}")
    @Transactional
    public Result<Void> update(@PathVariable Long id, @RequestBody AdminMemberRequest request) {
        Member member = request.getMember();
        member.setId(id);
        memberMapper.updateById(member);
        saveMemberAccount(id, request.getUsername(), request.getPassword(), false);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        adminDeleteService.deleteMember(id);
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
