package annotations

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.*
import kotlin.reflect.full.*

class TestXmlEntity {
    @Test
    fun `assigns an XML entity annotation to a class and checks if default parameters are assigned`() {
        @XmlEntity
        class Student

        val studentClass = Student::class
        val entityAnnotation = studentClass.findAnnotation<XmlEntity>()!!

        assertEquals("", entityAnnotation.name)
    }

    @Test
    fun `assigns an XML entity annotation to a class and checks if parameters are assigned`() {
        @XmlEntity("student")
        class Student

        val studentClass = Student::class
        val entityAnnotation = studentClass.findAnnotation<XmlEntity>()!!

        assertEquals("student", entityAnnotation.name)
    }

    @Test
    fun `assigns an XML entity annotation to a property and checks if default parameters are assigned`() {
        class Student(
            @XmlEntity
            val name: String
        )

        val studentClass = Student::class
        val properties = studentClass.declaredMemberProperties

        val nameProperty = properties.single { it.name == "name" }
        val entityAnnotation = nameProperty.findAnnotation<XmlEntity>()!!

        assertEquals("", entityAnnotation.name)
    }

    @Test
    fun `assigns an XML entity annotation to a property and checks if parameters are assigned`() {
        class Student(
            @XmlEntity(name = "studentName")
            val name: String
        )

        val studentClass = Student::class
        val properties = studentClass.declaredMemberProperties

        val nameProperty = properties.single { it.name == "name" }
        val entityAnnotation = nameProperty.findAnnotation<XmlEntity>()!!

        assertEquals("studentName", entityAnnotation.name)
    }
}
