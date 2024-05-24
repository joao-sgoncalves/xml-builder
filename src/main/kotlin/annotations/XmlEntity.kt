package annotations

/**
 * Represents an @XmlEntity annotation.
 *
 * It allows defining the corresponding element as an entity.
 *
 * @property name Name of the entity. The default value is an empty string.
 * If the name is an empty string, the name of the element is considered instead.
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY)
annotation class XmlEntity(val name: String = "")
