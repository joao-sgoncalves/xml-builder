package models

import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*

class TestXmlCompositeEntity {
    @Test
    fun `creates a new composite entity with name`() {
        val entity = XmlCompositeEntity(name = "entity")

        assertEquals("entity", entity.name)
    }

    @Test
    fun `creates a new composite entity with invalid name and validates exception is thrown`() {
        val exception = assertThrows<IllegalArgumentException> { XmlCompositeEntity(name = "inv@lid_n@m#") }

        assertEquals("Name must match the pattern '[a-zA-Z][a-zA-Z0-9]*'", exception.message)
    }

    @Test
    fun `changes composite entity name after creation`() {
        val entity = XmlCompositeEntity(name = "entity")

        entity.name = "renamedEntity"

        assertEquals("renamedEntity", entity.name)
    }

    @Test
    fun `changes composite entity name after creation to an invalid name and validates exception is thrown`() {
        val entity = XmlCompositeEntity(name = "entity")

        val exception = assertThrows<IllegalArgumentException> { entity.name = "inv@lid_n@m#" }

        assertEquals("Name must match the pattern '[a-zA-Z][a-zA-Z0-9]*'", exception.message)
        assertEquals("entity", entity.name)
    }

    @Test
    fun `creates a new composite entity and validates it has no attributes`() {
        val entity = XmlCompositeEntity(name = "entity")

        assertTrue(entity.attributes.isEmpty())
    }

    @Test
    fun `adds a new attribute to a composite entity and validates its presence`() {
        val entity = XmlCompositeEntity(name = "entity")

        val oldValue = entity.putAttribute(name = "id", value = "1")

        assertNull(oldValue)
        assertEquals(mapOf("id" to "1"), entity.attributes)
    }

    @Test
    fun `changes an attribute value and ensures the change happened`() {
        val entity = XmlCompositeEntity(name = "entity")

        entity.putAttribute(name = "id", value = "1")
        val oldValue = entity.putAttribute(name = "id", value = "2")

        assertEquals("1", oldValue)
        assertEquals(mapOf("id" to "2"), entity.attributes)
    }

    @Test
    fun `throws an exception when trying to put an attribute with an invalid name`() {
        val entity = XmlCompositeEntity(name = "entity")

        val exception = assertThrows<IllegalArgumentException> {
            entity.putAttribute(name = "inv@lid_@ttr&but#", value = "1")
        }

        assertEquals("Name must match the pattern '[a-zA-Z][a-zA-Z0-9]*'", exception.message)
        assertTrue(entity.attributes.isEmpty())
    }

    @Test
    fun `removes an attribute and validates it is no longer there`() {
        val entity = XmlCompositeEntity(name = "entity")

        entity.putAttribute(name = "id", value = "1")
        val oldValue = entity.removeAttribute(name = "id")

        assertEquals("1", oldValue)
        assertTrue(entity.attributes.isEmpty())
    }

    @Test
    fun `returns null when an attempt to remove an attribute that doesn't exist is made`() {
        val entity = XmlCompositeEntity(name = "entity")

        val oldValue = entity.removeAttribute(name = "id")

        assertNull(oldValue)
        assertTrue(entity.attributes.isEmpty())
    }

    @Test
    fun `renames an attribute and validates the change is made`() {
        val entity = XmlCompositeEntity(name = "entity")

        entity.putAttribute(name = "id", value = "1")
        val oldValue = entity.renameAttribute(oldName = "id", newName = "renamedId")

        assertEquals("1", oldValue)
        assertEquals(mapOf("renamedId" to "1"), entity.attributes)
    }

    @Test
    fun `returns null when an attempt to rename an attribute that doesn't exist is made`() {
        val entity = XmlCompositeEntity(name = "entity")

        val oldValue = entity.renameAttribute(oldName = "id", newName = "renamedId")

        assertNull(oldValue)
        assertTrue(entity.attributes.isEmpty())
    }

    @Test
    fun `throws an exception when trying to rename an attribute with an invalid name`() {
        val entity = XmlCompositeEntity(name = "entity")
        entity.putAttribute(name = "id", value = "1")

        val exception = assertThrows<IllegalArgumentException> {
            entity.renameAttribute(oldName = "id", newName = "inv@lid_@ttr&but#")
        }

        assertEquals("Name must match the pattern '[a-zA-Z][a-zA-Z0-9]*'", exception.message)
        assertEquals(mapOf("id" to "1"), entity.attributes)
    }

    @Test
    fun `creates a new composite entity and validates it has no children`() {
        val entity = XmlCompositeEntity(name = "entity")

        assertTrue(entity.children.isEmpty())
    }

    @Test
    fun `adds a child to a composite entity and ensures its presence in the children`() {
        val parent = XmlCompositeEntity(name = "parent")
        val child = XmlCompositeEntity(name = "child")

        val added = parent.addChild(child)

        assertTrue(added)
        assertEquals(listOf(child), parent.children)
    }

    @Test
    fun `does not add a child to a composite entity if it is already a child of it`() {
        val parent = XmlCompositeEntity(name = "parent")
        val child = XmlCompositeEntity(name = "child")

        parent.addChild(child)
        val added = parent.addChild(child)

        assertFalse(added)
        assertEquals(listOf(child), parent.children)
    }

