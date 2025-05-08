package hello.tis.hello_jpa.domain

import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToMany
import java.util.UUID

@Entity
class Account(
    @Id
    val accountNumber: String = UUID.randomUUID().toString(),
    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "source_account_number", referencedColumnName = "accountNumber")
    var withdrawTransactions: MutableSet<Transaction> = mutableSetOf(),
    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "target_account_number", referencedColumnName = "accountNumber")
    var depositTransactions: MutableSet<Transaction> = mutableSetOf()
) {
    val balance: Money
        get() {
            val outgoing = withdrawTransactions.fold(Money.ZERO) { acc, transaction ->
                acc + transaction.amount
            }

            val incoming = depositTransactions.fold(Money.ZERO) { acc, transaction ->
                acc + transaction.amount
            }

            return incoming - outgoing
        }

    fun deposit(transaction: Transaction) {
        this.depositTransactions.add(transaction)
    }

    fun withdraw(transaction: Transaction) {
        this.withdrawTransactions.add(transaction)
    }
}
