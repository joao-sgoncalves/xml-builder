# XML Builder

This is a library whose goal is to reduce the effort of building XML elements.

### Text Entities

An XML entity like the following:

```xml

<curso>Mestrado em Engenharia Informática</curso>
```

can be represented as:

```kotlin
val cursoEntity = XmlTextEntity(name = "curso", text = "Mestrado em Engenharia Informática")
```

### Composite Entities

It is also possible to add XML entities as children of other entities, like the following example:

```xml

<fuc>
    <nome>Programação Avançada</nome>
</fuc>
```

In order to do this, composite entities must be used:

```kotlin
val fucEntity = XmlCompositeEntity(name = "fuc")
val nomeEntity = XmlTextEntity(name = "nome", text = "Programação Avançada")

fucEntity.addChild(nomeEntity)
```

### Attributes

Entities can also have attributes, like in the following XML:

```xml

<componente nome="Dissertação" peso="60%"/>
```

This can be achieved like this:

```kotlin
val componenteEntity = XmlCompositeEntity(name = "componente")

componenteEntity.putAttribute(name = "nome", value = "Dissertação")
componenteEntity.putAttribute(name = "peso", value = "60%")
```

### Documents

An XML document like the following:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<plano>
    <curso>Mestrado em Engenharia Informática</curso>
</plano>
```

Can be represented like this:

```kotlin
val planoEntity = XmlCompositeEntity(name = "plano")
val cursoEntity = XmlTextEntity(name = "curso", text = "Mestrado em Engenharia Informática")

planoEntity.addChild(cursoEntity)

val document = XmlDocument(root = planoEntity)
```

The version and encoding can also be supplied in the constructor, if the default values are not appropriate.

```kotlin
val document = XmlDocument(root = planoEntity, version = 1.1, encoding = "UTF-16")
```

### Annotations

Manually creating documents with entities can become a very cumbersome and error-prone process.

Consider the following XML:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<plano>
    <curso>Mestrado em Engenharia Informática</curso>
    <fuc codigo="M4310">
        <nome>Programação Avançada</nome>
        <ects>6.0</ects>
        <avaliacao>
            <componente nome="Quizzes" peso="20%"/>
            <comonente nome="Projeto" peso="80%"/>
        </avaliacao>
    </fuc>
</plano>
```

This relatively small piece of XML could be represented as:

```kotlin
val planoEntity = XmlCompositeEntity(name = "plano")

val cursoEntity = XmlTextEntity(name = "curso", text = "Mestrado em Engenharia Informática")
planoEntity.addChild(cursoEntity)

val fucEntity = XmlCompositeEntity(name = "fuc")
fucEntity.putAttribute(name = "codigo", value = "M4310")
planoEntity.addChild(fucEntity)

val nomeEntity = XmlTextEntity(name = "nome", text = "Programação Avançada")
fucEntity.addChild(nomeEntity)

val ectsEntity = XmlTextEntity(name = "ects", text = "6.0")
fucEntity.addChild(ectsEntity)

val avaliacaoEntity = XmlCompositeEntity(name = "avaliacao")
fucEntity.addChild(avaliacaoEntity)

val componenteEntity1 = XmlCompositeEntity(name = "componente")
componenteEntity1.putAttribute(name = "nome", value = "Quizzes")
componenteEntity1.putAttribute(name = "peso", value = "20%")
avaliacaoEntity.addChild(componenteEntity1)

val componenteEntity2 = XmlCompositeEntity(name = "componente")
componenteEntity2.putAttribute(name = "nome", value = "Projeto")
componenteEntity2.putAttribute(name = "peso", value = "80%")
avaliacaoEntity.addChild(componenteEntity2)

val document = XmlDocument(root = planoEntity)
```

Even for this simple case, you can see the amount of code needed to produce the XML.

Not only that, but it is also very difficult to read the code and understand the relationship between each element.

Who is the parent of which entities? Who is the child?

It is not easy to deduce the hierarchy just by quickly looking at the code.

This is where annotations come in. With annotations, you can represent the previous XML document as:

```kotlin
@XmlEntity(name = "plano")
data class Plano(
    val curso: String,
    val fuc: Fuc,
)

data class Fuc(
    @XmlAttribute
    val codigo: String,
    val nome: String,
    val ects: Double,

    @XmlEntityWrapper
    @XmlEntity(name = "componente")
    val avaliacao: List<Componente>,
)

data class Componente(
    val nome: String,

    @XmlString(AddPercentage::class)
    val peso: Int,
)
```

With annotations, you can represent complex XML structures through annotated instances of classes.

After defining the previous set of classes, all that is needed to get the XML document is the following:

```kotlin
val plano = Plano(
    curso = "Mestrado em Engenharia Informática",
    fuc = Fuc(
        codigo = "M4310",
        nome = "Programação Avançada",
        ects = 6.0,
        avaliacao = listOf(
            Componente(nome = "Quizzes", peso = 20),
            Componente(nome = "Projeto", peso = 80),
        ),
    ),
)

val planoEntity = XmlEntity.from(plano)
val document = XmlDocument(root = planoEntity)
```

The above code would result in the same XML as previously shown, with less code, and in a much more readable way,
taking advantage of objects.

### DSL

Although annotations are great for customizing the XML building process in a much simpler way, through the usage of
objects, if you don't want to create additional classes for this purpose, or prefer to follow a different approach,
you can try a custom DSL, created to facilitate the process of building XML elements and to increase the visibility of
their relationships.

The example XML shown previously could be represented in the following way:

```kotlin
val document = XmlDslBuilder.document {
    composite("plano") {
        text("curso") {
            "Mestrado em Engenharia Informática"
        }
        composite("fuc") {
            attribute("codigo", "M4310")
            text("nome") {
                "Programação Avançada"
            }
            text("ects") {
                "6.0"
            }
            composite("avaliacao") {
                composite("componente") {
                    attribute("nome", "Quizzes")
                    attribute("peso", "20%")
                }
                composite("componente") {
                    attribute("nome", "Projeto")
                    attribute("peso", "80%")
                }
            }
        }
    }
}
```

With this approach, the XML hierarchy tree speaks through the code, in the way it is built, increasing the readability.
