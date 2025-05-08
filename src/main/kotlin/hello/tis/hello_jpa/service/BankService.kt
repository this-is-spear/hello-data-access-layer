package hello.tis.hello_jpa.service

import hello.tis.hello_jpa.domain.Account
import hello.tis.hello_jpa.domain.AccountService
import hello.tis.hello_jpa.domain.Money
import hello.tis.hello_jpa.domain.Transaction
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BankService(
    private val accountService: AccountService
) {
    @Transactional
    fun transfer(sourceAccountNumber: String, targetAccountNumber: String, amount: Money) {
        val sourceAccount = accountService.getAccount(sourceAccountNumber)
        val targetAccount = accountService.getAccount(targetAccountNumber)

        val transaction = Transaction(
            sourceAccountNumber = sourceAccount.accountNumber,
            targetAccountNumber = targetAccount.accountNumber,
            amount = amount
        )

        sourceAccount.withdraw(transaction)
        targetAccount.deposit(transaction)
        accountService.flush()
    }

    @Transactional
    fun createAccount(accountNumber: String): String {
        val account = if (accountNumber.isNotBlank()) {
            Account(accountNumber = accountNumber)
        } else {
            Account()
        }
        val createdAccount = accountService.createAccount(account)
        return createdAccount.accountNumber
    }

    @Transactional(readOnly = true)
    fun getBalance(accountNumber: String): Money {
        return accountService.getAccount(accountNumber).balance
    }

    @Transactional(readOnly = true)
    fun getAccountList(): List<Account> {
        return accountService.getAllAccounts()
    }
}
