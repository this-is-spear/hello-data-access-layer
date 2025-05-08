package hello.tis.hello_jpa.repository

import hello.tis.hello_jpa.domain.Account
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AccountRepository : JpaRepository<Account, String> {
    // 추가 기능이 필요한 경우 여기에 구현
}
