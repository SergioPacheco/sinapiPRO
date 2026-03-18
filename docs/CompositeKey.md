# Simple Composite Key

A simple key is when the id uses just one field. A simple key is used like below:

A composite key is needed when more than one attribute is required as an entity id. It is possible to find simple composite key and complex composite key. With a Simple composite key use only plain Java attributes for the id (e.g String, int, ...). In the following sections we will disquas all about complex composite key semantics.

There are two ways to map a simple composite key, with **@IdClass** or **@EmbeddedId**.

```
	@Id
	private int id; 

``` 

A composite key is needed when more than one attribute is required as an entity id. It is possible to find simple composite key and complex composite key. With a Simple composite key use only plain Java attributes for the id (e.g String, int, ...). In the following sections we will disquas all about complex composite key semantics.
There are two ways to map a simple composite key, with @IdClass or @EmbeddedId



## @IdClass

Check the code below:

```java
import javax.persistence.*;

@Entity
@IdClass(CarId.class)
public class Car {

	@Id
	private int serial;

	@Id
	private String brand;

	private String name;

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getSerial() {
		return serial;
	}
	public void setSerial(int serial) {
		this.serial = serial;
	}
	public String getBrand() {
		return brand;
	}
	public void setBrand(String brand) {
		this.brand = brand;
	}
}


```

About the code above;

* @IdClass(CarId.class) => this annotation indicates that the CarId has inside of it the id attributes found in the Car entity.
* All fields annotated with @Id must be found in the IdClass.
* It is possible to use the @GeneratedValue annotation with this kind of composite key. For example in the code above it would be possible to use the @GeneratedValue with the "serial"
attribute.

Check below the CarId class code:

import java.io.Serializable;


```java
public class CarId implements Serializable{

	private static final long serialVersionUID = 343L;

	private int serial;

	private String brand;

	// must have a default constructor
	public CarId() {
	}

	public CarId(int serial, String brand) {
		this.serial = serial;
		this.brand = brand;
	}

	public int getSerial() {
		return serial;
	}
	public String getBrand() {
		return brand;
	}
	// Must have a hashCode method
	@Override
	public int hashCode() {
		return serial + brand.hashCode();
	}
	// Must have an equals method
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof CarId) {
			CarId carId = (CarId) obj;
			return carId.serial == this.serial && carId.brand.equals(this.brand);
		}
		return false;
	}
}
```

The class CarId has the fields listed as @Id in the Car entity.

To use a class as ID it must follow the rules below:
* A public constructor with no arguments must be found
* Implements the Serializable interface
* Overwrite the hashCode/equals method

To do a query against a database to find an entity given a simple composite key just do like below:


```java
	EntityManager em = // get valid entity manager
	CarId carId = new CarId(33, "Ford");
	Car persistedCar = em.find(Car.class, carId);
	System.out.println(persistedCar.getName() + " - " + persistedCar.getSerial());
``` 
To use the find method it is necessary to provide the id class with the required information. 


## @Embeddable

The other approach to use the composite key is presented below:

```java 
import javax.persistence.*;
@Entity

public class Car {

	@EmbeddedId
	private CarId carId;

	private String name;

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public CarId getCarId() {
		return carId;
	}
	public void setCarId(CarId carId) {
		this.carId = carId;
	}
}
``` 

About the code above:

* The id class was written inside the Car class.
* The @EmbeddedId is used to define the class as an id class.
* There is no need to use the @Id annotation anymore.

The class id will be like below:

```java
import java.io.Serializable;
import javax.persistence.Embeddable;

@Embeddable
public class CarId implements Serializable{

	private static final long serialVersionUID = 343L;

	private int serial;

	private String brand;

	// must have a default construcot
	public CarId() {
	}

	public CarId(int serial, String brand) {
		this.serial = serial;
		this.brand = brand;
	}

	public int getSerial() {
		return serial;
	}

	public String getBrand() {
		return brand;
	}

	// Must have a hashCode method
	@Override
	public int hashCode() {
		return serial + brand.hashCode();
	}

	// Must have an equals method
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof CarId) {
			CarId carId = (CarId) obj;
			return carId.serial == this.serial && carId.brand.equals(this.brand);
		}
		return false;
	}
}
``` 

About the code above:

* The @Embeddable annotation allows the class to be used as id.
* The fields inside the class will be used as ids.

