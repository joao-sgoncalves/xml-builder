package models

import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.io.*
import java.io.*
import java.nio.file.*
import kotlin.io.path.*

class TestXmlDocument {
    @Test
    fun `creates a new document and validates default root is used`() {
        val document = XmlDocument()

        assertNull(document.root)
    }

    @Test
    fun `creates a new document with a composite entity as root`() {
        val root = XmlCompositeEntity(name = "root")
        val document = XmlDocument(root)

        assertEquals(root, document.root)
    }

    @Test
    fun `creates a new document with a text entity as root`() {
        val root = XmlTextEntity(name = "root", text = "This is text...")
        val document = XmlDocument(root)

        assertEquals(root, document.root)
    }

    @Test
    fun `creates a new document and validates default version is used`() {
        val document = XmlDocument()

        assertEquals(1.0, document.version)
    }

    @Test
    fun `creates a new document with the specified version`() {
        val document = XmlDocument(version = 1.1)

        assertEquals(1.1, document.version)
    }

    @Test
    fun `creates a new document with invalid version and validates exception is thrown`() {
        val exception = assertThrows<IllegalArgumentException> { XmlDocument(version = 2.0) }

        assertEquals("Version must be 1.0 or 1.1", exception.message)
    }

    @Test
    fun `creates a new document and validates default encoding is used`() {
        val document = XmlDocument()

        assertEquals("UTF-8", document.encoding)
    }

    @Test
    fun `creates a new document with the specified encoding`() {
        val document = XmlDocument(encoding = "UTF-16")

        assertEquals("UTF-16", document.encoding)
    }

    @Test
    fun `creates a new document with invalid encoding and validates exception is thrown`() {
        val exception = assertThrows<IllegalArgumentException> { XmlDocument(encoding = "UTF-32") }

        assertEquals("Encoding must be UTF-8 or UTF-16", exception.message)
    }

    @Test
    fun `changes document root after creation, to a composite entity`() {
        val document = XmlDocument()

        val newRoot = XmlCompositeEntity(name = "root")
        document.root = newRoot

        assertEquals(newRoot, document.root)
    }

    @Test
    fun `changes document root after creation, to a text entity`() {
        val document = XmlDocument()

        val newRoot = XmlTextEntity(name = "root", text = "This is text...")
        document.root = newRoot

        assertEquals(newRoot, document.root)
    }

    @Test
    fun `changes document root after creation, to no root`() {
        val root = XmlCompositeEntity(name = "root")
        val document = XmlDocument(root)

        document.root = null

        assertNull(document.root)
    }

    @Test
    fun `changes document version after creation`() {
        val document = XmlDocument()

        document.version = 1.1

        assertEquals(1.1, document.version)
    }

    @Test
    fun `changes document version after creation to an invalid version and validates exception is thrown`() {
        val document = XmlDocument()

        val exception = assertThrows<IllegalArgumentException> { document.version = 2.0 }

        assertEquals("Version must be 1.0 or 1.1", exception.message)
    }

    @Test
    fun `changes document encoding after creation`() {
        val document = XmlDocument()

        document.encoding = "UTF-16"

        assertEquals("UTF-16", document.encoding)
    }

    @Test
    fun `changes document encoding after creation to an invalid encoding and validates exception is thrown`() {
        val document = XmlDocument()

        val exception = assertThrows<IllegalArgumentException> { document.encoding = "UTF-32" }

        assertEquals("Encoding must be UTF-8 or UTF-16", exception.message)
    }

    @Test
    fun `returns the xml associated with a document that has several entities`() {
        val root = XmlCompositeEntity(name = "root")

        root.putAttribute(name = "breed", value = "pug")
        root.putAttribute(name = "code", value = "3210")

        val parent = XmlCompositeEntity(name = "parent")

        parent.putAttribute(name = "color", value = "green")

        val child1 = XmlCompositeEntity(name = "child1")

        child1.putAttribute(name = "animal", value = "dog")

        val child2 = XmlTextEntity(name = "child2", text = "This is text...")

        child2.putAttribute(name = "firstName", value = "John")
        child2.putAttribute(name = "lastName", value = "Doe")

        root.addChild(parent)
        parent.addChild(child1)
        parent.addChild(child2)

        val document = XmlDocument(root, version = 1.1, encoding = "UTF-16")

        assertEquals(
            """
                <?xml version="1.1" encoding="UTF-16"?>
                <root breed="pug" code="3210">
                    <parent color="green">
                        <child1 animal="dog" />
                        <child2 firstName="John" lastName="Doe">This is text...</child2>
                    </parent>
                </root>
            """.trimIndent(),
            document.xml,
        )
    }

