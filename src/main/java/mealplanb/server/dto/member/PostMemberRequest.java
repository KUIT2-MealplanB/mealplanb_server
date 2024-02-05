package mealplanb.server.dto.member;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class PostMemberRequest {

    /** 회원 가입 */
//    @NotNull(message = "provider: {NotNull}")
//    private String provider; //'kakao', 'google', 'naver'
//
//    @NotNull
//    private String socialToken;

    @NotNull(message = "email: {NotNull}")
    private String email;
    @NotNull(message = "password: {NotNull}")
    private String password;
    @NotNull(message = "sex: {NotNull}")
    private String sex;
    @NotNull(message = "age: {NotNull}")
    private int age;
    @NotNull(message = "height: {NotNull}")
    private int height;
    @NotNull(message = "initial_weight: {NotNull}")
    private double initialWeight;
    @NotNull(message = "target_weight: {NotNull}")
    private double targetWeight;

    @NotNull(message = "diet_type: {NotNull}")
    private String dietType;

    @NotNull(message = "avatar_color: {NotNull}")
    private String avatarColor;

    @NotNull(message = "nickname: {NotNull}")
    private String nickname;
}