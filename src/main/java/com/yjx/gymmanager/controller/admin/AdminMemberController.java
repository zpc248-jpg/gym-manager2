package com.yjx.gymmanager.controller.admin;

import com.yjx.gymmanager.common.Result;
import com.yjx.gymmanager.entity.Member;
import com.yjx.gymmanager.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/members")
@RequiredArgsConstructor
public class AdminMemberController {
    private final MemberMapper memberMapper;

    @GetMapping
    public Result<List<Member>> list() {
        return Result.ok(memberMapper.selectList(null));
    }

    @PostMapping
    public Result<Void> create(@RequestBody Member member) {
        memberMapper.insert(member);
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
        memberMapper.deleteById(id);
        return Result.ok();
    }
}
