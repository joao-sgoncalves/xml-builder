package builders

import annotations.*
import converters.*
import extensions.*
import models.*
import models.XmlEntity
import kotlin.reflect.*
import kotlin.reflect.full.*

/**
 * Class responsible for building XML elements from instances of objects that can be annotated.
 */
object XmlObjectBuilder {
    /**
     * Builds an XML document from any given object.
     * Makes use of several annotations like [annotations.XmlEntity] and [annotations.XmlAttribute],
     * to provide more flexibility in the building process.
     *
     * @param rootObj An instance of an object to be built into an XML entity, that will be the root of the document.
     * This object can be annotated, using the annotations defined in the annotations folder.
     * @param version The XML version of the document.
     * @param encoding The encoding of the document.
     * @return The built XML document.
     */
    fun document(rootObj: Any, version: Double = 1.0, encoding: String = "UTF-8"): XmlDocument {
        val rootEntity = entity(rootObj)
        return XmlDocument(rootEntity, version, encoding)
    }

    /**
     * Builds an XML entity from any given object.
     * Makes use of several annotations like [annotations.XmlEntity] and [annotations.XmlAttribute],
     * to provide more flexibility in the conversion process.
     *
     * @param obj An instance of an object to be built into an XML entity.
     * This object can be annotated, using the annotations defined in the annotations folder.
     * @return The built XML entity.
     */
    fun entity(obj: Any): XmlEntity {
        return buildEntity(obj)
    }

    private fun buildEntity(
        obj: Any,
        parent: XmlCompositeEntity? = null,
        element: KAnnotatedElement = obj::class,
        isListItem: Boolean = false,
    ): XmlEntity {
        val fallbackElement = obj::class

        if (element.hasAnnotation<XmlIgnore>(fallbackElement)) {
            return parent!!
        }

        val objClass = obj::class
        val elementName = when (element) {
            is KClass<*> -> element.simpleName!!
            is KProperty<*> -> element.name
            else -> throw IllegalArgumentException("Element '${element::class.simpleName}' is not supported")
        }

        val wrapperAnnotation = element.findAnnotation<XmlEntityWrapper>(fallbackElement)
        val wrapperEntity: XmlCompositeEntity?

        if (wrapperAnnotation != null && !isListItem) {
            val wrapperName = wrapperAnnotation.name.ifEmpty { elementName }
            wrapperEntity = XmlCompositeEntity(wrapperName)

            parent?.addChild(wrapperEntity)
        } else {
            wrapperEntity = parent
        }

        val stringAnnotation = element.findAnnotation<XmlString>(fallbackElement)
        val converterClass = stringAnnotation?.converterClass ?: ToStringConverter::class

        val stringConverter = converterClass.objectInstance ?: converterClass.createInstance()
        val objString = convertObject(obj, stringConverter)

        val attributeAnnotation = element.findAnnotation<XmlAttribute>(fallbackElement)

        if (attributeAnnotation != null) {
            val attributeName = attributeAnnotation.name.ifEmpty { elementName }
            wrapperEntity!!.putAttribute(attributeName, objString)

            return wrapperEntity
        }

        val entityAnnotation = element.findAnnotation<annotations.XmlEntity>(fallbackElement)
        val entityName = entityAnnotation?.name.takeIf { !it.isNullOrEmpty() } ?: elementName
        val childEntity: XmlEntity?

        if (objClass.javaPrimitiveType != null || obj is String || obj is Enum<*> || stringAnnotation != null) {
            childEntity = XmlTextEntity(entityName, objString)
        } else if (obj is Iterable<*>) {
            obj.filterNotNull().forEach { buildEntity(it, wrapperEntity, element, isListItem = true) }
            childEntity = null
        } else {
            childEntity = XmlCompositeEntity(entityName)
            val properties = objClass.sortedDeclaredMemberProperties.filter { it.visibility == KVisibility.PUBLIC }

            properties.forEach {
                val propertyValue = it.getter.call(obj) ?: return@forEach
                buildEntity(propertyValue, childEntity, it)
            }
        }

        childEntity?.let { wrapperEntity?.addChild(it) }

        val adapterAnnotation = element.findAnnotation<XmlAdapter>(fallbackElement)
        val adapterClass = adapterAnnotation?.adapterClass

        val xmlAdapter = adapterClass?.objectInstance ?: adapterClass?.createInstance()
        childEntity?.let { xmlAdapter?.process(it) }

        return childEntity ?: wrapperEntity!!
    }

    private fun <T : Any> convertObject(obj: Any, converter: StringConverter<T>): String {
        @Suppress("UNCHECKED_CAST")
        return converter.convert(obj as T)
    }
}
