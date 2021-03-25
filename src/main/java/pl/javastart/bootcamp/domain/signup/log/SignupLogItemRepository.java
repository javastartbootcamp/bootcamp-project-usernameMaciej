package pl.javastart.bootcamp.domain.signup.log;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SignupLogItemRepository extends JpaRepository<SignupLogItem, Long> {
}
