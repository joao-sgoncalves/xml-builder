package builders

import adapters.XmlAdapter
import annotations.*
import converters.*
import models.*
import models.XmlEntity
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import java.time.*
import java.util.concurrent.*

class TestXmlObjectBuilder {
    object AddPercentage : StringConverter<Any> {
        override fun convert(value: Any): String {
            return "$value%"
        }
    }

    data class Address(
        var street: String,
        val number: Int,
    )

    object AddressConverter : StringConverter<Address> {
        override fun convert(value: Address): String {
            return "${value.street}, ${value.number}"
        }
    }

    enum class AnimalType {
        CAT,
        DOG,
    }

    object NameAdapter : XmlAdapter {
        override fun process(entity: XmlEntity) {
            entity.name += "CHANGED"
        }
    }

    @Test
    fun `creates an XML document with the given object as a root text entity`() {
        val document = XmlObjectBuilder.document(rootObj = 13.4)

        val root = document.root as XmlTextEntity

        assertEquals("Double", root.name)
        assertEquals("13.4", root.text)
    }

    @Test
    fun `creates an XML document with the given object as a root composite entity`() {
        class Student

        val student = Student()
        val document = XmlObjectBuilder.document(student)

        val root = document.root as XmlCompositeEntity

        assertEquals("Student", root.name)
        assertTrue(root.children.isEmpty())
    }

    @Test
    fun `creates an XML document with the default version`() {
        val document = XmlObjectBuilder.document(rootObj = 1.3f)

        assertEquals(1.0, document.version)
    }

    @Test
    fun `creates an XML document with the given version`() {
        val document = XmlObjectBuilder.document(rootObj = true, version = 1.1)

        assertEquals(1.1, document.version)
    }

    @Test
    fun `creates an XML document with invalid version and validates exception is thrown`() {
        val exception = assertThrows<IllegalArgumentException> { XmlObjectBuilder.document(rootObj = 2, version = 2.0) }

        assertEquals("Version must be 1.0 or 1.1", exception.message)
    }

    @Test
    fun `creates an XML document with the default encoding`() {
        val document = XmlObjectBuilder.document(rootObj = 'T')

        assertEquals("UTF-8", document.encoding)
    }

    @Test
    fun `creates an XML document with the given encoding`() {
        val document = XmlObjectBuilder.document(rootObj = DayOfWeek.MONDAY, encoding = "UTF-16")

        assertEquals("UTF-16", document.encoding)
    }

    @Test
    fun `creates an XML document with invalid encoding and validates exception is thrown`() {
        val exception = assertThrows<IllegalArgumentException> {
            XmlObjectBuilder.document(rootObj = TimeUnit.SECONDS, encoding = "ASCII")
        }

        assertEquals("Encoding must be UTF-8 or UTF-16", exception.message)
    }

    @Test
    fun `creates an XML text entity from a primitive value, with the name of its class`() {
        val textEntity = XmlObjectBuilder.entity(24) as XmlTextEntity

        assertEquals("Int", textEntity.name)
        assertEquals("24", textEntity.text)
    }

    @Test
    fun `creates an XML text entity from a string, with the name of its class`() {
        val textEntity = XmlObjectBuilder.entity("The dog barks") as XmlTextEntity

        assertEquals("String", textEntity.name)
        assertEquals("The dog barks", textEntity.text)
    }

    @Test
    fun `creates an XML text entity from an enum, with the name of its class`() {
        val textEntity = XmlObjectBuilder.entity(TimeUnit.DAYS) as XmlTextEntity

        assertEquals("TimeUnit", textEntity.name)
        assertEquals("DAYS", textEntity.text)
    }

    @Test
    fun `creates an XML composite entity from a class, with the name of the class itself`() {
        class Student

        val student = Student()
        val studentEntity = XmlObjectBuilder.entity(student) as XmlCompositeEntity

        assertEquals("Student", studentEntity.name)
    }

    @Test
    fun `creates an XML composite entity from an annotated class with default name`() {
        @annotations.XmlEntity
        class Student

        val student = Student()
        val studentEntity = XmlObjectBuilder.entity(student) as XmlCompositeEntity

        assertEquals("Student", studentEntity.name)
    }

    @Test
    fun `creates an XML composite entity from an annotated class with the given name`() {
        @annotations.XmlEntity(name = "student")
        class Student

        val student = Student()
        val studentEntity = XmlObjectBuilder.entity(student) as XmlCompositeEntity

        assertEquals("student", studentEntity.name)
    }

