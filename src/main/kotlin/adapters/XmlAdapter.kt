package adapters

import models.*

/**
 * Represents an adapter used after building XML entities.
 *
 * The goal is to create a class implementing this interface,
 * so that it can be used in the [annotations.XmlAdapter] annotation.
 */
interface XmlAdapter {
    /**
     * Processes an entity after it has been created.
     *
     * @param entity The entity to be processed.
     */
    fun process(entity: XmlEntity)
}
