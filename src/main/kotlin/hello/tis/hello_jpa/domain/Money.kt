package hello.tis.hello_jpa.domain

class Money(
    val amount: Double = 0.0
) {
    companion object {
        val ZERO: Money = Money(0.0)
    }

    operator fun plus(other: Money): Money {
        return Money(this.amount + other.amount)
    }

    operator fun minus(other: Money): Money {
        return Money(this.amount - other.amount)
    }
}