    @Test
    fun `throws an error when creating an XML composite entity from an annotated class with an invalid name`() {
        @annotations.XmlEntity(name = "_invalidN@m#")
        class Student

        val student = Student()
        val exception = assertThrows<IllegalArgumentException> { XmlObjectBuilder.entity(student) }

        assertEquals("Name must match the pattern '[a-zA-Z][a-zA-Z0-9]*'", exception.message)
    }

    @Test
    fun `creates an XML composite entity from a class with its constructor properties as entities, by order of declaration`() {
        class Student(val firstName: String, lastName: String, var age: Int, val email: String)

        val student = Student(firstName = "John", lastName = "Doe", age = 24, email = "john.doe@test.com")
        val studentEntity = XmlObjectBuilder.entity(student) as XmlCompositeEntity

        assertEquals(listOf("firstName", "age", "email"), studentEntity.children.map { it.name })
        assertEquals(
            listOf("John", "24", "john.doe@test.com"),
            studentEntity.children.map { (it as XmlTextEntity).text },
        )
    }

    @Test
    fun `creates an XML composite entity from a class with its properties as entities, by alphabetical order`() {
        class Student {
            val firstName = "John"
            var age = 24
            val email = "john.doe@test.com"
        }

        val student = Student()
        val studentEntity = XmlObjectBuilder.entity(student) as XmlCompositeEntity

        assertEquals(listOf("age", "email", "firstName"), studentEntity.children.map { it.name })
        assertEquals(
            listOf("24", "john.doe@test.com", "John"),
            studentEntity.children.map { (it as XmlTextEntity).text },
        )
    }

    @Test
    fun `creates an XML composite entity from a class with its properties as entities, by declaration (constructor) and alphabetical (remaining) order`() {
        class Student(val firstName: String, lastName: String, var email: String) {
            var age = 24
            val address = "Test Street"
        }

        val student = Student(firstName = "John", lastName = "Doe", email = "john.doe@test.com")
        val studentEntity = XmlObjectBuilder.entity(student) as XmlCompositeEntity

        assertEquals(listOf("firstName", "email", "address", "age"), studentEntity.children.map { it.name })
        assertEquals(
            listOf("John", "john.doe@test.com", "Test Street", "24"),
            studentEntity.children.map { (it as XmlTextEntity).text },
        )
    }

    @Test
    fun `creates an XML composite entity from a class without any property that is not public`() {
        class Student(internal val firstName: String, private var lastName: String, protected val grades: List<Int>) {
            protected var id = "#1"
            private val age = 24
            internal var address = "Test Street"
        }

        val student = Student(firstName = "John", lastName = "Doe", grades = listOf(15, 16, 11))
        val studentEntity = XmlObjectBuilder.entity(student) as XmlCompositeEntity

        assertTrue(studentEntity.children.isEmpty())
    }

    @Test
    fun `creates an XML composite entity from a class with its properties as composite entities`() {
        data class Address(
            val street: String,
            var number: Int,
        )

        data class Department(
            var id: Int,
            val name: String,
        )

        data class Employee(
            var address: Address?,
            val department: Department,
        )

        val employee = Employee(
            address = Address(street = "Some street", number = 17),
            department = Department(id = 1, name = "Sales"),
        )

        val employeeEntity = XmlObjectBuilder.entity(employee) as XmlCompositeEntity

        assertEquals(listOf("address", "department"), employeeEntity.children.map { it.name })

        val addressEntity = employeeEntity.children[0] as XmlCompositeEntity

        assertEquals(listOf("street", "number"), addressEntity.children.map { it.name })
        assertEquals(listOf("Some street", "17"), addressEntity.children.map { (it as XmlTextEntity).text })

        val departmentEntity = employeeEntity.children[1] as XmlCompositeEntity

        assertEquals(listOf("id", "name"), departmentEntity.children.map { it.name })
        assertEquals(listOf("1", "Sales"), departmentEntity.children.map { (it as XmlTextEntity).text })
    }

    @Test
    fun `creates an XML composite entity from a class without any null property`() {
        class Mapping(val field1: String?, val field2: String, var field3: String?)

        val mapping = Mapping(field1 = null, field2 = "AccountNumber", field3 = null)
        val mappingEntity = XmlObjectBuilder.entity(mapping) as XmlCompositeEntity

        assertEquals(listOf("field2"), mappingEntity.children.map { it.name })
        assertEquals(listOf("AccountNumber"), mappingEntity.children.map { (it as XmlTextEntity).text })
    }

