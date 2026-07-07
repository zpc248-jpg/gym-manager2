package com.yjx.gymmanager.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yjx.gymmanager.common.CurrentUser;
import com.yjx.gymmanager.common.Result;
import com.yjx.gymmanager.common.UserContext;
import com.yjx.gymmanager.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {
    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        String authorization = request.getHeader("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            writeUnauthorized(response, "请先登录");
            return false;
        }
        try {
            CurrentUser user = jwtUtil.parseToken(authorization.substring(7));
            String uri = request.getRequestURI();
            if (uri.startsWith("/api/admin") && !"admin".equals(user.getRole())) {
                writeUnauthorized(response, "无管理员权限");
                return false;
            }
            if (uri.startsWith("/api/member") && !"member".equals(user.getRole())) {
                writeUnauthorized(response, "无会员权限");
                return false;
            }
            UserContext.set(user);
            return true;
        } catch (Exception exception) {
            writeUnauthorized(response, "登录已失效");
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.clear();
    }

    private void writeUnauthorized(HttpServletResponse response, String message) throws Exception {
        response.setStatus(200);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(Result.error(401, message)));
    }
}