    @Test
    fun `returns the xml associated with a document that has no root`() {
        val document = XmlDocument()

        assertEquals("""<?xml version="1.0" encoding="UTF-8"?>""", document.xml)
    }

    @Test
    fun `writes the document xml to a file in the specified path`(@TempDir tempDir: Path) {
        val root = XmlCompositeEntity(name = "root")

        root.putAttribute(name = "course", value = "Economics")
        root.putAttribute(name = "students", value = "30")

        val child1 = XmlTextEntity(name = "child1", text = "Computer Science")

        child1.putAttribute(name = "application", value = "Excel")
        child1.putAttribute(name = "rows", value = "2000")

        val child2 = XmlCompositeEntity(name = "child2")

        child2.putAttribute(name = "birthdate", value = "20/06/1987")

        root.addChild(child1)
        root.addChild(child2)

        val document = XmlDocument(root, version = 1.1, encoding = "UTF-16")

        val path = tempDir.resolve("test.xml").pathString
        document.writeToFile(path)

        assertEquals(
            """
                <?xml version="1.1" encoding="UTF-16"?>
                <root course="Economics" students="30">
                    <child1 application="Excel" rows="2000">Computer Science</child1>
                    <child2 birthdate="20/06/1987" />
                </root>
            """.trimIndent(),
            File(path).readText(),
        )
    }

    @Test
    fun `writes the document xml to a file in the specified path, with no root`(@TempDir tempDir: Path) {
        val document = XmlDocument()

        val path = tempDir.resolve("test.xml").pathString
        document.writeToFile(path)

        assertEquals("""<?xml version="1.0" encoding="UTF-8"?>""", File(path).readText())
    }

    @Test
    fun `adds a new attribute to all entities with a specified name`() {
        val root = XmlCompositeEntity(name = "entity")
        val parent = XmlCompositeEntity(name = "parent")
        val child = XmlTextEntity(name = "entity", text = "This is text...")

        child.putAttribute(name = "id", value = "1")

        root.addChild(parent)
        parent.addChild(child)

        val document = XmlDocument(root)

        document.putAttribute(name = "age", value = "32", entityName = "entity")

        assertEquals(mapOf("age" to "32"), root.attributes)
        assertTrue(parent.attributes.isEmpty())
        assertEquals(mapOf("id" to "1", "age" to "32"), child.attributes)
    }

    @Test
    fun `updates the value of an attribute in all entities with the specified name`() {
        val root = XmlCompositeEntity(name = "entity")
        val parent = XmlCompositeEntity(name = "parent")
        val child = XmlTextEntity(name = "entity", text = "This is text...")

        root.putAttribute(name = "firstName", value = "John")
        root.putAttribute(name = "lastName", value = "Doe")

        child.putAttribute(name = "firstName", value = "Amy")
        child.putAttribute(name = "age", value = "64")

        root.addChild(parent)
        parent.addChild(child)

        val document = XmlDocument(root)

        document.putAttribute(name = "firstName", value = "Josh", entityName = "entity")

        assertEquals(mapOf("firstName" to "Josh", "lastName" to "Doe"), root.attributes)
        assertTrue(parent.attributes.isEmpty())
        assertEquals(mapOf("firstName" to "Josh", "age" to "64"), child.attributes)
    }

    @Test
    fun `does nothing when trying to put an attribute in a document without root`() {
        val document = XmlDocument()

        document.putAttribute(name = "animal", value = "dolphin", entityName = "root")

        assertNull(document.root)
    }