    @Test
    fun `creates an XML composite entity from an annotated class with its properties as entities, with default names`() {
        data class House(
            @annotations.XmlEntity
            val numberOfBedrooms: Int,

            @annotations.XmlEntity
            var squareMeters: Float,
        )

        val house = House(numberOfBedrooms = 2, squareMeters = 119.23f)
        val houseEntity = XmlObjectBuilder.entity(house) as XmlCompositeEntity

        assertEquals(listOf("numberOfBedrooms", "squareMeters"), houseEntity.children.map { it.name })
        assertEquals(listOf("2", "119.23"), houseEntity.children.map { (it as XmlTextEntity).text })
    }

    @Test
    fun `creates an XML composite entity from an annotated class with its properties as entities, with given names`() {
        data class Fruit(
            @annotations.XmlEntity(name = "fruitColor")
            var color: String?,

            @annotations.XmlEntity(name = "fruitShape")
            val shape: String,
        )

        val fruit = Fruit(color = "orange", shape = "round")
        val fruitEntity = XmlObjectBuilder.entity(fruit) as XmlCompositeEntity

        assertEquals(listOf("fruitColor", "fruitShape"), fruitEntity.children.map { it.name })
        assertEquals(listOf("orange", "round"), fruitEntity.children.map { (it as XmlTextEntity).text })
    }

    @Test
    fun `creates an XML composite entity from an annotated class with its properties as entities, considering fallback elements`() {
        @annotations.XmlEntity
        data class FruitColor(val name: String)

        @annotations.XmlEntity(name = "ShapeOfFruit")
        data class FruitShape(val name: String)

        data class Fruit(var color: FruitColor, val shape: FruitShape)

        val fruit = Fruit(color = FruitColor(name = "orange"), shape = FruitShape(name = "round"))
        val fruitEntity = XmlObjectBuilder.entity(fruit) as XmlCompositeEntity

        assertEquals(listOf("color", "ShapeOfFruit"), fruitEntity.children.map { it.name })

        val colorEntity = fruitEntity.children[0] as XmlCompositeEntity

        assertEquals(listOf("name"), colorEntity.children.map { it.name })
        assertEquals(listOf("orange"), colorEntity.children.map { (it as XmlTextEntity).text })

        val shapeEntity = fruitEntity.children[1] as XmlCompositeEntity

        assertEquals(listOf("name"), shapeEntity.children.map { it.name })
        assertEquals(listOf("round"), shapeEntity.children.map { (it as XmlTextEntity).text })
    }

    @Test
    fun `throws an error when creating an XML composite entity from an annotated class with an invalid name for its property`() {
        data class Fruit(
            @annotations.XmlEntity(name = "fru#tC@l@r")
            val color: String?,
        )

        val fruit = Fruit(color = "orange")
        val exception = assertThrows<IllegalArgumentException> { XmlObjectBuilder.entity(fruit) }

        assertEquals("Name must match the pattern '[a-zA-Z][a-zA-Z0-9]*'", exception.message)
    }

    @Test
    fun `creates an XML composite entity from an annotated class with the given attributes and default names`() {
        data class Student(
            @XmlAttribute
            val name: String,

            @XmlAttribute
            var averageGrade: Double?,

            @XmlAttribute
            val worker: Boolean,
        )

        val student = Student(name = "John Doe", averageGrade = 18.78, worker = true)
        val studentEntity = XmlObjectBuilder.entity(student) as XmlCompositeEntity

        assertEquals(
            mapOf("name" to "John Doe", "averageGrade" to "18.78", "worker" to "true"),
            studentEntity.attributes,
        )
    }

    @Test
    fun `creates an XML composite entity from an annotated class with the given attributes and corresponding names`() {
        data class Card(
            @XmlAttribute(name = "cardStatus")
            val status: Char,

            @XmlAttribute(name = "cardExpiration")
            val expirationDay: DayOfWeek,
        )

        val card = Card(status = 'S', expirationDay = DayOfWeek.SUNDAY)
        val cardEntity = XmlObjectBuilder.entity(card) as XmlCompositeEntity

        assertEquals(mapOf("cardStatus" to "S", "cardExpiration" to "SUNDAY"), cardEntity.attributes)
    }

