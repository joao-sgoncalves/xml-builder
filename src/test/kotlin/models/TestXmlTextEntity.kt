package models

import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*

class TestXmlTextEntity {
    @Test
    fun `creates a new text entity with name and text`() {
        val entity = XmlTextEntity(name = "entity", text = "This is text...")

        assertEquals("entity", entity.name)
        assertEquals("This is text...", entity.text)
    }

    @Test
    fun `creates a new document and validates default text is used`() {
        val entity = XmlTextEntity(name = "entity")

        assertEquals("", entity.text)
    }

    @Test
    fun `creates a new text entity with invalid name and validates exception is thrown`() {
        val exception = assertThrows<IllegalArgumentException> {
            XmlTextEntity(name = "inv@lid_n@m#", text = "Some text")
        }

        assertEquals("Name must match the pattern '[a-zA-Z][a-zA-Z0-9]*'", exception.message)
    }

    @Test
    fun `changes text entity name after creation`() {
        val entity = XmlTextEntity(name = "entity", text = "This is text...")

        entity.name = "renamedEntity"

        assertEquals("renamedEntity", entity.name)
    }

    @Test
    fun `changes text entity name after creation to an invalid name and validates exception is thrown`() {
        val entity = XmlTextEntity(name = "entity", text = "Some text")

        val exception = assertThrows<IllegalArgumentException> { entity.name = "inv@lid_n@m#" }

        assertEquals("Name must match the pattern '[a-zA-Z][a-zA-Z0-9]*'", exception.message)
        assertEquals("entity", entity.name)
    }

    @Test
    fun `changes text entity text after creation`() {
        val entity = XmlTextEntity(name = "entity", text = "This is text...")

        entity.text = "This is a new text..."

        assertEquals("This is a new text...", entity.text)
    }

    @Test
    fun `creates a new text entity and validates it has no attributes`() {
        val entity = XmlTextEntity(name = "entity", text = "This is text...")

        assertTrue(entity.attributes.isEmpty())
    }

    @Test
    fun `adds a new attribute to a text entity and validates its presence`() {
        val entity = XmlTextEntity(name = "entity", text = "This is text...")

        val oldValue = entity.putAttribute(name = "id", value = "1")

        assertNull(oldValue)
        assertEquals(mapOf("id" to "1"), entity.attributes)
    }

    @Test
    fun `changes an attribute value and ensures the change happened`() {
        val entity = XmlTextEntity(name = "entity", text = "This is text...")

        entity.putAttribute(name = "id", value = "1")
        val oldValue = entity.putAttribute(name = "id", "2")

        assertEquals("1", oldValue)
        assertEquals(mapOf("id" to "2"), entity.attributes)
    }

    @Test
    fun `throws an exception when trying to put an attribute with an invalid name`() {
        val entity = XmlTextEntity(name = "entity", text = "This is text...")

        val exception = assertThrows<IllegalArgumentException> {
            entity.putAttribute(name = "inv@lid_@ttr&but#", value = "1")
        }

        assertEquals("Name must match the pattern '[a-zA-Z][a-zA-Z0-9]*'", exception.message)
        assertTrue(entity.attributes.isEmpty())
    }

    @Test
    fun `removes an attribute and validates it is no longer there`() {
        val entity = XmlTextEntity(name = "entity", text = "This is text...")

        entity.putAttribute(name = "id", value = "1")
        val oldValue = entity.removeAttribute(name = "id")

        assertEquals("1", oldValue)
        assertTrue(entity.attributes.isEmpty())
    }

    @Test
    fun `returns null when an attempt to remove an attribute that doesn't exist is made`() {
        val entity = XmlTextEntity(name = "entity", text = "This is text...")

        val oldValue = entity.removeAttribute(name = "id")

        assertNull(oldValue)
        assertTrue(entity.attributes.isEmpty())
    }

    @Test
    fun `renames an attribute and validates the change is made`() {
        val entity = XmlTextEntity(name = "entity", text = "This is text...")

        entity.putAttribute(name = "id", value = "1")
        val oldValue = entity.renameAttribute(oldName = "id", newName = "renamedId")

        assertEquals("1", oldValue)
        assertEquals(mapOf("renamedId" to "1"), entity.attributes)
    }

