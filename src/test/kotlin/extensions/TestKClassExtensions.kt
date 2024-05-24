package extensions

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.*
import kotlin.reflect.*

class TestKClassExtensions {
    @Test
    fun `returns the properties of a class sorted by the order of their declaration in the primary constructor`() {
        class Student(val firstName: String, lastName: String, var age: Int, val email: String)

        val studentClass = Student::class
        val sortedProperties = studentClass.sortedDeclaredMemberProperties

        assertEquals(listOf("firstName", "age", "email"), sortedProperties.map { it.name })
    }

    @Test
    fun `returns the properties of a class sorted alphabetically`() {
        class Student {
            val firstName = "John"
            var age = 24
            val email = "john.doe@test.com"
        }

        val studentClass = Student::class
        val sortedProperties = studentClass.sortedDeclaredMemberProperties

        assertEquals(listOf("age", "email", "firstName"), sortedProperties.map { it.name })
    }

    @Test
    fun `returns the properties of a class sorted by the order of their declaration in the primary constructor (the remaining properties are returned in alphabetical order)`() {
        class Student(firstName: String, lastName: String, val age: Int) {
            val firstName = firstName
            var email = "john.doe@test.com"
            var address = "Test Street"
        }

        val studentClass = Student::class
        val sortedProperties = studentClass.sortedDeclaredMemberProperties

        assertEquals(listOf("firstName", "age", "address", "email"), sortedProperties.map { it.name })
    }

    @Test
    fun `returns an empty list when a class has no properties`() {
        class Student(name: String, email: String)

        val studentClass = Student::class
        val sortedProperties = studentClass.sortedDeclaredMemberProperties

        assertEquals(emptyList<KProperty<*>>(), sortedProperties)
    }
}
