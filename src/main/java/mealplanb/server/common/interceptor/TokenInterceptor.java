package mealplanb.server.common.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import mealplanb.server.common.exception.MemberException;
import mealplanb.server.service.TokenService;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import static mealplanb.server.common.response.status.BaseExceptionResponseStatus.INVALID_TOKEN;

@Component
@RequiredArgsConstructor
public class TokenInterceptor implements HandlerInterceptor {

    private final TokenService tokenService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("Authorization");
        if (token != null && !token.isEmpty()) {
            // "Bearer " 접두사 제거
            token = token.replace("Bearer ", "");
            // 토큰 존재 여부 확인
            if (!tokenService.checkTokenExists(token)) {
                throw new MemberException(INVALID_TOKEN);
            }
        }
        return true;
    }

}
