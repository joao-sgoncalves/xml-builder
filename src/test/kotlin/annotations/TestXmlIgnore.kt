package annotations

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.*
import kotlin.reflect.full.*

class TestXmlIgnore {
    @Test
    fun `assigns an XML ignore annotation to a property and checks if is properly assigned`() {
        class Student(
            @XmlIgnore
            val name: String
        )

        val studentClass = Student::class
        val properties = studentClass.declaredMemberProperties

        val nameProperty = properties.single { it.name == "name" }
        val ignoreAnnotation = nameProperty.findAnnotation<XmlIgnore>()

        assertNotNull(ignoreAnnotation)
    }
}
