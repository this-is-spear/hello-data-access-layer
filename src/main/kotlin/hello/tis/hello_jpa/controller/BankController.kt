package hello.tis.hello_jpa.controller

import hello.tis.hello_jpa.domain.Money
import hello.tis.hello_jpa.controller.dto.AccountListResponse
import hello.tis.hello_jpa.controller.dto.AccountResponse
import hello.tis.hello_jpa.controller.dto.CreateAccountRequest
import hello.tis.hello_jpa.controller.dto.CreateAccountResponse
import hello.tis.hello_jpa.controller.dto.TransferRequest
import hello.tis.hello_jpa.controller.dto.TransferResponse
import hello.tis.hello_jpa.service.BankService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/bank")
class BankController(
    private val bankService: BankService
) {
    @PostMapping("/accounts")
    fun createAccount(@RequestBody request: CreateAccountRequest): ResponseEntity<CreateAccountResponse> {
        val accountNumber = bankService.createAccount(request.accountNumber ?: "")
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(CreateAccountResponse(accountNumber))
    }

    @GetMapping("/accounts")
    fun getAccounts(): ResponseEntity<AccountListResponse> {
        val accounts = bankService.getAccountList()
        val accountResponses = accounts.map { account ->
            AccountResponse(
                accountNumber = account.accountNumber,
                balance = account.balance.amount
            )
        }
        return ResponseEntity.ok(AccountListResponse(accountResponses))
    }

    @GetMapping("/accounts/{accountNumber}")
    fun getAccountBalance(@PathVariable accountNumber: String): ResponseEntity<AccountResponse> {
        val balance = bankService.getBalance(accountNumber)
        return ResponseEntity.ok(
            AccountResponse(
                accountNumber = accountNumber,
                balance = balance.amount
            )
        )
    }

    @PostMapping("/transfers")
    fun transferMoney(@RequestBody request: TransferRequest): ResponseEntity<TransferResponse> {
        // Money 객체 생성
        val amount = Money(request.amount)
        
        // 이체 수행
        synchronized(this) {
            bankService.transfer(
                sourceAccountNumber = request.sourceAccountNumber,
                targetAccountNumber = request.targetAccountNumber,
                amount = amount
            )
        }
        
        // 응답 생성
        return ResponseEntity.status(HttpStatus.CREATED).body(
            TransferResponse(
                sourceAccountNumber = request.sourceAccountNumber,
                targetAccountNumber = request.targetAccountNumber,
                amount = request.amount
            )
        )
    }
    
    // 예외 처리
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(e: IllegalArgumentException): ResponseEntity<Map<String, String>> {
        val errorResponse = mapOf("error" to e.message.orEmpty())
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }
    
    @ExceptionHandler(Exception::class)
    fun handleGenericException(e: Exception): ResponseEntity<Map<String, String>> {
        val errorResponse = mapOf("error" to "An unexpected error occurred: ${e.message}")
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse)
    }
}
