package com.yjx.gymmanager.controller.admin;

import com.yjx.gymmanager.common.Result;
import com.yjx.gymmanager.entity.Coach;
import com.yjx.gymmanager.mapper.CoachMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/coaches")
@RequiredArgsConstructor
public class AdminCoachController {
    private final CoachMapper coachMapper;

    @GetMapping
    public Result<List<Coach>> list() {
        return Result.ok(coachMapper.selectList(null));
    }

    @PostMapping
    public Result<Void> create(@RequestBody Coach coach) {
        coachMapper.insert(coach);
        return Result.ok();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Coach coach) {
        coach.setId(id);
        coachMapper.updateById(coach);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        coachMapper.deleteById(id);
        return Result.ok();
    }
}
