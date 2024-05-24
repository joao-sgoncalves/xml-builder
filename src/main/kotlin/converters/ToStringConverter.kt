package converters

/**
 * Represents the default string converter used by [annotations.XmlString].
 *
 * It converts any object to a string, by calling its [toString] method.
 */
object ToStringConverter : StringConverter<Any> {
    /**
     * It converts a value to a string, by calling its [toString] method.
     *
     * @param value Value to be converted.
     * @return The result of calling the [toString] method on the object.
     */
    override fun convert(value: Any): String {
        return value.toString()
    }
}
