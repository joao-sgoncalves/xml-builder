package annotations

import converters.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.*
import kotlin.reflect.full.*

class TestXmlString {
    class TestConverter : StringConverter<Any> {
        override fun convert(value: Any): String {
            return "Converted Value"
        }
    }

    @Test
    fun `assigns an XML string annotation to a property and checks if default parameters are assigned`() {
        class Student(
            @XmlString
            val name: String
        )

        val studentClass = Student::class
        val properties = studentClass.declaredMemberProperties

        val nameProperty = properties.single { it.name == "name" }
        val stringAnnotation = nameProperty.findAnnotation<XmlString>()!!

        assertEquals(ToStringConverter::class, stringAnnotation.converterClass)
    }

    @Test
    fun `assigns an XML string annotation to a property and checks if parameters are assigned`() {
        class Student(
            @XmlString(TestConverter::class)
            val name: String
        )

        val studentClass = Student::class
        val properties = studentClass.declaredMemberProperties

        val nameProperty = properties.single { it.name == "name" }
        val stringAnnotation = nameProperty.findAnnotation<XmlString>()!!

        assertEquals(TestConverter::class, stringAnnotation.converterClass)
    }
}
