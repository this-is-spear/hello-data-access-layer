package hello.tis.hello_jpa

import com.fasterxml.jackson.databind.ObjectMapper
import hello.tis.hello_jpa.controller.dto.CreateAccountRequest
import hello.tis.hello_jpa.controller.dto.TransferRequest
import hello.tis.hello_jpa.domain.Account
import hello.tis.hello_jpa.domain.AccountService
import hello.tis.hello_jpa.service.BankService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional

@ExtendWith(SpringExtension::class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class IntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var bankService: BankService

    @Autowired
    private lateinit var accountService: AccountService

    private lateinit var testAccount1: Account
    private lateinit var testAccount2: Account
    private lateinit var testAccount3: Account

    @BeforeEach
    fun setup() {
        val accountNumber1 = bankService.createAccount("TEST-ACCOUNT-1")
        val accountNumber2 = bankService.createAccount("TEST-ACCOUNT-2")
        val accountNumber3 = bankService.createAccount("TEST-ACCOUNT-3")

        testAccount1 = accountService.getAccount(accountNumber1)
        testAccount2 = accountService.getAccount(accountNumber2)
        testAccount3 = accountService.getAccount(accountNumber3)
    }

    @Test
    fun `계좌 생성 테스트`() {
        // 요청 데이터 준비
        val request = CreateAccountRequest("NEW-TEST-ACCOUNT")

        // API 호출 및 검증
        mockMvc.perform(
            post("/api/bank/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.accountNumber").value("NEW-TEST-ACCOUNT"))
    }

    @Test
    fun `UUID 자동 생성으로 계좌 생성 테스트`() {
        // 빈 요청으로 계좌 생성 (UUID 자동 생성)
        val request = CreateAccountRequest()

        // API 호출 및 검증
        mockMvc.perform(
            post("/api/bank/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.accountNumber").isNotEmpty)
    }

    @Test
    fun `모든 계좌 목록 조회 테스트`() {
        mockMvc.perform(get("/api/bank/accounts"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.accounts").isArray)
            .andExpect(jsonPath("$.accounts.length()").value(3))
            .andExpect(jsonPath("$.accounts[0].accountNumber").exists())
            .andExpect(jsonPath("$.accounts[1].accountNumber").exists())
            .andExpect(jsonPath("$.accounts[2].accountNumber").exists())
    }

    @Test
    fun `특정 계좌 잔액 조회 테스트`() {
        mockMvc.perform(get("/api/bank/accounts/{accountNumber}", testAccount1.accountNumber))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.accountNumber").value(testAccount1.accountNumber))
            .andExpect(jsonPath("$.balance").value(0.0))
    }

    @Test
    fun `존재하지 않는 계좌 조회 테스트`() {
        mockMvc.perform(get("/api/bank/accounts/NON-EXISTENT-ACCOUNT"))
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.error").exists())
    }

    @Test
    fun `계좌 이체 테스트`() {
        계좌이체요청(testAccount1, testAccount2, 100.0)
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.sourceAccountNumber").value(testAccount1.accountNumber))
            .andExpect(jsonPath("$.targetAccountNumber").value(testAccount2.accountNumber))
            .andExpect(jsonPath("$.amount").value(100.0))

        계좌잔액확인(testAccount1, -100.0)
        계좌잔액확인(testAccount2, 100.0)
    }

    @Test
    fun `동일 계좌로 이체 시도시 오류 테스트`() {
        계좌이체요청(testAccount1, testAccount1, 50.0)
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.error").exists())
    }

    @Test
    fun `존재하지 않는 출금 계좌로 이체 시도시 오류 테스트`() {
        val 존재하지_않는_계좌 = Account("NON-EXISTENT-ACCOUNT")
        계좌이체요청(존재하지_않는_계좌, testAccount2, 50.0)
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.error").exists())
    }

    @Test
    fun `존재하지 않는 입금 계좌로 이체 시도시 오류 테스트`() {
        val 존재하지_않는_계좌 = Account("NON-EXISTENT-ACCOUNT")
        계좌이체요청(testAccount2, 존재하지_않는_계좌, 50.0)
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.error").exists())
    }

    @Test
    fun `복합 거래 시나리오 테스트`() {
        계좌이체요청(testAccount1, testAccount2, 100.0)
            .andExpect(status().isCreated)

        계좌이체요청(testAccount2, testAccount1, 30.0)
            .andExpect(status().isCreated)

        계좌잔액확인(testAccount1, -70.0)
        계좌잔액확인(testAccount2, 70.0)
    }

    @Test
    fun `세 번째 계좌 생성 후 거래 흐름 테스트`() {
        계좌이체요청(testAccount1, testAccount3, 50.0)
            .andExpect(status().isCreated)
        계좌이체요청(testAccount2, testAccount3, 25.0)
            .andExpect(status().isCreated)

        계좌잔액확인(testAccount1, -50.0)
        계좌잔액확인(testAccount2, -25.0)
        계좌잔액확인(testAccount3, 75.0)
    }

    private fun 계좌잔액확인(account: Account, amount: Double) =
        mockMvc.perform(get("/api/bank/accounts/{accountNumber}", account.accountNumber))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.balance").value(amount))

    private fun 계좌이체요청(sourceAccount: Account, targetAccount: Account, amount: Double) =
        mockMvc.perform(
            post("/api/bank/transfers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(
                        TransferRequest(
                            sourceAccountNumber = sourceAccount.accountNumber,
                            targetAccountNumber = targetAccount.accountNumber,
                            amount = amount
                        )
                    )
                )
        )
}
