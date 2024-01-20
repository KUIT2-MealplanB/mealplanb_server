package mealplanb.server.util.jwt;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import mealplanb.server.common.exception.jwt.bad_request.JwtUnsupportedTokenException;
import mealplanb.server.common.exception.jwt.unauthorized.JwtInvalidTokenException;
import mealplanb.server.common.exception.jwt.unauthorized.JwtMalformedTokenException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

import static mealplanb.server.common.response.status.BaseExceptionResponseStatus.*;

@Slf4j
@Component
public class JwtProvider {

    @Value("${secret.jwt-secret-key}")
    private String JWT_SECRET_KEY;

    @Value("${secret.jwt-expired-in}")
    private long JWT_EXPIRED_IN;
    public String extractJwtToken(String authorizationHeader) {
        String[] parts = authorizationHeader.split(" ");
        if (parts.length == 2) {
            return parts[1]; // 토큰 부분 추출
        }
        throw new JwtMalformedTokenException(MALFORMED_TOKEN);
    }

    public Long extractMemberIdFromJwtToken(String jwtToken) {
        Claims claims = Jwts.parserBuilder().setSigningKey(JWT_SECRET_KEY).build().parseClaimsJws(jwtToken).getBody();
        Long memberId = claims.get("memberId", Long.class);

        if (memberId == null) {
            throw new JwtMalformedTokenException(MALFORMED_TOKEN);
        }

        return memberId;
    }
}