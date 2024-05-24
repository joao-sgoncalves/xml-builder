package builders

import models.*

/**
 * Class responsible for building XML elements from a defined DSL, to facilitate this process.
 */
object XmlDslBuilder {
    /**
     * Builds an XML document and applies the specified [init] method to it.
     *
     * @param version The XML version of the document.
     * Possible values are 1.0 or 1.1.
     * @param encoding The encoding of the document.
     * Possible values are UTF-8 or UTF-16.
     * @param init Method called on the built document after its creation, to modify it according to the user needs.
     * @return The built XML document.
     */
    fun document(version: Double = 1.0, encoding: String = "UTF-8", init: XmlDocument.() -> Unit = {}): XmlDocument {
        val document = XmlDocument(version = version, encoding = encoding)

        document.init()

        return document
    }

    /**
     * Builds an XML composite and applies the specified [init] method to it.
     *
     * @param name The name of the composite entity.
     * It must start with a letter, followed by a sequence of letters or digits.
     * @param init Method called on the built entity after its creation, to modify it according to the user needs.
     * @return The built XML composite entity.
     */
    fun composite(name: String, init: XmlCompositeEntity.() -> Unit = {}): XmlCompositeEntity {
        val compositeEntity = XmlCompositeEntity(name)

        compositeEntity.init()

        return compositeEntity
    }

    /**
     * Builds an XML text entity and applies the specified [init] method to it.
     *
     * @param name The name of the text entity.
     * It must start with a letter, followed by a sequence of letters or digits.
     * @param init Method called on the built entity after its creation, to modify it according to the user needs.
     * This method must return the text of the entity.
     * @return The built XML text entity.
     */
    fun text(name: String, init: XmlTextEntity.() -> String = { "" }): XmlTextEntity {
        val textEntity = XmlTextEntity(name)

        textEntity.text = textEntity.init()

        return textEntity
    }
}

/**
 * Builds an XML composite entity and applies the specified [init] method to it.
 * It also sets the built entity to be the root of the XML document.
 *
 * @param name The name of the composite entity.
 * It must start with a letter, followed by a sequence of letters or digits.
 * @param init Method called on the built entity after its creation, to modify it according to the user needs.
 * @return The built XML composite entity.
 */
fun XmlDocument.composite(name: String, init: XmlCompositeEntity.() -> Unit = {}): XmlCompositeEntity {
    val compositeEntity = XmlDslBuilder.composite(name, init)

    root = compositeEntity

    return compositeEntity
}

/**
 * Builds an XML text entity and applies the specified [init] method to it.
 * It also sets the built entity to be the root of the XML document.
 *
 * @param name The name of the text entity.
 * It must start with a letter, followed by a sequence of letters or digits.
 * @param init Method called on the built entity after its creation, to modify it according to the user needs.
 * This method must return the text of the entity.
 * @return The built XML text entity.
 */
fun XmlDocument.text(name: String, init: XmlTextEntity.() -> String = { "" }): XmlTextEntity {
    val textEntity = XmlDslBuilder.text(name, init)

    root = textEntity

    return textEntity
}

/**
 * Builds an XML composite entity and applies the specified [init] method to it.
 * It also adds the built entity as a child of the current composite entity.
 *
 * @param name The name of the composite entity.
 * It must start with a letter, followed by a sequence of letters or digits.
 * @param init Method called on the built entity after its creation, to modify it according to the user needs.
 * @return The built XML composite entity.
 */
fun XmlCompositeEntity.composite(name: String, init: XmlCompositeEntity.() -> Unit = {}): XmlCompositeEntity {
    val compositeEntity = XmlDslBuilder.composite(name, init)

    addChild(compositeEntity)

    return compositeEntity
}

/**
 * Builds an XML text entity and applies the specified [init] method to it.
 * It also adds the built entity as a child of the current composite entity.
 *
 * @param name The name of the text entity.
 * It must start with a letter, followed by a sequence of letters or digits.
 * @param init Method called on the built entity after its creation, to modify it according to the user needs.
 * This method must return the text of the entity.
 * @return The built XML text entity.
 */
fun XmlCompositeEntity.text(name: String, init: XmlTextEntity.() -> String = { "" }): XmlTextEntity {
    val textEntity = XmlDslBuilder.text(name, init)

    addChild(textEntity)

    return textEntity
}

/**
 * Puts an attribute in the current XML entity.
 *
 * @param name The name of the attribute.
 * It must start with a letter, followed by a sequence of letters or digits.
 * @param value The value of the attribute.
 * @return The previous value associated with the attribute name,
 * or null if the name was not present in the attributes.
 */
fun XmlEntity.attribute(name: String, value: String): String? {
    return putAttribute(name, value)
}
