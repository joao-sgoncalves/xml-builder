package extensions

import annotations.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import kotlin.reflect.full.*

class TestKAnnotatedElementExtensions {
    @Test
    fun `returns true from hasAnnotation when element has supplied annotation`() {
        data class Student(
            @XmlEntity
            val name: String,
        )

        val studentClass = Student::class
        val nameProperty = studentClass.declaredMemberProperties.single { it.name == "name" }

        val hasAnnotation = nameProperty.hasAnnotation<XmlEntity>(studentClass)

        assertTrue(hasAnnotation)
    }

    @Test
    fun `returns true from hasAnnotation when fallback element has supplied annotation`() {
        @XmlEntity
        data class Student(val name: String)

        val studentClass = Student::class
        val nameProperty = studentClass.declaredMemberProperties.single { it.name == "name" }

        val hasAnnotation = nameProperty.hasAnnotation<XmlEntity>(studentClass)

        assertTrue(hasAnnotation)
    }

    @Test
    fun `returns true from hasAnnotation when element and fallback element have supplied annotation`() {
        @XmlEntity
        data class Student(
            @XmlEntity
            val name: String,
        )

        val studentClass = Student::class
        val nameProperty = studentClass.declaredMemberProperties.single { it.name == "name" }

        val hasAnnotation = nameProperty.hasAnnotation<XmlEntity>(studentClass)

        assertTrue(hasAnnotation)
    }

    @Test
    fun `returns false from hasAnnotation when neither element nor fallback element have supplied annotation`() {
        data class Student(val name: String)

        val studentClass = Student::class
        val nameProperty = studentClass.declaredMemberProperties.single { it.name == "name" }

        val hasAnnotation = nameProperty.hasAnnotation<XmlEntity>(studentClass)

        assertFalse(hasAnnotation)
    }

    @Test
    fun `returns annotation from findAnnotation when element has supplied annotation`() {
        data class Student(
            @XmlEntity
            val name: String,
        )

        val studentClass = Student::class
        val nameProperty = studentClass.declaredMemberProperties.single { it.name == "name" }

        val entityAnnotation = nameProperty.findAnnotation<XmlEntity>(studentClass)

        assertEquals(nameProperty.findAnnotation<XmlEntity>(), entityAnnotation)
    }

    @Test
    fun `returns annotation from findAnnotation when fallback element has supplied annotation`() {
        @XmlEntity
        data class Student(val name: String)

        val studentClass = Student::class
        val nameProperty = studentClass.declaredMemberProperties.single { it.name == "name" }

        val entityAnnotation = nameProperty.findAnnotation<XmlEntity>(studentClass)

        assertEquals(studentClass.findAnnotation<XmlEntity>(), entityAnnotation)
    }

    @Test
    fun `returns annotation from findAnnotation when element and fallback element have supplied annotation`() {
        @XmlEntity
        data class Student(
            @XmlEntity
            val name: String,
        )

        val studentClass = Student::class
        val nameProperty = studentClass.declaredMemberProperties.single { it.name == "name" }

        val entityAnnotation = nameProperty.findAnnotation<XmlEntity>(studentClass)

        assertEquals(nameProperty.findAnnotation<XmlEntity>(), entityAnnotation)
    }

    @Test
    fun `returns null from findAnnotation when neither element nor fallback element have supplied annotation`() {
        data class Student(val name: String)

        val studentClass = Student::class
        val nameProperty = studentClass.declaredMemberProperties.single { it.name == "name" }

        val entityAnnotation = nameProperty.findAnnotation<XmlEntity>(studentClass)

        assertNull(entityAnnotation)
    }
}