To use a class as ID it must follow the rules below:

* A public constructor with no arguments must be found
* Implements the Serializable interface
* Overwrite the hashCode/equals method

It is possible to do queries with this kind of composite key just like the @IdClass presented above.


## Complex Composite Key

A complex composite key is composed of other entities – not plain Java attributes.
Imagine an entity DogHouse where it uses the Dog as the id. Take a look at the code below:

```java
import javax.persistence.*;

@Entity
public class Dog {

	@Id
	private int id;

	private String name;

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
```

```java
import javax.persistence.*;

@Entity
public class DogHouse {

	@Id
	@OneToOne
	@JoinColumn(name = "DOG_ID")
	private Dog dog;

	private String brand;
	
	public Dog getDog() {
		return dog;
	}
	public void setDog(Dog dog) {
		this.dog = dog;
	}
	public String getBrand() {
		return brand;
	}
	public void setBrand(String brand) {
	this.brand = brand;
	}
}
```

About the code above:

* The @Id annotation is used in the entity DogHouse to inform JPA that the DogHouse will have the same id as the Dog.
* We can combine the @Id annotation with the @OneToOne annotation to dictate that there is an explicit relationship between the classes. More information about the @OneToOne annotation will be available later on.

Imagine a scenario where it is required to access the DogHouse id without passing through the class Dog (dogHouse.getDog().getId()). JPA has a way of doing it without the need of the Demeter Law pattern:

```java 
import javax.persistence.*;

@Entity
public class DogHouseB {

	@Id
	private int dogId;

	@MapsId
	@OneToOne
	@JoinColumn(name = "DOG_ID")
	private Dog dog;
	
	private String brand;
	
	public Dog getDog() {
		return dog;
	}
	public void setDog(Dog dog) {
		this.dog = dog;
	}
	public String getBrand() {
		return brand;
	}
	public void setBrand(String brand) {
		this.brand = brand;
	}
	public int getDogId() {
		return dogId;
	}
	public void setDogId(int dogId) {
		this.dogId = dogId;
	}
}
```

About the code above:

* There is an explicit field mapped with @Id.
* The Dog entity is mapped with the @MapsId annotation. This annotation indicates that JPA will use the Dog.Id as the DogHouse.DogId (just like before); the dogId attribute will have the same value as the dog.getId() and this value will be attributed at run time.
* dogId field does not need to be explicitely mapped to a database table column. When the application starts JPA will attribute the dog.getId() to the dogId attribute.

To finish this subject, let us see one more topic. How can we map an entity id with more than one entities?

Check the code below:

```java
import javax.persistence.*;
@Entity
@IdClass(DogHouseId.class)
public class DogHouse {
	@Id
	@OneToOne
	@JoinColumn(name = "DOG_ID")
	private Dog dog;
	@Id
	@OneToOne
	@JoinColumn(name = "PERSON_ID")
	private Person person;
	private String brand;
	// get and set
}
```
About the code above:

* Notice that both entities (Dog and Person) were annotated with @Id.
* The annotation @IdClass is used to indicate the need of a class to map the id.

```java 
import java.io.Serializable;
public class DogHouseId implements Serializable{

private static final long serialVersionUID = 1L;

	private int person;
	private int dog;

	public int getPerson() {
		return person;
	}

	public void setPerson(int person) {
		this.person = person;
	}

	public int getDog() {
		return dog;
	}

	public void setDog(int dog) {
		this.dog = dog;
	}

	@Override
	public int hashCode() {
		return person + dog;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof DogHouseId){
			DogHouseId dogHouseId = (DogHouseId) obj;
		return dogHouseId.dog == dog && dogHouseId.person == person;
		}
		return false;
	}
}
```
About the code above:

* The class has the same amount of attributes as the number of attributes in the class DogHouse annotated with @Id
* Notice that the attributes inside the DogHouseId have the same name as the attributes inside the DogHouse annotated with @Id. This is mandatory for JPA to correctly utilize id functionality.
For example If we named the Person type attribute inside the DogHouse class as
"dogHousePerson" the name of the Person type attribute inside the DogHouseId class would
have to change also to "dogHousePerson".

To use a class as ID it must follow the rules below:

* public constructor with no arguments must be found
* Implements the Serializable interface
* Overwrite the hashCode/equals method


