package annotations

/**
 * Represents an @XmlAttribute annotation.
 *
 * It allows defining the corresponding element as an attribute
 * of the entity, instead of an entity itself.
 *
 * @property name Name of the attribute. The default value is an empty string.
 * If the name is an empty string, the name of the element is considered instead.
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY)
annotation class XmlAttribute(val name: String = "")
