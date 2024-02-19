package mealplanb.server.repository;

import mealplanb.server.domain.Member.Member;
import mealplanb.server.domain.Member.MemberStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByMemberId(Long memberId);
    Optional<Member> findByEmail(String email);
    boolean existsByEmailAndStatus(String email, MemberStatus a); // 이메일 중복 확인
    boolean existsByMemberIdAndStatus(Long memberId, MemberStatus a);
}