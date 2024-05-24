package extensions

import kotlin.reflect.*
import kotlin.reflect.full.*

/**
 * Returns the list of properties declared in the receiver class, sorted by their order of
 * declaration in the primary constructor. The remaining properties are sorted alphabetically.
 * If there is no primary constructor, all properties are sorted alphabetically.
 */
val <T : Any> KClass<T>.sortedDeclaredMemberProperties: List<KProperty1<T, *>>
    get() {
        val constructorProperties = primaryConstructor?.parameters?.mapNotNull { p ->
            declaredMemberProperties.find { it.name == p.name }
        } ?: emptyList()

        return constructorProperties + declaredMemberProperties.filter { it !in constructorProperties }
    }
