package com.yjx.gymmanager.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yjx.gymmanager.common.BusinessException;
import com.yjx.gymmanager.common.PageResult;
import com.yjx.gymmanager.dto.AdminMemberRequest;
import com.yjx.gymmanager.entity.Member;
import com.yjx.gymmanager.entity.SysUser;
import com.yjx.gymmanager.mapper.MemberMapper;
import com.yjx.gymmanager.mapper.SysUserMapper;
import com.yjx.gymmanager.util.MD5Util;
import com.yjx.gymmanager.vo.AdminMemberVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
public class MemberService {
    @Resource
    private MemberMapper memberMapper;
    @Resource
    private SysUserMapper sysUserMapper;
    public List<AdminMemberVO> getAllMember() {
        List<AdminMemberVO> members = memberMapper.selectAll();
        return members;
    }

    public PageResult<AdminMemberVO> pageMember(Long pageNum, Long pageSize, String keyword) {
        long current = pageNum == null || pageNum < 1 ? 1 : pageNum;
        long size = pageSize == null || pageSize < 1 ? 10 : Math.min(pageSize, 100);
        String searchText = keyword == null ? "" : keyword.trim();
        return PageResult.of(memberMapper.pageMember(new Page<>(current, size), searchText));
    }
    @Transactional
    public void addMember(AdminMemberRequest request) {
        Member member = request.getMember();
        checkMember(member, request.getUsername(), request.getPassword());
        memberMapper.addMember(member);
        SysUser user = new SysUser();
        user.setUsername(request.getUsername());
        user.setPassword(MD5Util.md5(request.getPassword()));
        user.setRole("member");
        user.setRelatedId(memberMapper.selectIdByName(member.getName()).longValue());
        user.setStatus(1);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        sysUserMapper.addUser(user);
    }
    @Transactional
    public void updateMember(long  id,AdminMemberRequest request) {
        Member member = request.getMember();
        SysUser user = new SysUser();
      /*  checkMember(member, request.getUsername(), request.getPassword());*/
        memberMapper.updateById(id, member);
        if (sysUserMapper.selectOne(id) == null){
            user.setUsername(request.getUsername());
            user.setPassword(MD5Util.md5(request.getPassword()));
            user.setRole("member");
            user.setRelatedId(id);
            user.setStatus(member.getStatus());
            user.setCreateTime(LocalDateTime.now());
            user.setUpdateTime(LocalDateTime.now());
            sysUserMapper.addUser(user);
        }else{
            user.setUpdateTime(LocalDateTime.now());
            user.setUsername(request.getUsername());
            user.setPassword(MD5Util.md5(request.getPassword()));
            user.setStatus(member.getStatus());
            sysUserMapper.updateUser(id,user);
        }
    }
    @Transactional
    public void deleteMember(long id) {
       if (memberMapper.selectMemberById(id) == null||sysUserMapper.selectUserById(id) == null){
           throw new BusinessException("会员不存在");
       }
       if ( memberMapper.deleteMemberById(id) < 1|| sysUserMapper.deleteUserById(id) < 1){
           throw new BusinessException("会员删除失败");
       }
    }

    private void checkMember(Member member, String username, String password) {
        if (member == null){
            throw new BusinessException("会员信息不能为空");
        }
        if (member.getName() == null || member.getName().isEmpty()){
            throw new BusinessException("会员姓名不能为空");
        }
        if (member.getPhone() == null || member.getPhone().isEmpty()){
            throw new BusinessException("会员手机号不能为空");
        }
        if (member.getGender() == null || member.getGender().isEmpty()){
            throw new BusinessException("会员性别不能为空");
        }
        if (member.getAge() == null || member.getAge() < 0){
            throw new BusinessException("会员年龄不能为空");
        }
        if (member.getCardType() == null || member.getCardType().isEmpty()){
            throw new BusinessException("会员会员类型不能为空");
        }
        if (member.getExpireTime() == null){
            throw new BusinessException("会员到期时间不能为空");
        }
        if (username == null || username.isEmpty()){
            throw new BusinessException("会员登录账号不能为空");
        }else if(sysUserMapper.selectByUsername(username) != null){
            throw new BusinessException("会员登录账号已存在");
        }
        if (password == null || password.isEmpty()){
            throw new BusinessException("会员登录密码不能为空");
        }
    }
}
