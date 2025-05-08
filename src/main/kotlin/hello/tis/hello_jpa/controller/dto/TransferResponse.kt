package hello.tis.hello_jpa.controller.dto

import java.time.LocalDate

data class TransferResponse(
    val sourceAccountNumber: String,
    val targetAccountNumber: String,
    val amount: Double,
    val timestamp: LocalDate = LocalDate.now()
)
