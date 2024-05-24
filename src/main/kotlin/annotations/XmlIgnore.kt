package annotations

/**
 * Represents an @XmlIgnore annotation.
 *
 * It allows ignoring a property during the building process.
 * An element with this annotation will not be included in the final entity.
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY)
annotation class XmlIgnore
