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
- **startPos**: The starting position of the segment to parse and it's always the character after the delimiter. In this case setting it to **0** will start from the **m** in **mario**.
- **length**: The segment length excluding the delimiters. In this case is **10**.

### Example Wrapper Objects
<pre>mario     ;rossi     ;</pre>

````java
public class ResultDto {
    @CobolSegment(startPos = 0, length = 10)
    String name;
    @CobolSegment(startPos = 11, length = 10)
    String surname;
}
````

### Example Complex Objects
The annotation is used without any parameter set, that's because they need to be specified inside the **Complex Object** itself.
<pre>mario     ;rossi     ;</pre>

````java
public class Person {
    @CobolSegment(startPos = 0, length = 10)
    String name;
    @CobolSegment(startPos = 11, length = 10)
    String surname;
}

public class ResultDto {
    @CobolSegment
    Person person;
}
````

## Lists of Wrapper Objects (List\<String\>, List\<Integer\>...)
<pre>red  ;blue ;green;</pre>
- **startPost**: The starting position of the list and it's always the character after the delimiter. In this case setting it to **0** will start from the **r** in **red**.
- **length**: The segment length excluding the delimiters. In this case is **5** characters long.
- **listLength**: This is used to set the size of whole list. Always excluding the starting/ending delimiters, but counting the ones in between. So here the listLength is **17**.

### Example
<pre>red  ;blue ;green;</pre>

````java
public class ResultDto {
    @CobolSegment(startPos = 0, length = 5, listLength = 17)
    List<String> colorList;
}
````

## Lists of Complex Objects (List\<Person\>, List\<Car\>...)
<pre>mario     ;rossi     ;dario     ;verdi     ;</pre>
- **length**: In this case the **length** is calculated for the whole **Complex Objects** to parse. Also count the **delimiters in between**, but never the starting/ending ones. So **name segment** + **surname segment** + **one delimiter** = **21**.  
- **listLength**: As above, you need to count the delimiters in between the whole list. So the listLength is **43**.
**mario** size (**10**) + **rossi** size (**10**) + **delimiter** (**1**) = **21**.

Also the **@CobolSegment** inside the **Complex Objects** need to be always set at the first element position. The mapper will increase the offset while cycling through the elements. 

## Example
<pre>mario     ;rossi     ;dario     ;verdi     ;</pre>

````java
public class Person {
    @CobolSegment(startPos = 0, length = 10)
    String name;
    @CobolSegment(startPos = 11, length = 10)
    String surname;
}

public class ResultDto {
    @CobolSegment(startPos = 0, length = 21, listLength = 43)
    List<Person> personList;
}
````

## Lists of Lists of Lists...
The **Cobol Mapper** uses **recursion** to go deeper inside the objects until the last **Wrapper Object** is found.

## Example
<pre>apple     ;red       ;green     ;banana    ;yellow    ;green     ;</pre>

````java
public class Fruit {
    @CobolSegment(startPos = 0, length = 10)
    String name;
    @CobolSegment(startPos = 11, length = 10, listLength = 21)
    List<String> possibleColors;
}

public class ResultDto {
    @CobolSegment(startPos = 0, length = 32, listLength = 65)
    List<Fruit> fruitList;
}
````