package hello.tis.hello_jpa.domain

import hello.tis.hello_jpa.repository.AccountRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class AccountService(
    private val accountRepository: AccountRepository,
) {
    fun createAccount(account: Account): Account {
        return accountRepository.save(account)
    }

    fun getAccount(accountNumber: String): Account {
        return accountRepository.findByIdOrNull(accountNumber) ?: throw IllegalArgumentException("Account not found")
    }

    fun getAllAccounts(): List<Account> {
        return accountRepository.findAll()
    }

    fun flush() {
        accountRepository.flush()
    }
}
