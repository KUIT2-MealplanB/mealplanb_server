package mealplanb.server.common.response.status;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum BaseExceptionResponseStatus implements ResponseStatus {

    /**
     * 1000: 요청 성공 (OK)
     */
    SUCCESS(1000, HttpStatus.OK.value(), "요청에 성공하였습니다."),

    /**
     * 2000: Request 오류 (BAD_REQUEST)
     */
    BAD_REQUEST(2000, HttpStatus.BAD_REQUEST.value(), "유효하지 않은 요청입니다."),
    URL_NOT_FOUND(2001, HttpStatus.BAD_REQUEST.value(), "유효하지 않은 URL 입니다."),
    METHOD_NOT_ALLOWED(2002, HttpStatus.METHOD_NOT_ALLOWED.value(), "해당 URL에서는 지원하지 않는 HTTP Method 입니다."),
    UNSUPPORTED_STATISTIC_TYPE(2003, HttpStatus.BAD_REQUEST.value() ,"지원하지 않는 statisticType입니다. (daily, weekly, monthly)중 하나를 입력해주세요"),

    /**
     * 3000: Server, Database 오류 (INTERNAL_SERVER_ERROR)
     */
    SERVER_ERROR(3000, HttpStatus.INTERNAL_SERVER_ERROR.value(), "서버에서 오류가 발생하였습니다."),
    DATABASE_ERROR(3001, HttpStatus.INTERNAL_SERVER_ERROR.value(), "데이터베이스에서 오류가 발생하였습니다."),
    BAD_SQL_GRAMMAR(3002, HttpStatus.INTERNAL_SERVER_ERROR.value(), "SQL에 오류가 있습니다."),

    /**
     * 4000: Authorization 오류
     */
    JWT_ERROR(4000, HttpStatus.UNAUTHORIZED.value(), "JWT에서 오류가 발생하였습니다."),
    TOKEN_NOT_FOUND(4001, HttpStatus.BAD_REQUEST.value(), "토큰이 HTTP Header에 없습니다."),
    UNSUPPORTED_TOKEN_TYPE(4002, HttpStatus.BAD_REQUEST.value(), "지원되지 않는 토큰 형식입니다."),
    INVALID_TOKEN(4003, HttpStatus.UNAUTHORIZED.value(), "유효하지 않은 토큰입니다."),
    MALFORMED_TOKEN(4004, HttpStatus.UNAUTHORIZED.value(), "토큰이 올바르게 구성되지 않았습니다."),
    EXPIRED_TOKEN(4005, HttpStatus.UNAUTHORIZED.value(), "만료된 토큰입니다."),
    TOKEN_MISMATCH(4006, HttpStatus.UNAUTHORIZED.value(), "로그인 정보가 토큰 정보와 일치하지 않습니다."),

    /**
     * 5000: User 오류
     */
    INVALID_USER_VALUE(5000, HttpStatus.BAD_REQUEST.value(), "회원가입 요청에서 잘못된 값이 존재합니다."),
    MEMBER_NOT_FOUND(5001, HttpStatus.NOT_FOUND.value(), "회원을 찾을 수 없습니다."),
    DUPLICATE_EMAIL(5002, HttpStatus.BAD_REQUEST.value(), "이미 존재하는 이메일입니다."),
    DUPLICATE_NICKNAME(5003, HttpStatus.BAD_REQUEST.value(), "이미 존재하는 닉네임입니다."),
    PASSWORD_NO_MATCH(5004, HttpStatus.BAD_REQUEST.value(), "비밀번호가 일치하지 않습니다."),
    INVALID_USER_STATUS(5005, HttpStatus.BAD_REQUEST.value(), "잘못된 회원 status 값입니다."),
    EMAIL_NOT_FOUND(5006, HttpStatus.BAD_REQUEST.value(), "존재하지 않는 이메일입니다."),
    RATIO_NOT_CORRECT(5007,HttpStatus.BAD_REQUEST.value(), "탄,단,지 비율 합이 100이 되지 않습니다."),
    /**
     * 6000: Food 오류
     */
    FOOD_NOT_FOUND(6001, HttpStatus.NOT_FOUND.value(), "식품을 찾을 수 없습니다."),

    /**
     * 7000: Meal 오류
     */
    INVALID_MEAL_VALUE(7000, HttpStatus.BAD_REQUEST.value(), "식단 요청에서 잘못된 값이 존재합니다."),
    MEAL_NOT_FOUND(7001, HttpStatus.NOT_FOUND.value(), "식단을 찾을 수 없습니다."),
    DUPLICATE_MEAL(7003, HttpStatus.BAD_REQUEST.value(), "이미 존재하는 식단입니다."),
    UNAUTHORIZED_ACCESS(7004, HttpStatus.UNAUTHORIZED.value() , "해당 식단에 대해 권한이 없는 멤버입니다."),

    /**
     * 8000: Weight 오류
     */
    WEIGHT_NOT_FOUND(8000, HttpStatus.BAD_REQUEST.value(), "해당 유저의 체중을 찾을 수 없습니다.");

    private final int code;
    private final int status;
    private final String message;

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return message;
    }

}
