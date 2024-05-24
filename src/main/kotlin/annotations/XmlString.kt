package annotations

import converters.*
import kotlin.reflect.*

/**
 * Represents an @XmlString annotation.
 *
 * It allows defining a custom conversion function for the text of XML elements.
 * Properties with this annotation will always be represented as XML text (entities or attributes).
 *
 * @property converterClass The class of the string converter that implements the
 * [StringConverter] interface and has the appropriate [StringConverter.convert] function.
 * The default value is the [ToStringConverter] class, which simply calls [toString] function
 * on the passed object.
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY)
annotation class XmlString(val converterClass: KClass<out StringConverter<*>> = ToStringConverter::class)
