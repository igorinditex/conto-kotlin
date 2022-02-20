package com.ximedes.conto.integration

import com.codeborne.selenide.Selenide
import com.ximedes.conto.ContoApplication
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.ContextConfiguration

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ContextConfiguration(classes = [ContoApplication::class])
@TestInstance(PER_CLASS)
abstract class AbstractIntegrationTest {

    val defaultPassword = "p@ssw0rd"

    @LocalServerPort
    var serverPort: Int? = null

    @BeforeAll
    fun setup() {
        System.setProperty("selenide.baseUrl", "http://localhost:${serverPort}")
        System.setProperty("selenide.reportsFolder", "target/test-reports")
    }

    @AfterEach
    fun `reset browser`() {
        Selenide.clearBrowserCookies()
        Selenide.clearBrowserLocalStorage()
    }

    companion object {
        var USER_ID = 1
    }

    fun createUniqueUsername() = "user${(USER_ID++).toString().padStart(10, '0')}"



}