    @Test
    fun `creates an XML composite entity from an annotated class with the given attributes, considering fallback elements`() {
        @XmlAttribute
        data class CardStatus(val status: Char) {
            override fun toString(): String {
                return status.toString()
            }
        }

        @XmlAttribute(name = "DayOfExpiration")
        data class CardExpirationDay(val day: DayOfWeek) {
            override fun toString(): String {
                return day.toString()
            }
        }

        data class Card(val status: CardStatus, val expirationDay: CardExpirationDay)

        val card = Card(status = CardStatus(status = 'S'), expirationDay = CardExpirationDay(day = DayOfWeek.SUNDAY))
        val cardEntity = XmlObjectBuilder.entity(card) as XmlCompositeEntity

        assertEquals(mapOf("status" to "S", "DayOfExpiration" to "SUNDAY"), cardEntity.attributes)
    }

    @Test
    fun `throws an error when creating an XML composite entity from an annotated class with an invalid name for its attribute`() {
        data class Card(
            @XmlAttribute(name = "c^r!St&tu*")
            val status: Char,
        )

        val card = Card(status = 'T')
        val exception = assertThrows<IllegalArgumentException> { XmlObjectBuilder.entity(card) }

        assertEquals("Name must match the pattern '[a-zA-Z][a-zA-Z0-9]*'", exception.message)
    }

    @Test
    fun `creates an XML composite entity from an annotated class without any property annotated with XML ignore`() {
        data class Movie(
            @XmlIgnore
            val name: String,

            @XmlIgnore
            var rating: Double,

            @XmlIgnore
            val highBudget: Boolean,
        )

        val movie = Movie(name = "Iron Man", rating = 4.6, highBudget = true)
        val movieEntity = XmlObjectBuilder.entity(movie) as XmlCompositeEntity

        assertTrue(movieEntity.children.isEmpty())
    }

    @Test
    fun `creates an XML composite entity from an annotated class without any property annotated with XML ignore, considering fallback elements`() {
        @XmlIgnore
        data class MovieName(val name: String)

        @XmlIgnore
        data class MovieRating(var rating: Double)

        @XmlIgnore
        data class MovieHighBudget(val budget: Boolean)

        data class Movie(val name: MovieName, var rating: MovieRating, val highBudget: MovieHighBudget)

        val movie = Movie(
            name = MovieName(name = "Iron Man"),
            rating = MovieRating(rating = 4.6),
            highBudget = MovieHighBudget(budget = true),
        )

        val movieEntity = XmlObjectBuilder.entity(movie) as XmlCompositeEntity

        assertTrue(movieEntity.children.isEmpty())
    }

    @Test
    fun `creates an XML composite entity from an annotated class, with the default string conversion function`() {
        data class Owner(val firstName: String, var lastName: String) {
            override fun toString(): String {
                return "$firstName $lastName"
            }
        }

        data class Animal(
            @XmlString
            val type: AnimalType,

            @XmlAttribute
            @XmlString
            var numberOfLegs: Short,

            @XmlString
            val owner: Owner,
        )

        val animal = Animal(
            type = AnimalType.DOG,
            numberOfLegs = 4,
            owner = Owner(firstName = "John", lastName = "Doe"),
        )

        val animalEntity = XmlObjectBuilder.entity(animal) as XmlCompositeEntity

        assertEquals(mapOf("numberOfLegs" to "4"), animalEntity.attributes)
        assertEquals(listOf("type", "owner"), animalEntity.children.map { it.name })
        assertEquals(listOf("DOG", "John Doe"), animalEntity.children.map { (it as XmlTextEntity).text })
    }

    @Test
    fun `creates an XML composite entity from an annotated class, with a custom string conversion function`() {
        data class Student(
            @XmlString(AddPercentage::class)
            var participation: Double,

            @XmlAttribute(name = "studentGrade")
            @XmlString(AddPercentage::class)
            val grade: Float,

            @XmlString(AddressConverter::class)
            var address: Address,
        )

        val student = Student(
            participation = 16.28,
            grade = 19.01f,
            address = Address(street = "Test Street", number = 19),
        )

        val studentEntity = XmlObjectBuilder.entity(student) as XmlCompositeEntity

        assertEquals(mapOf("studentGrade" to "19.01%"), studentEntity.attributes)
        assertEquals(listOf("participation", "address"), studentEntity.children.map { it.name })
        assertEquals(listOf("16.28%", "Test Street, 19"), studentEntity.children.map { (it as XmlTextEntity).text })
    }

