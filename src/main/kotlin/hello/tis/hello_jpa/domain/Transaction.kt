package hello.tis.hello_jpa.domain

import jakarta.persistence.*
import java.time.LocalDate
import java.util.UUID

@Entity
class Transaction(
    @Column(name = "source_account_number")
    val sourceAccountNumber: String,

    @Column(name = "target_account_number")
    val targetAccountNumber: String,

    @Embedded
    val amount: Money = Money.ZERO,

    val transactionDate: LocalDate = LocalDate.now(),

    @Id
    val transactionId: String = UUID.randomUUID().toString()
) {
    init {
        require(sourceAccountNumber != targetAccountNumber) { "Cannot transfer to the same account" }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Transaction

        return transactionId == other.transactionId
    }

    override fun hashCode(): Int {
        return transactionId.hashCode()
    }
}
