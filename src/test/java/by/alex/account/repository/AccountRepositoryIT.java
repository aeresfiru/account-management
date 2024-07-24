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
class AccountRepositoryIT {

    @Autowired
    private AccountRepository accountRepository;

    @Test
    void findByUserId_ReturnsAccount() {
        // given
        int userId = 1;

        // when
        var actual = this.accountRepository.findByUserId(userId);

        // then
        assertThat(actual).isPresent().get().hasNoNullFieldsOrProperties();
        assertThat(actual.get().getUser().getId()).isEqualTo(1);
    }
}