    @Test
    fun `does not add a child to a composite entity if it already has a parent`() {
        val parent1 = XmlCompositeEntity(name = "parent1")
        val parent2 = XmlCompositeEntity(name = "parent2")
        val child = XmlCompositeEntity(name = "child")

        parent1.addChild(child)
        val added = parent2.addChild(child)

        assertFalse(added)
        assertTrue(parent2.children.isEmpty())
    }

    @Test
    fun `does not add a child to a composite entity if the child is already a parent of that entity`() {
        val parent = XmlCompositeEntity(name = "parent")
        val child = XmlCompositeEntity(name = "child")

        parent.addChild(child)
        val added = child.addChild(parent)

        assertFalse(added)
        assertTrue(child.children.isEmpty())
    }

    @Test
    fun `removes a child from a composite entity and ensures its removal from the children`() {
        val parent = XmlCompositeEntity(name = "parent")
        val child = XmlCompositeEntity(name = "child")

        parent.addChild(child)
        val removed = parent.removeChild(child)

        assertTrue(removed)
        assertTrue(parent.children.isEmpty())
    }

    @Test
    fun `does not remove a child from a composite entity if it is not a child of it`() {
        val parent = XmlCompositeEntity(name = "parent")
        val child = XmlCompositeEntity(name = "child")

        val removed = parent.removeChild(child)

        assertFalse(removed)
        assertTrue(parent.children.isEmpty())
    }

    @Test
    fun `creates a new composite entity and validates it has no parent`() {
        val entity = XmlCompositeEntity(name = "entity")

        assertNull(entity.parent)
    }

    @Test
    fun `checks if parent is correctly assigned to composite entity`() {
        val parent = XmlCompositeEntity(name = "parent")
        val child = XmlCompositeEntity(name = "child")

        parent.addChild(child)

        assertEquals(parent, child.parent)
    }

    @Test
    fun `checks if parent is null once child composite entity is removed`() {
        val parent = XmlCompositeEntity(name = "parent")
        val child = XmlCompositeEntity(name = "child")

        parent.addChild(child)
        parent.removeChild(child)

        assertNull(child.parent)
    }

    @Test
    fun `creates a new composite entity and validates it has depth 1`() {
        val entity = XmlCompositeEntity(name = "entity")

        assertEquals(1, entity.depth)
    }

    @Test
    fun `returns 2 as depth for composite entities that have parent`() {
        val parent = XmlCompositeEntity(name = "parent")
        val child = XmlCompositeEntity(name = "child")

        parent.addChild(child)

        assertEquals(2, child.depth)
    }

    @Test
    fun `returns 3 as depth for composite entities that have grandparent`() {
        val grandparent = XmlCompositeEntity(name = "grandparent")
        val parent = XmlCompositeEntity(name = "parent")
        val child = XmlCompositeEntity(name = "child")

        grandparent.addChild(parent)
        parent.addChild(child)

        assertEquals(3, child.depth)
    }

    @Test
    fun `returns the xml associated with a composite entity that has no children`() {
        val entity = XmlCompositeEntity(name = "entity")

        assertEquals("<entity />", entity.xml)
    }

    @Test
    fun `returns the xml associated with a composite entity that has no children, along with its attributes`() {
        val entity = XmlCompositeEntity(name = "entity")

        entity.putAttribute(name = "id", value = "1")
        entity.putAttribute(name = "age", value = "32")

        assertEquals("""<entity id="1" age="32" />""", entity.xml)
    }

    @Test
    fun `returns the xml associated with a composite entity that has a parent`() {
        val parent = XmlCompositeEntity(name = "parent")
        val child = XmlCompositeEntity(name = "child")

        parent.addChild(child)

        assertEquals("    <child />", child.xml)
    }

    @Test
    fun `returns the xml associated with a composite entity that has children, along with its attributes`() {
        val parent = XmlCompositeEntity(name = "parent")

        parent.putAttribute(name = "id", value = "1")
        parent.putAttribute(name = "age", value = "32")

        val child1 = XmlCompositeEntity(name = "child1")
        val child2 = XmlCompositeEntity(name = "child2")

        parent.addChild(child1)
        parent.addChild(child2)

        assertEquals("""
            <parent id="1" age="32">
                <child1 />
                <child2 />
            </parent>
        """.trimIndent(), parent.xml)
    }

    @Test
    fun `validates that composite entities visit themselves and all their children, when visitor function returns true`() {
        val parent = XmlCompositeEntity(name = "parent")
        val child1 = XmlCompositeEntity(name = "child1")
        val child2 = XmlTextEntity(name = "child2", text = "Text...")

        parent.addChild(child1)
        parent.addChild(child2)

        var count = 0

        parent.accept {
            assertTrue(it == parent || it in parent.children)
            count += 1

            true
        }

        assertEquals(3, count)
    }

    @Test
    fun `validates that composite entities visit themselves and not their children, when visitor function returns false`() {
        val parent = XmlCompositeEntity(name = "parent")
        val child1 = XmlCompositeEntity(name = "child1")
        val child2 = XmlTextEntity(name = "child2", text = "Text...")

        parent.addChild(child1)
        parent.addChild(child2)

        var count = 0

        parent.accept {
            assertEquals(parent, it)
            count += 1

            false
        }

        assertEquals(1, count)
    }
}
