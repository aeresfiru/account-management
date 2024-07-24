package by.alex.account.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryIT {

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByUsername_ReturnsUser() {
        // given
        String username = "user1";

        // when
        var user = this.userRepository.findByUsername(username);

        // then
        assertThat(user).isPresent().get().hasNoNullFieldsOrProperties();
        assertThat(user.get().getUsername()).isEqualTo("user1");
    }
}