    @Test
    fun `returns null when an attempt to rename an attribute that doesn't exist is made`() {
        val entity = XmlTextEntity(name = "entity", text = "This is text...")

        val oldValue = entity.renameAttribute(oldName = "id", newName = "renamedId")

        assertNull(oldValue)
        assertTrue(entity.attributes.isEmpty())
    }

    @Test
    fun `throws an exception when trying to rename an attribute with an invalid name`() {
        val entity = XmlTextEntity(name = "entity", text = "This is text...")
        entity.putAttribute(name = "id", value = "1")

        val exception = assertThrows<IllegalArgumentException> {
            entity.renameAttribute(oldName = "id", newName = "inv@lid_@ttr&but#")
        }

        assertEquals("Name must match the pattern '[a-zA-Z][a-zA-Z0-9]*'", exception.message)
        assertEquals(mapOf("id" to "1"), entity.attributes)
    }

    @Test
    fun `creates a new text entity and validates it has no parent`() {
        val entity = XmlTextEntity(name = "entity", text = "This is text...")

        assertNull(entity.parent)
    }

    @Test
    fun `checks if parent is correctly assigned to text entity`() {
        val parent = XmlCompositeEntity(name = "parent")
        val child = XmlTextEntity(name = "child", text = "This is text...")

        parent.addChild(child)

        assertEquals(parent, child.parent)
    }

    @Test
    fun `checks if parent is null once child text entity is removed`() {
        val parent = XmlCompositeEntity(name = "parent")
        val child = XmlTextEntity(name = "child", text = "This is text...")

        parent.addChild(child)
        parent.removeChild(child)

        assertNull(child.parent)
    }

    @Test
    fun `creates a new text entity and validates it has depth 1`() {
        val entity = XmlTextEntity(name = "entity", text = "This is text...")

        assertEquals(1, entity.depth)
    }

    @Test
    fun `returns 2 as depth for text entities that have parent`() {
        val parent = XmlCompositeEntity(name = "parent")
        val child = XmlTextEntity(name = "child", text = "This is text...")

        parent.addChild(child)

        assertEquals(2, child.depth)
    }

    @Test
    fun `returns 3 as depth for text entities that have grandparent`() {
        val grandparent = XmlCompositeEntity(name = "grandparent")
        val parent = XmlCompositeEntity(name = "parent")
        val child = XmlTextEntity(name = "child", text = "This is text...")

        grandparent.addChild(parent)
        parent.addChild(child)

        assertEquals(3, child.depth)
    }

    @Test
    fun `returns the xml associated with the text entity`() {
        val entity = XmlTextEntity(name = "entity", text = "This is text...")

        assertEquals("<entity>This is text...</entity>", entity.xml)
    }

    @Test
    fun `returns the xml associated with the text entity, along with its attributes`() {
        val entity = XmlTextEntity(name = "entity", text = "This is text...")

        entity.putAttribute(name = "id", value = "1")
        entity.putAttribute(name = "age", value = "32")

        assertEquals("""<entity id="1" age="32">This is text...</entity>""", entity.xml)
    }

    @Test
    fun `returns the xml associated with a text entity that has a parent`() {
        val parent = XmlCompositeEntity(name = "parent")
        val child = XmlTextEntity(name = "child", text = "This is text...")

        parent.addChild(child)

        assertEquals("    <child>This is text...</child>", child.xml)
    }

    @Test
    fun `validates that text entities visit themselves and only once, when visitor function returns true`() {
        val entity = XmlTextEntity(name = "entity", text = "Text...")
        var count = 0

        entity.accept {
            assertEquals(entity, it)
            count += 1

            true
        }

        assertEquals(1, count)
    }

    @Test
    fun `validates that text entities visit themselves and only once, when visitor function returns false`() {
        val entity = XmlTextEntity(name = "entity", text = "Text...")
        var count = 0

        entity.accept {
            assertEquals(entity, it)
            count += 1

            false
        }

        assertEquals(1, count)
    }
}
