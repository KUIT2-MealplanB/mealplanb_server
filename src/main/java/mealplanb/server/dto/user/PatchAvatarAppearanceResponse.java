package mealplanb.server.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

import javax.swing.*;

@Getter
@AllArgsConstructor
public class PatchAvatarAppearanceResponse {

    /** 아바타 외형 수정 */
    private String avatarAppearance;
}
