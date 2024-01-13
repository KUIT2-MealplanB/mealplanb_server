package mealplanb.server.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserJoinRequest {

    /** 회원 가입 */
    @NotNull(message = "provider: {NotNull}")
    private String provider; //'kakao', 'google', 'naver'

    @NotNull
    private String socialToken;

    @NotNull(message = "sex: {NotNull}")
    private String sex;

    @NotNull(message = "age: {NotNull}")
    private int age;

    @NotNull(message = "height: {NotNull}")
    private int height;

    @NotNull(message = "initial_weight: {NotNull}")
    @JsonProperty("initial_weight")
    private double initialWeight;

    @NotNull(message = "target_weight: {NotNull}")
    @JsonProperty("target_weight")
    private double targetWeight;

    @NotNull(message = "diet_type: {NotNull}")
    @JsonProperty("diet_type")
    private double dietType;

    @NotNull(message = "avatar_color: {NotNull}")
    @JsonProperty("avatar_color")
    private double avatarColor;

    @NotNull(message = "nickname: {NotNull}")
    private double nickname;
}