    @Test
    fun `throws an exception when trying to put an attribute with an invalid name in all entities`() {
        val root = XmlCompositeEntity(name = "entity")
        val parent = XmlCompositeEntity(name = "parent")
        val child = XmlTextEntity(name = "entity", text = "This is text...")

        child.putAttribute(name = "id", value = "1")

        root.addChild(parent)
        parent.addChild(child)

        val document = XmlDocument(root)

        val exception = assertThrows<IllegalArgumentException> {
            document.putAttribute(name = "inv@lid_@ttr&but#", value = "32", entityName = "entity")
        }

        assertEquals("Name must match the pattern '[a-zA-Z][a-zA-Z0-9]*'", exception.message)
        assertTrue(root.attributes.isEmpty())
        assertTrue(parent.attributes.isEmpty())
        assertEquals(mapOf("id" to "1"), child.attributes)
    }

    @Test
    fun `renames all entities that match the specified name`() {
        val root = XmlCompositeEntity(name = "entity")
        val parent = XmlCompositeEntity(name = "parent")
        val child = XmlTextEntity(name = "entity", text = "Some text!")

        root.addChild(parent)
        parent.addChild(child)

        val document = XmlDocument(root)

        document.renameEntity(oldName = "entity", newName = "renamedEntity")

        assertEquals("renamedEntity", root.name)
        assertEquals("parent", parent.name)
        assertEquals("renamedEntity", child.name)
    }

    @Test
    fun `does nothing when trying to rename entities in a document without root`() {
        val document = XmlDocument()

        document.renameEntity(oldName = "entity", newName = "renamedEntity")

        assertNull(document.root)
    }

    @Test
    fun `throws an exception when trying to rename all entities by specifying an invalid name`() {
        val root = XmlCompositeEntity(name = "entity")
        val parent = XmlCompositeEntity(name = "parent")
        val child = XmlTextEntity(name = "entity", text = "Some text!")

        root.addChild(parent)
        parent.addChild(child)

        val document = XmlDocument(root)

        val exception = assertThrows<IllegalArgumentException> {
            document.renameEntity(oldName = "entity", newName = "inv@lid_n@m#")
        }

        assertEquals("Name must match the pattern '[a-zA-Z][a-zA-Z0-9]*'", exception.message)
        assertEquals("entity", root.name)
        assertEquals("parent", parent.name)
        assertEquals("entity", child.name)
    }

    @Test
    fun `renames all attributes that match the specified name, along with the given entity name`() {
        val root = XmlCompositeEntity(name = "entity")
        val parent = XmlCompositeEntity(name = "parent")
        val child1 = XmlTextEntity(name = "entity", text = "Random text...")
        val child2 = XmlCompositeEntity(name = "entity")

        root.putAttribute(name = "id", value = "#221")
        root.putAttribute(name = "hand", value = "right")

        child1.putAttribute(name = "hand", value = "left")
        child1.putAttribute(name = "color", value = "red")

        child2.putAttribute(name = "dog", value = "oof")

        root.addChild(parent)
        parent.addChild(child1)
        parent.addChild(child2)

        val document = XmlDocument(root)

        document.renameAttribute(oldName = "hand", newName = "steer", entityName = "entity")

        assertEquals(mapOf("id" to "#221", "steer" to "right"), root.attributes)
        assertTrue(parent.attributes.isEmpty())
        assertEquals(mapOf("color" to "red", "steer" to "left"), child1.attributes)
        assertEquals(mapOf("dog" to "oof"), child2.attributes)
    }

    @Test
    fun `does nothing when trying to rename attributes in a document without root`() {
        val document = XmlDocument()

        document.renameAttribute(oldName = "hand", newName = "steer", entityName = "entity")

        assertNull(document.root)
    }

