package mealplanb.server.repository;

import mealplanb.server.domain.Member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

}
