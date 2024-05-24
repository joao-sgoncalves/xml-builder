package annotations

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.*
import kotlin.reflect.full.*

class TestXmlEntityWrapper {
    @Test
    fun `assigns an XML entity wrapper annotation to a property and checks if default parameters are assigned`() {
        class Student(
            @XmlEntityWrapper
            val grades: List<Int>
        )

        val studentClass = Student::class
        val properties = studentClass.declaredMemberProperties

        val gradesProperty = properties.single { it.name == "grades" }
        val wrapperAnnotation = gradesProperty.findAnnotation<XmlEntityWrapper>()!!

        assertEquals("", wrapperAnnotation.name)
    }

    @Test
    fun `assigns an XML entity wrapper annotation to a property and checks if parameters are assigned`() {
        class Student(
            @XmlEntityWrapper(name = "studentGrades")
            val grades: List<Int>
        )

        val studentClass = Student::class
        val properties = studentClass.declaredMemberProperties

        val gradesProperty = properties.single { it.name == "grades" }
        val wrapperAnnotation = gradesProperty.findAnnotation<XmlEntityWrapper>()!!

        assertEquals("studentGrades", wrapperAnnotation.name)
    }
}
