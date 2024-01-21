package mealplanb.server.repository;

import mealplanb.server.domain.Member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email); // 이메일로 유저 찾기
    boolean existsByEmail(String email); // 이메일 중복 확인
}
