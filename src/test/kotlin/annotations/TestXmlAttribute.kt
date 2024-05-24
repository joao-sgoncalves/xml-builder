package annotations

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.*
import kotlin.reflect.full.*

class TestXmlAttribute {
    @Test
    fun `assigns an XML attribute annotation to a property and checks if default parameters are assigned`() {
        class Student(
            @XmlAttribute
            val name: String
        )

        val studentClass = Student::class
        val properties = studentClass.declaredMemberProperties

        val nameProperty = properties.single { it.name == "name" }
        val attributeAnnotation = nameProperty.findAnnotation<XmlAttribute>()!!

        assertEquals("", attributeAnnotation.name)
    }

    @Test
    fun `assigns an XML attribute annotation to a property and checks if parameters are assigned`() {
        class Student(
            @XmlAttribute(name = "studentName")
            val name: String
        )

        val studentClass = Student::class
        val properties = studentClass.declaredMemberProperties

        val nameProperty = properties.single { it.name == "name" }
        val attributeAnnotation = nameProperty.findAnnotation<XmlAttribute>()!!

        assertEquals("studentName", attributeAnnotation.name)
    }
}
