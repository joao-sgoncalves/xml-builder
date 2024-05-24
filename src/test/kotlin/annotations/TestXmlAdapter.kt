package annotations

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.*
import kotlin.reflect.full.*

class TestXmlAdapter {
    class StudentAdapter : adapters.XmlAdapter {
        override fun process(entity: models.XmlEntity) { }
    }

    @Test
    fun `assigns an XML adapter annotation to a class and checks if parameters are assigned`() {
        @XmlAdapter(StudentAdapter::class)
        class Student

        val studentClass = Student::class
        val adapterAnnotation = studentClass.findAnnotation<XmlAdapter>()!!

        assertEquals(StudentAdapter::class, adapterAnnotation.adapterClass)
    }
}
