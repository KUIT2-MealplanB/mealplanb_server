package mealplanb.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mealplanb.server.common.exception.MemberException;
import mealplanb.server.common.response.BaseResponse;
import mealplanb.server.common.response.status.BaseExceptionResponseStatus;
import mealplanb.server.domain.Member.Member;
import mealplanb.server.dto.user.GetAvatarResponse;
import mealplanb.server.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final AvatarService avatarService;

    /** 아바타 정보 조회 */
    @Transactional(readOnly = true)
    public GetAvatarResponse getAvatarResponse(Long memberId){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(()-> new MemberException(BaseExceptionResponseStatus.MEMBER_NOT_FOUND));
        String avatarAppearance = avatarService.calculateAvatarAppearance(member);
        return new GetAvatarResponse(avatarAppearance, member.getAvatarColor(), member.getNickname());
    }

}
