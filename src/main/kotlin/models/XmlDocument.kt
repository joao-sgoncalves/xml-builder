package models

import java.io.*

/**
 * Represents an XML document.
 *
 * @property root The root entity of the document.
 * @property version The XML version of the document.
 * Possible values are 1.0 or 1.1.
 * @property encoding The encoding of the document.
 * Possible values are UTF-8 or UTF-16.
 */
class XmlDocument(var root: XmlEntity? = null, version: Double = 1.0, encoding: String = "UTF-8") {
    var version: Double = version
        set(value) {
            require(value == 1.0 || value == 1.1) { "Version must be 1.0 or 1.1" }
            field = value
        }

    var encoding: String = encoding
        set(value) {
            require(value == "UTF-8" || value == "UTF-16") { "Encoding must be UTF-8 or UTF-16" }
            field = value
        }

    init {
        this.version = version
        this.encoding = encoding
    }

    /**
     * The XML representation of the document.
     *
     * Represents the XML representation of the document, properly indented, according
     * to each entity position in the hierarchy. The first part of the XML contains
     * the initial declaration (with version and encoding), and second part contains the
     * root XML, along with all its children.
     */
    val xml: String
        get() = buildString {
            val declarationXml = """<?xml version="$version" encoding="$encoding"?>"""
            append(declarationXml)

            val rootXml = root?.xml
            rootXml?.let { append("\n$it") }
        }

    /**
     * Writes the XML document to a file.
     *
     * @param path The path of the file to write to.
     */
    fun writeToFile(path: String) {
        val file = File(path)
        file.writeText(xml)
    }

    /**
     * Adds or updates an attribute to all entities in the document that match the specified name.
     *
     * @param name The name of the attribute.
     * @param value The value of the attribute.
     * @param entityName The name of the entity to change the attribute on.
     */
    fun putAttribute(name: String, value: String, entityName: String) {
        root?.accept {
            if (it.name == entityName) {
                it.putAttribute(name, value)
            }

            true
        }
    }

    /**
     * Renames all entities in the document that match the specified name.
     *
     * @param oldName The old name of the entity.
     * @param newName The new name of the entity.
     */
    fun renameEntity(oldName: String, newName: String) {
        root?.accept {
            if (it.name == oldName) {
                it.name = newName
            }

            true
        }
    }

    /**
     * Renames an attribute in all entities in the document that match the specified name.
     *
     * @param oldName The old name of the attribute.
     * @param newName The new name of the attribute.
     * @param entityName The name of the entity to rename the attribute on.
     */
    fun renameAttribute(oldName: String, newName: String, entityName: String) {
        root?.accept {
            if (it.name == entityName) {
                it.renameAttribute(oldName, newName)
            }

            true
        }
    }

    /**
     * Removes all entities from the document that match the specified name.
     *
     * If the root entity's name matches the specified name, then it is set
     * to null, meaning the document loses the root entity.
     *
     * @param name The name of the entity to remove.
     */
    fun removeEntity(name: String) {
        root?.accept {
            if (it.name == name) {
                if (it == root) root = null
                it.parent?.removeChild(it)
            }

            true
        }
    }

    /**
     * Removes an attribute from all entities in the document that match the specified name.
     *
     * @param name The name of the attribute to remove.
     * @param entityName The name of the entity to remove the attribute from.
     */
    fun removeAttribute(name: String, entityName: String) {
        root?.accept {
            if (it.name == entityName) {
                it.removeAttribute(name)
            }

            true
        }
    }

    /**
     * Selects entities in the document based on an XPath-like expression.
     *
     * For example, the XPath expression "university/course/room" would return
     * all "room" entities that are children of "course" entities,
     * that are in turn children of "university" entities.
     *
     * @param xpath The XPath-like expression specifying the entities to select.
     * @return A list of entities matching the XPath expression.
     * An empty list is returned if no entity matches the XPath expression
     * or if the document has no root.
     */
    fun selectEntities(xpath: String): List<XmlEntity> {
        val entities = mutableListOf<XmlEntity>()

        root?.accept {
            if (matchesExpression(xpath, it)) {
                entities.add(it)
            }

            true
        }

        return entities
    }

    private tailrec fun matchesExpression(xpath: String, entity: XmlEntity): Boolean {
        val entityName = xpath.substringAfterLast('/')
        val remainingXpath = xpath.substringBeforeLast('/', missingDelimiterValue = "")

        if (entity.name != entityName) {
            return false
        }

        if (remainingXpath.isEmpty()) {
            return true
        }

        return matchesExpression(remainingXpath, entity.parent ?: return false)
    }
}
