package extensions

import kotlin.reflect.*
import kotlin.reflect.full.*

/**
 * Determines whether an element has the supplied annotation or not.
 * If not, it also checks if the fallback element has the annotation or not.
 *
 * @param fallbackElement The element to check for the annotation, if it is not present in the main element.
 * @return 'true' if the element or the fallback element have the annotation.
 */
inline fun <reified T : Annotation> KAnnotatedElement.hasAnnotation(fallbackElement: KAnnotatedElement): Boolean {
    return hasAnnotation<T>() || fallbackElement.hasAnnotation<T>()
}

/**
 * Finds the supplied annotation in the element, if it exists.
 * If not, it also tries to find it in the fallback element.
 *
 * @param fallbackElement The element to consider when searching for the annotation, if it is not present in the main element.
 * @return The annotation found in the element or the fallback element, or 'null' if it was not found in any.
 */
inline fun <reified T : Annotation> KAnnotatedElement.findAnnotation(fallbackElement: KAnnotatedElement): T? {
    return findAnnotation() ?: fallbackElement.findAnnotation()
}
