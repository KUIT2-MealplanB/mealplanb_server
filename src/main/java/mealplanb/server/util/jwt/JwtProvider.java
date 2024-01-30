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

    public String createToken(String principal, long memberId) {
        log.info("JWT key={}", JWT_SECRET_KEY);

        Claims claims = Jwts.claims().setSubject(principal);
        Date now = new Date();
        Date validity = new Date(now.getTime() + JWT_EXPIRED_IN);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .claim("memberId", memberId)
                .signWith(SignatureAlgorithm.HS256, JWT_SECRET_KEY)
                .compact();
    }

    public boolean isExpiredToken(String token) throws JwtInvalidTokenException {
        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(JWT_SECRET_KEY).build()
                    .parseClaimsJws(token);
            return claims.getBody().getExpiration().before(new Date());

        } catch (ExpiredJwtException e) {
            return true;

        } catch (UnsupportedJwtException e) {
            throw new JwtUnsupportedTokenException(UNSUPPORTED_TOKEN_TYPE);
        } catch (MalformedJwtException e) {
            throw new JwtMalformedTokenException(MALFORMED_TOKEN);
        } catch (IllegalArgumentException e) {
            throw new JwtInvalidTokenException(INVALID_TOKEN);
        } catch (JwtException e) {
            log.error("[JwtTokenProvider.validateAccessToken]", e);
            throw e;
        }
    }

    public String getPrincipal(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(JWT_SECRET_KEY).build()
                .parseClaimsJws(token)
                .getBody().getSubject();
    }

    public Long extractIdFromHeader(String authorization){
        // Authorization 헤더에서 JWT 토큰 추출
        String jwtToken = extractJwtToken(authorization);

        // JWT 토큰에서 사용자 정보 추출
        Long memberId = extractMemberIdFromJwtToken(jwtToken);
        return memberId;
    }

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

