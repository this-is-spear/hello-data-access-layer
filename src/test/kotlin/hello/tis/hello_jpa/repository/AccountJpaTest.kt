package hello.tis.hello_jpa.repository

import hello.tis.hello_jpa.domain.Account
import io.hypersistence.utils.jdbc.validator.SQLStatementCountValidator
import jakarta.persistence.EntityManagerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.stream.IntStream
import kotlin.streams.toList
import kotlin.test.Test


@SpringBootTest
class AccountJpaTest {
    @Autowired
    private lateinit var accountRepository: AccountRepository

    @Autowired
    private lateinit var entityManagerFactory: EntityManagerFactory

    @Test
    fun `test1`() {
        SQLStatementCountValidator.reset()
        val accounts = IntStream.range(0, 20).toList().map { Account() }
        accountRepository.saveAll(accounts)
        accountRepository.flush()
        SQLStatementCountValidator.assertInsertCount(20)
    }

    @Test
    fun `test2`() {
        SQLStatementCountValidator.reset()
        entityManagerFactory.createEntityManager().use { entityManager ->
            entityManager.transaction.begin()
            val accounts = IntStream.range(0, 20).toList().map { Account() }
            accounts.forEach { entityManager.persist(it) }
            entityManager.transaction.commit()
        }
        SQLStatementCountValidator.assertInsertCount(20)
    }
}
