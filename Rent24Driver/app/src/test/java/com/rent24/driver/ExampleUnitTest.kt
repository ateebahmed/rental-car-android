package com.rent24.driver

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun isFindIndexed() {
        listOf(1, 2, 10, 3, 5, 9).find {
            print("value $it")
            it == 4
        }
    }
}
