package com.ximedes.conto.db

import com.ximedes.conto.AbstractIntegrationTest
import com.ximedes.conto.UserBuilder
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class UserMapperTest : AbstractIntegrationTest() {

    @Autowired
    lateinit var userMapper: UserMapper

    @Test
    fun `find by username`() {
        assertNull(userMapper.findByUsername("jopo"))
        val jopo = UserBuilder.build {
            username = "jopo"
        }
        userMapper.insertUser(jopo, "jopo")
        assertEquals(jopo, userMapper.findByUsername("jopo"))
    }

    @Test
    fun `it finds the common passwords correctly`() {
        // These are the first and last entries in the import file
        assertTrue(userMapper.isCommonPassword("123456"))
        assertTrue(userMapper.isCommonPassword("brady"))
        assertFalse(userMapper.isCommonPassword("jopoisok"))
    }
}