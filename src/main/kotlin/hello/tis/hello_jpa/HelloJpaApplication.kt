package hello.tis.hello_jpa

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["hello.tis.hello_jpa"])
class HelloJpaApplication

fun main(args: Array<String>) {
	runApplication<HelloJpaApplication>(*args)
}