    @Test
    fun `creates an XML composite entity from an annotated class, with a string conversion function, considering fallback elements`() {
        @XmlString
        data class StudentParticipation(var participation: Double) {
            override fun toString(): String {
                return participation.toString()
            }
        }

        @XmlAttribute(name = "studentGrade")
        @XmlString(AddPercentage::class)
        data class StudentGrade(val grade: Float) {
            override fun toString(): String {
                return grade.toString()
            }
        }

        data class Student(var participation: StudentParticipation, val grade: StudentGrade)

        val student = Student(
            participation = StudentParticipation(participation = 16.28),
            grade = StudentGrade(grade = 19.01f),
        )

        val studentEntity = XmlObjectBuilder.entity(student) as XmlCompositeEntity

        assertEquals(mapOf("studentGrade" to "19.01%"), studentEntity.attributes)
        assertEquals(listOf("participation"), studentEntity.children.map { it.name })
        assertEquals(listOf("16.28"), studentEntity.children.map { (it as XmlTextEntity).text })
    }

    @Test
    fun `throws an error when creating an XML composite entity from an annotated class, if value type doesn't match string conversion function receiver type`() {
        data class Student(
            @XmlString(AddressConverter::class)
            val name: String,
        )

        val student = Student(name = "John Doe")
        val exception = assertThrows<ClassCastException> { XmlObjectBuilder.entity(student) }

        println(exception.message)
        assertTrue(exception.message!!.startsWith("class java.lang.String cannot be cast to class builders.TestXmlObjectBuilder\$Address"))
    }

    @Test
    fun `creates an XML composite entity from a class, with its iterable properties as separate entities`() {
        data class Teacher(var disciplines: List<String>, val phoneNumbers: MutableList<Long>)

        val teacher = Teacher(disciplines = listOf("Arts", "History"), phoneNumbers = mutableListOf(91, 96, 93))
        val teacherEntity = XmlObjectBuilder.entity(teacher) as XmlCompositeEntity

        assertEquals(
            listOf("disciplines", "disciplines", "phoneNumbers", "phoneNumbers", "phoneNumbers"),
            teacherEntity.children.map { it.name },
        )

        assertEquals(
            listOf("Arts", "History", "91", "96", "93"),
            teacherEntity.children.map { (it as XmlTextEntity).text },
        )
    }

    @Test
    fun `creates an XML composite entity from an annotated class, with entity wrappers and default names`() {
        data class Car(
            @XmlEntityWrapper
            val passengerNames: List<String>,

            @XmlEntityWrapper
            var passengerWeights: List<Double>,
        )

        val car = Car(passengerNames = listOf("John", "Tom", "Anya"), passengerWeights = listOf(70.0, 89.2, 65.0))
        val carEntity = XmlObjectBuilder.entity(car) as XmlCompositeEntity

        assertEquals(listOf("passengerNames", "passengerWeights"), carEntity.children.map { it.name })

        val namesEntity = carEntity.children[0] as XmlCompositeEntity

        assertEquals(
            listOf("passengerNames", "passengerNames", "passengerNames"),
            namesEntity.children.map { it.name },
        )

        assertEquals(listOf("John", "Tom", "Anya"), namesEntity.children.map { (it as XmlTextEntity).text })

        val weightsEntity = carEntity.children[1] as XmlCompositeEntity

        assertEquals(
            listOf("passengerWeights", "passengerWeights", "passengerWeights"),
            weightsEntity.children.map { it.name },
        )

        assertEquals(listOf("70.0", "89.2", "65.0"), weightsEntity.children.map { (it as XmlTextEntity).text })
    }