    @Test
    fun `throws an exception when trying to rename all attributes by specifying an invalid name`() {
        val root = XmlCompositeEntity(name = "entity")
        val parent = XmlCompositeEntity(name = "parent")
        val child1 = XmlTextEntity(name = "entity", text = "Random text...")
        val child2 = XmlCompositeEntity(name = "entity")

        root.putAttribute(name = "id", value = "#221")
        root.putAttribute(name = "hand", value = "right")

        child1.putAttribute(name = "hand", value = "left")
        child1.putAttribute(name = "color", value = "red")

        child2.putAttribute(name = "dog", value = "oof")

        root.addChild(parent)
        parent.addChild(child1)
        parent.addChild(child2)

        val document = XmlDocument(root)

        val exception = assertThrows<IllegalArgumentException> {
            document.renameAttribute(oldName = "hand", newName = "inv@lid_@ttr&but#", entityName = "entity")
        }

        assertEquals("Name must match the pattern '[a-zA-Z][a-zA-Z0-9]*'", exception.message)
        assertEquals(mapOf("id" to "#221", "hand" to "right"), root.attributes)
        assertTrue(parent.attributes.isEmpty())
        assertEquals(mapOf("hand" to "left", "color" to "red"), child1.attributes)
        assertEquals(mapOf("dog" to "oof"), child2.attributes)
    }

    @Test
    fun `removes all entities that match the specified name`() {
        val root = XmlCompositeEntity(name = "root")
        val parent = XmlCompositeEntity(name = "entity")
        val child = XmlTextEntity(name = "entity", text = "Some bits of data.")

        root.addChild(parent)
        parent.addChild(child)

        val document = XmlDocument(root)

        document.removeEntity(name = "entity")

        assertTrue(root.children.isEmpty())
        assertTrue(parent.children.isEmpty())
    }

    @Test
    fun `removes all entities that match the specified name, including the root`() {
        val root = XmlCompositeEntity(name = "entity")
        val parent = XmlCompositeEntity(name = "parent")
        val child = XmlTextEntity(name = "entity", text = "Some data...")

        root.addChild(parent)
        parent.addChild(child)

        val document = XmlDocument(root)

        document.removeEntity(name = "entity")

        assertNull(document.root)
        assertEquals(listOf(parent), root.children)
        assertTrue(parent.children.isEmpty())
    }

    @Test
    fun `does nothing when trying to remove entities from a document without root`() {
        val document = XmlDocument()

        document.removeEntity(name = "entity")

        assertNull(document.root)
    }

    @Test
    fun `removes an attribute from all entities that match the specified name`() {
        val root = XmlCompositeEntity(name = "entity")
        val parent = XmlCompositeEntity(name = "parent")
        val child1 = XmlTextEntity(name = "entity", text = "Just some text...")
        val child2 = XmlCompositeEntity(name = "entity")

        root.putAttribute(name = "id", value = "1")
        root.putAttribute(name = "salary", value = "10000")

        parent.putAttribute(name = "id", value = "2")

        child1.putAttribute(name = "salary", value = "2500")
        child1.putAttribute(name = "id", value = "3")

        child2.putAttribute(name = "id", value = "4")

        root.addChild(parent)
        parent.addChild(child1)
        parent.addChild(child2)

        val document = XmlDocument(root)

        document.removeAttribute(name = "salary", entityName = "entity")

        assertEquals(mapOf("id" to "1"), root.attributes)
        assertEquals(mapOf("id" to "2"), parent.attributes)
        assertEquals(mapOf("id" to "3"), child1.attributes)
        assertEquals(mapOf("id" to "4"), child2.attributes)
    }

    @Test
    fun `selects all entities that match the specified xpath expression`() {
        val root = XmlCompositeEntity(name = "root")

        val parent1 = XmlCompositeEntity(name = "parent")
        val parent2 = XmlCompositeEntity(name = "parent2")
        val parent3 = XmlTextEntity(name = "parent3", text = "...")

        val child1 = XmlCompositeEntity(name = "child1")
        val child2 = XmlCompositeEntity(name = "child")
        val child3 = XmlTextEntity(name = "child", text = "Just text")
        val child4 = XmlCompositeEntity(name = "child")
        val child5 = XmlTextEntity(name = "child5", text = "Random text")

        root.addChild(parent1)
        root.addChild(parent2)
        root.addChild(parent3)

        parent1.addChild(child1)
        parent1.addChild(child2)
        parent1.addChild(child3)

        parent2.addChild(child4)
        parent2.addChild(child5)

        val document = XmlDocument(root)
        val entities = document.selectEntities("parent/child")

        assertEquals(listOf(child2, child3), entities)
    }
}
