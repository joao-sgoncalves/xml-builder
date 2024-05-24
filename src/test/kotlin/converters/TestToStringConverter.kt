package converters

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.*

class TestToStringConverter {
    @Test
    fun `returns string representation of string when converting it`() {
        val convertedValue = ToStringConverter.convert("This is a string!")

        assertEquals("This is a string!", convertedValue)
    }

    @Test
    fun `returns string representation of integer when converting it`() {
        val convertedValue = ToStringConverter.convert(24)

        assertEquals("24", convertedValue)
    }

    @Test
    fun `returns string representation of complex class when converting it`() {
        class Address(val street: String, val number: Int) {
            override fun toString(): String {
                return "$street $number"
            }
        }

        val address = Address(street = "Test Street", number = 7)
        val convertedValue = ToStringConverter.convert(address)

        assertEquals("Test Street 7", convertedValue)
    }
}
