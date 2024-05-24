package annotations

/**
 * Represents an @XmlEntityWrapper annotation.
 *
 * It allows defining an additional entity around another entity.
 * It is particularly useful for collection properties.
 *
 * @property name Name of the wrapper entity. The default value is an empty string.
 * If the name is an empty string, the name of the element is considered instead.
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY)
annotation class XmlEntityWrapper(val name: String = "")
