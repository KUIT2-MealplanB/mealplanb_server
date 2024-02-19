package mealplanb.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {

    @Value("${secret.jwt-expired-in}")
    private long JWT_EXPIRED_IN;

    private final RedisTemplate<String, Object> redisTemplate;

    public void storeToken(String token, long memberId) {
        // redis 에 토큰 저장
        redisTemplate.opsForValue().set(token, memberId, JWT_EXPIRED_IN, TimeUnit.MILLISECONDS);
    }

    public boolean checkTokenExists(String token) {
        // 들어온 토큰이 redis 에 있는지 확인
        Boolean result = redisTemplate.hasKey(token);
        return result != null && result;
    }

    public void invalidateToken(String token) {
        // redis 에서 토큰 삭제
        redisTemplate.delete(token);
    }

}
