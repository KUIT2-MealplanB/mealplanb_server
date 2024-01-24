package mealplanb.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mealplanb.server.common.exception.MemberException;

import mealplanb.server.domain.Member.Member;
import mealplanb.server.dto.user.PostMemberRequest;
import mealplanb.server.dto.user.PostMemberResponse;
import mealplanb.server.repository.MemberRepository;
import mealplanb.server.util.jwt.JwtProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static mealplanb.server.common.response.status.BaseExceptionResponseStatus.DUPLICATE_EMAIL;

import mealplanb.server.common.response.status.BaseExceptionResponseStatus;
import mealplanb.server.dto.user.GetAvatarResponse;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final AvatarService avatarService;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public PostMemberResponse signUp(PostMemberRequest postUserRequest){
        log.info("[MemberService.signUp]");

        validateEmail(postUserRequest.getEmail()); // 이메일 유효성 검사

        Member member = createAndSaveMember(postUserRequest);
        String jwt = jwtProvider.createToken(postUserRequest.getEmail(), member.getMemberId());
        return new PostMemberResponse(member.getMemberId(), jwt);
    }

    private Member createAndSaveMember(PostMemberRequest postUserRequest) {
        String encodedPassword = passwordEncoder.encode(postUserRequest.getPassword());
        postUserRequest.setPassword(encodedPassword);
        Member member = new Member(
                postUserRequest.getEmail(),
                postUserRequest.getPassword(),
                postUserRequest.getSex(),
                postUserRequest.getAge(),
                postUserRequest.getHeight(),
                postUserRequest.getInitialWeight(),
                postUserRequest.getTargetWeight(),
                postUserRequest.getDietType(),
                postUserRequest.getAvatarColor(),
                postUserRequest.getNickname()
        );
        return memberRepository.save(member);
    }

    private void validateEmail(String email) {
        if(memberRepository.existsByEmail(email)){
            throw new MemberException(DUPLICATE_EMAIL);
        }
    }
    

    /** 아바타 정보 조회 */
    @Transactional(readOnly = true)
    public GetAvatarResponse getAvatarResponse(Long memberId){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(()-> new MemberException(BaseExceptionResponseStatus.MEMBER_NOT_FOUND));
        String avatarAppearance = avatarService.calculateAvatarAppearance(member);
        return new GetAvatarResponse(avatarAppearance, member.getAvatarColor(), member.getNickname());
    }
}
