# Cobol Mapper
Lightweight Java library to map positional Cobol Strings.

# How to Use
The use is pretty straightforward:

```java
ResultDto result = new CobolMapper().map(cobolInput, ResultDto.class);
```

You can set a custom **DateTimeFormatter** for date and dateTime values. If not set, the defaults are **DateTimeFormatter.ISO_DATE_TIME** and **DateTimeFormatter.ISO_DATE**:
```java
ResultDto result = new CobolMapper()
        .withDateTimeFormatter("yyyy-MM-dd'T'HH:mm:ss").withDateFormatter("yyyy-MM-dd")
        .map(cobolInput, ResultDto.class);
```

You can change the **delimiterSize** by using **withDelimiterSize()** method. If not set, the default is **1**.  
Remember that the annotation works with the exact starting end ending position of the elements, so if you add a new different delimiter when you were already using another one, you also need to increase/decrease those values. 

```java
String cobolInput = "mario     ,,rossi     ,,";

ResultDto result = new CobolMapper()
        .withDelimiterSize(2)
        .map(cobolInput, ResultDto.class);
```

# @CobolSegment
This annotation is the most important part that needs to be set corrrectly for the mapper to work. The 3 params need to be used as follow:

## Wrapper Objects (String, Integer...) and Complex Objects (Person, Car...)
<pre>mario     ;rossi     ;</pre>
- **start**: The starting position to parse the segment and it's always the character after the delimiter. In this case setting it to **0** will start from the **m** in **mario**.
- **end**: The ending position of the segment and it's always the character before the delimiter. In this case setting it to **9** will read the last space in the **mario** segment before the delimiter **";"**.

### Example Wrapper Objects
<pre>mario     ;rossi     ;</pre>

````java
public class ResultDto {
    @CobolSegment(start = 0, end = 10)
    String name;
    @CobolSegment(start = 11, end = 21)
    String surname;
}
````

### Example Complex Objects
The annotation is used without any parameter set, that's because they need to be specified inside the **Object** itself.
<pre>mario     ;rossi     ;</pre>

````java
public class Person {
    @CobolSegment(start = 0, end = 10)
    String name;
    @CobolSegment(start = 11, end = 21)
    String surname;
}

public class ResultDto {
    @CobolSegment
    Person person;
}
````

## Lists of Wrapper Objects (List\<String\>, List\<Integer\>...)
<pre>red  ;blue ;green;</pre>
- **start**: The starting position of the list and it's always the character after the delimiter. In this case setting it to **0** will start from the **r** in **red**.
- **end**: The ending position of the list and it's always the character before the delimiter. In this case setting it to **17** will read the **n** in **green**.
- **listElementSize**: This is used to set the size of every element of the list. In this case it's **5**.

### Example
<pre>red  ;blue ;green;</pre>

````java
public class ResultDto {
    @CobolSegment(start = 0, end = 17, listElementSize = 5)
    List<String> colorList;
}
````

## Lists of Complex Objects (List\<Person\>, List\<Car\>...)
<pre>mario     ;rossi     ;dario     ;verdi     ;</pre>
- **listElementSize**: In this case the size is calculated from the start to the end of the element. You also need to count all the delimiters in between but never the starting and ending ones. So in this case you count from **m** in **mario** to the last space in **rossi** before the delimiter.  
**mario** size (**10**) + **rossi** size (**10**) + **delimiter** (**1**) = **21**.

Also the **@CobolSegment** inside the **Complex Objects** need to be always set at the first element position. The mapper will increase the offset while cycling through the elements. 

## Example
<pre>mario     ;rossi     ;dario     ;verdi     ;</pre>

````java
public class Person {
    @CobolSegment(start = 0, end = 10)
    String name;
    @CobolSegment(start = 11, end = 21)
    String surname;
}

public class ResultDto {
    @CobolSegment(start = 0, end = 43, listElementSize = 21)
    List<Person> personList;
}
````

## Lists of Lists of Lists...
The **Cobol Mapper** uses **recursion** to go deeper inside the objects until the last **Wrapper Object** is found.

## Example
<pre>apple     ;red       ;green     ;banana    ;yellow    ;green     ;</pre>

````java
public class Fruit {
    @CobolSegment(start = 0, end = 10)
    String name;
    @CobolSegment(start = 11, end = 32, listElementSize = 10)
    List<String> possibleColors;
}

public class ResultDto {
    @CobolSegment(start = 0, end = 65, listElementSize = 32)
    List<Fruit> fruitList;
}
````