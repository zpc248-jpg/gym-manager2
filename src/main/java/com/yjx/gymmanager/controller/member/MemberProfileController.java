package com.yjx.gymmanager.controller.member;

import com.yjx.gymmanager.common.CurrentUser;
import com.yjx.gymmanager.common.Result;
import com.yjx.gymmanager.common.UserContext;
import com.yjx.gymmanager.entity.Member;
import com.yjx.gymmanager.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/member/profile")
@RequiredArgsConstructor
public class MemberProfileController {
    private final MemberMapper memberMapper;

    @GetMapping
    public Result<Member> profile() {
        CurrentUser user = UserContext.get();
        return Result.ok(memberMapper.selectById(user.getRelatedId()));
    }

    @PutMapping
    public Result<Void> update(@RequestBody Member member) {
        CurrentUser user = UserContext.get();
        member.setId(user.getRelatedId());
        memberMapper.updateById(member);
        return Result.ok();
    }
}
