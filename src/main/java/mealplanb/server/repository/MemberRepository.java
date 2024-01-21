package mealplanb.server.repository;

import mealplanb.server.domain.Member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {


    Optional<Member> findByEmail(String email); // 이메일로 유저 찾기
    boolean existsByEmail(String email); // 이메일 중복 확인
  
    Optional<Member> findById(Long memberId); // 사용자 id 로 멤버를 찾는 메소드

}
