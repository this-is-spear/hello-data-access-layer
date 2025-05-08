package hello.tis.hello_jpa.controller.dto

data class TransferRequest(
    val sourceAccountNumber: String,
    val targetAccountNumber: String,
    val amount: Double
)
