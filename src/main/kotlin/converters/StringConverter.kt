package converters

/**
 * Represents a converter that is used to convert objects to strings,
 * so that they can be used as text for [models.XmlEntity].
 *
 * The goal is to create a class implementing this interface,
 * so that it can be used in the [annotations.XmlString] annotation.
 *
 * Alternatively, the [ToStringConverter] class can be used instead.
 */
interface StringConverter<in T : Any> {
    /**
     * Converts an object to a string.
     *
     * @param value The instance of the object to be converted.
     * @return The string resulting from the object conversion.
     */
    fun convert(value: T): String
}
