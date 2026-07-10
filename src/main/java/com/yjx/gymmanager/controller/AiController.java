package com.yjx.gymmanager.controller;

import com.yjx.gymmanager.common.BusinessException;
import com.yjx.gymmanager.common.CurrentUser;
import com.yjx.gymmanager.common.Result;
import com.yjx.gymmanager.common.UserContext;
import com.yjx.gymmanager.dto.AiChatRequest;
import com.yjx.gymmanager.service.AiService;
import com.yjx.gymmanager.vo.AiChatVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ai")
public class AiController {
    private final AiService aiService;

    @PostMapping("/chat")
    public Result<AiChatVO> chat(@RequestBody AiChatRequest request) {
        CurrentUser currentUser = UserContext.get();
        if (currentUser == null || (!"admin".equals(currentUser.getRole()) && !"member".equals(currentUser.getRole()))) {
            throw new BusinessException(403, "需要登录后使用");
        }
        String reply = aiService.chat(request);
        return Result.ok(new AiChatVO(reply));
    }
}
