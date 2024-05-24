package annotations

import adapters.XmlAdapter
import kotlin.reflect.*

/**
 * Represents an @XmlAdapter annotation.
 *
 * It allows the post-processing of entities, to provide more flexibility to the user.
 *
 * @property adapterClass The class of the adapter that implements the
 * [XmlAdapter] interface and has the appropriate [XmlAdapter.process] function.
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY)
annotation class XmlAdapter(val adapterClass: KClass<out XmlAdapter>)
