package models

/**
 * Represents an XML entity.
 *
 * This class serves as the base class for all XML entities, which are the tags that can exist.
 *
 * @property name The name of the entity.
 * It must start with a letter, followed by a sequence of letters or digits.
 */
abstract class XmlEntity(name: String) {
    private companion object {
        const val NAME_PATTERN = "[a-zA-Z][a-zA-Z0-9]*"
        val NAME_REGEX = Regex(NAME_PATTERN)

        fun requireValidName(value: String) {
            require(value.matches(NAME_REGEX)) { "Name must match the pattern '$NAME_PATTERN'" }
        }
    }

    private val _attributes = linkedMapOf<String, String>()

    var name: String = name
        set(value) {
            requireValidName(value)
            field = value
        }

    init {
        this.name = name
    }

    /**
     * The attributes associated with the entity.
     *
     * Attributes are name-value pairs that are associated with XML entities.
     */
    val attributes: Map<String, String>
        get() = _attributes

    /**
     * The parent of the entity.
     *
     * All entities may have a parent. If an entity has just been created and not yet added as a child
     * of another entity, or if it has been removed from the children of its corresponding entity, this is null.
     */
    var parent: XmlCompositeEntity? = null
        internal set

    /**
     * The depth of the entity in the whole hierarchy.
     *
     * This corresponds to the number of ascendant entities the entity has, counting with itself.
     */
    val depth: Int
        get() = (parent?.depth ?: 0) + 1

    /**
     * Indicates whether the entity is self-closing.
     *
     * A self-closing entity does not have any content inside (p.e., <entity />)
     *
     * @return 'true' if the entity is self-closing, 'false' otherwise.
     */
    protected abstract val isSelfClosing: Boolean

    /**
     * Indicates the XML to be placed between opening and closing tags of the entity.
     */
    protected abstract val innerXml: String

    /**
     * The XML representation of the entity.
     *
     * Represents the XML representation of the entity, properly indented, according
     * to each entity position in the hierarchy.
     */
    val xml: String
        get() = buildString {
            val indent = " ".repeat((depth - 1) * 4)
            append("$indent<$name")

            val attributesString = attributes.entries.joinToString(separator = " ", prefix = " ") {
                "${it.key}=\"${it.value}\""
            }.ifBlank { "" }
            append(attributesString)

            if (isSelfClosing) {
                append(" />")
            } else {
                append(">$innerXml")

                if (innerXml.endsWith("\n")) {
                    append(indent)
                }

                append("</$name>")
            }
        }

    /**
     * Adds or updates an attribute to the entity.
     *
     * @param name The name of the attribute.
     * It must start with a letter, followed by a sequence of letters or digits.
     * @param value The value of the attribute.
     * @return The previous value associated with the attribute name,
     * or null if the name was not present in the attributes.
     */
    fun putAttribute(name: String, value: String): String? {
        requireValidName(name)
        return _attributes.put(name, value)
    }

    /**
     * Removes the attribute with the specified name.
     *
     * @param name The name of the attribute to remove.
     * @return The previous value associated with the attribute name,
     * or null if the name was not present in the attributes.
     */
    fun removeAttribute(name: String): String? = _attributes.remove(name)

    /**
     * Renames an attribute.
     *
     * @param oldName The old name of the attribute.
     * @param newName The new name of the attribute.
     * It must start with a letter, followed by a sequence of letters or digits.
     * @return The value associated with the old attribute name,
     * that will become associated with the new attribute name,
     * or null if the old name was not present in the attributes.
     */
    fun renameAttribute(oldName: String, newName: String): String? {
        requireValidName(newName)

        val oldValue = removeAttribute(oldName) ?: return null
        putAttribute(newName, oldValue)

        return oldValue
    }

    /**
     * Accepts a visitor function to perform operations on the entity (and its children).
     *
     * @param visitor The visitor function to be applied on the entity.
     * This function receives the entity to be applied to, and returns 'true'
     * if the visit should continue to its children, or 'false' otherwise.
     */
    open fun accept(visitor: (XmlEntity) -> Boolean) {
        visitor(this)
    }
}