    @Test
    fun `creates an XML composite entity from an annotated class, with entity wrappers and given names`() {
        data class ComputerProgram(
            @XmlEntityWrapper(name = "booleans")
            @annotations.XmlEntity(name = "boolean")
            val booleanVariables: Set<Boolean>,

            @XmlEntityWrapper(name = "bytes")
            @annotations.XmlEntity(name = "byte")
            var byteVariables: MutableSet<Byte>,
        )

        val program = ComputerProgram(
            booleanVariables = setOf(false, true),
            byteVariables = mutableSetOf(101, 111, 100),
        )

        val programEntity = XmlObjectBuilder.entity(program) as XmlCompositeEntity

        assertEquals(listOf("booleans", "bytes"), programEntity.children.map { it.name })

        val booleansEntity = programEntity.children[0] as XmlCompositeEntity

        assertEquals(listOf("boolean", "boolean"), booleansEntity.children.map { it.name })
        assertEquals(listOf("false", "true"), booleansEntity.children.map { (it as XmlTextEntity).text })

        val bytesEntity = programEntity.children[1] as XmlCompositeEntity

        assertEquals(listOf("byte", "byte", "byte"), bytesEntity.children.map { it.name })
        assertEquals(listOf("101", "111", "100"), bytesEntity.children.map { (it as XmlTextEntity).text })
    }

    @Test
    fun `creates an XML composite entity from an annotated class, with entity wrappers, considering fallback elements`() {
        @XmlEntityWrapper
        @annotations.XmlEntity(name = "boolean")
        data class ProgramBooleanVariables(val variables: Set<Boolean>)

        @XmlEntityWrapper(name = "BytesOfProgram")
        @annotations.XmlEntity(name = "byte")
        data class ProgramByteVariables(var variables: MutableSet<Byte>)

        data class ComputerProgram(
            val booleanVariables: ProgramBooleanVariables,
            var byteVariables: ProgramByteVariables,
        )

        val program = ComputerProgram(
            booleanVariables = ProgramBooleanVariables(variables = setOf(false, true)),
            byteVariables = ProgramByteVariables(variables = mutableSetOf(101, 111, 100)),
        )

        val programEntity = XmlObjectBuilder.entity(program) as XmlCompositeEntity

        assertEquals(listOf("booleanVariables", "BytesOfProgram"), programEntity.children.map { it.name })

        val programBooleansEntity = programEntity.children[0] as XmlCompositeEntity

        assertEquals(listOf("boolean"), programBooleansEntity.children.map { it.name })

        val booleanVariablesEntity = programBooleansEntity.children[0] as XmlCompositeEntity

        assertEquals(listOf("variables", "variables"), booleanVariablesEntity.children.map { it.name })
        assertEquals(listOf("false", "true"), booleanVariablesEntity.children.map { (it as XmlTextEntity).text })

        val programBytesEntity = programEntity.children[1] as XmlCompositeEntity

        assertEquals(listOf("byte"), programBytesEntity.children.map { it.name })

        val byteVariablesEntity = programBytesEntity.children[0] as XmlCompositeEntity

        assertEquals(listOf("variables", "variables", "variables"), byteVariablesEntity.children.map { it.name })
        assertEquals(listOf("101", "111", "100"), byteVariablesEntity.children.map { (it as XmlTextEntity).text })
    }

    @Test
    fun `throws an error when creating an XML composite entity from an annotated class with an invalid name for its wrapper`() {
        data class ComputerProgram(
            @XmlEntityWrapper(name = "b@@le%nVar1#ables")
            val booleanVariables: Set<Boolean>,
        )

        val program = ComputerProgram(booleanVariables = setOf(true))
        val exception = assertThrows<IllegalArgumentException> { XmlObjectBuilder.entity(program) }

        assertEquals("Name must match the pattern '[a-zA-Z][a-zA-Z0-9]*'", exception.message)
    }

    @Test
    fun `creates an XML composite entity from an annotated class, with its name changed from using adapter`() {
        @annotations.XmlAdapter(NameAdapter::class)
        class Country

        val country = Country()
        val countryEntity = XmlObjectBuilder.entity(country) as XmlCompositeEntity

        assertEquals("CountryCHANGED", countryEntity.name)
    }

    @Test
    fun `creates an XML composite entity from an annotated class, with names changed from using adapter, considering fallback elements`() {
        @annotations.XmlAdapter(NameAdapter::class)
        class City

        class Region

        data class Country(
            val city: City,

            @annotations.XmlAdapter(NameAdapter::class)
            var area: String,

            @annotations.XmlAdapter(NameAdapter::class)
            val region: Region,
        )

        val country = Country(city = City(), area = "Spain", region = Region())
        val countryEntity = XmlObjectBuilder.entity(country) as XmlCompositeEntity

        assertEquals("Country", countryEntity.name)
        assertEquals(listOf("cityCHANGED", "areaCHANGED", "regionCHANGED"), countryEntity.children.map { it.name })
    }
}