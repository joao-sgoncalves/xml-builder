package builders

import models.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*

class TestXmlDslBuilder {
    @Test
    fun `creates an XML document and validates default version is used`() {
        val document = XmlDslBuilder.document()

        assertEquals(1.0, document.version)
    }

    @Test
    fun `creates an XML document and validates given version is used`() {
        val document = XmlDslBuilder.document(version = 1.1)

        assertEquals(1.1, document.version)
    }

    @Test
    fun `creates an XML document with invalid version and validates exception is thrown`() {
        val exception = assertThrows<IllegalArgumentException> { XmlDslBuilder.document(version = 2.0) }

        assertEquals("Version must be 1.0 or 1.1", exception.message)
    }

    @Test
    fun `creates an XML document and validates default encoding is used`() {
        val document = XmlDslBuilder.document()

        assertEquals("UTF-8", document.encoding)
    }

    @Test
    fun `creates an XML document and validates given encoding is used`() {
        val document = XmlDslBuilder.document(encoding = "UTF-16")

        assertEquals("UTF-16", document.encoding)
    }

    @Test
    fun `creates an XML document with invalid encoding and validates exception is thrown`() {
        val exception = assertThrows<IllegalArgumentException> { XmlDslBuilder.document(encoding = "UTF-32") }

        assertEquals("Encoding must be UTF-8 or UTF-16", exception.message)
    }

    @Test
    fun `creates an XML document and validates it has no root`() {
        val document = XmlDslBuilder.document()

        assertNull(document.root)
    }

    @Test
    fun `creates an XML document with a composite entity as its root`() {
        val document = XmlDslBuilder.document {
            composite(name = "Card")
        }

        val root = document.root as XmlCompositeEntity

        assertEquals("Card", root.name)
        assertNull(root.parent)
        assertTrue(root.children.isEmpty())
    }

    @Test
    fun `creates an XML document with a text entity as its root, with the default text`() {
        val document = XmlDslBuilder.document {
            text(name = "Card")
        }

        val root = document.root as XmlTextEntity

        assertEquals("Card", root.name)
        assertNull(root.parent)
        assertEquals("", root.text)
    }

    @Test
    fun `creates an XML document with a text entity as its root, with the given text`() {
        val document = XmlDslBuilder.document {
            text(name = "Card") {
                "Visa"
            }
        }

        val root = document.root as XmlTextEntity

        assertEquals("Card", root.name)
        assertNull(root.parent)
        assertEquals("Visa", root.text)
    }

    @Test
    fun `creates an XML composite entity with given name`() {
        val animalEntity = XmlDslBuilder.composite(name = "Animal")

        assertEquals("Animal", animalEntity.name)
    }

    @Test
    fun `creates an XML composite entity with invalid name and validates exception is thrown`() {
        val exception = assertThrows<IllegalArgumentException> { XmlDslBuilder.composite(name = "An#m@l") }

        assertEquals("Name must match the pattern '[a-zA-Z][a-zA-Z0-9]*'", exception.message)
    }

    @Test
    fun `creates an XML composite entity with no children`() {
        val animalEntity = XmlDslBuilder.composite(name = "Animal")

        assertEquals("Animal", animalEntity.name)
        assertTrue(animalEntity.children.isEmpty())
    }

    @Test
    fun `creates an XML composite entity with children`() {
        val animalEntity = XmlDslBuilder.composite(name = "Animal") {
            composite(name = "Dog") {
                text(name = "Bulldog")
                composite(name = "numberOfLegs")
            }
            text(name = "Cat") {
                "Meow"
            }
        }

        assertEquals(listOf("Dog", "Cat"), animalEntity.children.map { it.name })

        val dogEntity = animalEntity.children[0] as XmlCompositeEntity

        assertEquals(animalEntity, dogEntity.parent)
        assertEquals(listOf("Bulldog", "numberOfLegs"), dogEntity.children.map { it.name })

        val bulldogEntity = dogEntity.children[0] as XmlTextEntity

        assertEquals(dogEntity, bulldogEntity.parent)
        assertEquals("", bulldogEntity.text)

        val legsEntity = dogEntity.children[1] as XmlCompositeEntity

        assertEquals(dogEntity, legsEntity.parent)
        assertTrue(legsEntity.children.isEmpty())

        val catEntity = animalEntity.children[1] as XmlTextEntity

        assertEquals(animalEntity, catEntity.parent)
        assertEquals("Meow", catEntity.text)
    }

    @Test
    fun `creates an XML composite entity and validates it has no attributes`() {
        val animalEntity = XmlDslBuilder.composite(name = "Animal")

        assertTrue(animalEntity.attributes.isEmpty())
    }

    @Test
    fun `creates an XML composite entity with the given attributes`() {
        val animalEntity = XmlDslBuilder.composite(name = "Animal") {
            attribute(name = "Age", value = "7")
            attribute(name = "Breed", value = "Pug")
        }

        assertEquals("Animal", animalEntity.name)
        assertEquals(mapOf("Age" to "7", "Breed" to "Pug"), animalEntity.attributes)
        assertTrue(animalEntity.children.isEmpty())
    }

    @Test
    fun `creates an XML composite entity and validates it has no parent`() {
        val animalEntity = XmlDslBuilder.composite(name = "Animal")

        assertNull(animalEntity.parent)
    }

    @Test
    fun `creates an XML text entity with given name`() {
        val addressEntity = XmlDslBuilder.text(name = "Address")

        assertEquals("Address", addressEntity.name)
    }

    @Test
    fun `creates an XML text entity with invalid name and validates exception is thrown`() {
        val exception = assertThrows<IllegalArgumentException> { XmlDslBuilder.text(name = "Addr&s^") }

        assertEquals("Name must match the pattern '[a-zA-Z][a-zA-Z0-9]*'", exception.message)
    }

    @Test
    fun `creates an XML text entity with the default text`() {
        val addressEntity = XmlDslBuilder.text(name = "Address")

        assertEquals("Address", addressEntity.name)
        assertEquals("", addressEntity.text)
    }

    @Test
    fun `creates an XML text entity with the given text`() {
        val addressEntity = XmlDslBuilder.text(name = "Address") {
            "Test Street 19, Test Country"
        }

        assertEquals("Address", addressEntity.name)
        assertEquals("Test Street 19, Test Country", addressEntity.text)
    }

    @Test
    fun `creates an XML text entity and validates it has no attributes`() {
        val addressEntity = XmlDslBuilder.text(name = "Address")

        assertTrue(addressEntity.attributes.isEmpty())
    }

    @Test
    fun `creates an XML text entity with the given attributes`() {
        val addressEntity = XmlDslBuilder.text(name = "Address") {
            attribute(name = "Number", value = "26")
            attribute(name = "Country", value = "Madagascar")

            "Test Street"
        }

        assertEquals("Address", addressEntity.name)
        assertEquals(mapOf("Number" to "26", "Country" to "Madagascar"), addressEntity.attributes)
        assertEquals("Test Street", addressEntity.text)
    }

    @Test
    fun `creates an XML text entity and validates it has no parent`() {
        val addressEntity = XmlDslBuilder.text(name = "Address")

        assertNull(addressEntity.parent)
    }
}
