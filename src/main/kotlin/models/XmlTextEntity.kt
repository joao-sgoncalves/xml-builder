package models

/**
 * Represents an XML text entity that can contain [text].
 *
 * @property name The name of the text entity.
 * @property text The text inside the entity.
 */
class XmlTextEntity(name: String, var text: String = "") : XmlEntity(name) {
    override val isSelfClosing: Boolean
        get() = false

    override val innerXml: String
        get() = text
}
