package models

/**
 * Represents a composite XML entity that can have [children] entities.
 *
 * @property name The name of the composite entity.
 */
class XmlCompositeEntity(name: String): XmlEntity(name) {
    private val _children = linkedSetOf<XmlEntity>()

    /**
     * The children entities of the composite entity.
     */
    val children: List<XmlEntity>
        get() = _children.toList()

    override val isSelfClosing: Boolean
        get() = children.isEmpty()

    override val innerXml: String
        get() = children.joinToString(separator = "\n", prefix = "\n", postfix = "\n") { it.xml }

    /**
     * Adds a child entity to the composite entity.
     *
     * After adding the child entity to the composite entity's [children],
     * the composite entity becomes the [parent] of the child entity.
     *
     * @param newChild The child entity to add.
     * @return 'true' if the child entity was added successfully,
     * 'false' if it already exists as a child of the composite entity,
     * or if the child entity to be added already has a parent,
     * or if the child entity to be added is a parent of the composite entity.
     */
    fun addChild(newChild: XmlEntity): Boolean {
        if (newChild.parent != null || parent == newChild) {
            return false
        }

        val added = _children.add(newChild)

        if (added) {
            newChild.parent = this
        }

        return added
    }

    /**
     * Removes a child entity from the composite entity.
     *
     * After removing the child entity from the composite entity's [children],
     * the child entity's [parent] is assigned to null.
     *
     * @param oldChild The child entity to remove.
     * @return 'true' if the child entity was removed successfully,
     * 'false' if it does not exist as a child of the composite entity.
     */
    fun removeChild(oldChild: XmlEntity): Boolean {
        val removed = _children.remove(oldChild)

        if (removed) {
            oldChild.parent = null
        }

        return removed
    }

    override fun accept(visitor: (XmlEntity) -> Boolean) {
        if (visitor(this)) {
            children.forEach { it.accept(visitor) }
        }
    }
}
