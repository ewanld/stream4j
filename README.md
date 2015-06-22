# stream4j
Simple substitute for package java.util.stream, when working with legacy Java versions (&lt; 8).

## Quickstart
```java
Function<Integer, String> stringifyInt =/*						@formatter:off*/ new Function<Integer, String>() { @Override public String apply(Integer t)  {
  return "the number " + t; 									}}; /*@formatter:on*/

Predicate<Integer> gt2 =/* 										@formatter:off*/ new Predicate<Integer>() { @Override public boolean test(Integer t){
  return t > 2; 												}}; /*@formatter:on*/

Stream<Integer> stream = Stream.of(1, 3, 2, 56);
List<String> l = stream.filter(gt2).map(stringifyInt).toList();
// l is: ["the number 3", "the number 56"]
```

## Code formatting
You may have noticed the weird formatting of the ```stringifyInt``` and ```gt2``` anonymous classes. The main barrier to writing functional code in Java 6/7 is the extreme verbosity of small anonymous classes. The formatting above pushes all the cruft to the right side, and keeps the useful part to the left; it enhances readability greatly (once you are used to it), at the expense of writability.

This example uses ```@formatter``` directives used by Eclipse to disable formatting (they must be enabled in the Java formatter settings, tab "Off/on tags"); adapt to your favorite formatter (if your formatter doesn't have on/off directives, change formatter. Seriously.)

## Examples
```java
Stream.of(3, 2, 1, 4).filter(gt2);  //[3, 4]
Stream.of(3, 2).allMatch(gt2);      //true
Stream.of(3, 2, 1).allMatch(gt2);   //false
Stream.of(3, 2, 1).anyMatch(gt2);   //true
Stream.of(3, 2, 1).count();         //3
Stream.of(2, 1).findFirst();        //2
Stream.of(3, 1).toMap(stringfyInt); //{ "the number 3": 3, "the number 1": 1 }
Stream.of(3, 2, 1).partitionBy(gt2, list1, list2);  //list1=[3], list2=[2, 1]
Stream.of(3, 2, 1).sorted();        //[1, 2, 3]
```

## Build from source
Execute ```ant``` from the project root directory . A file ```dist/stream4j.jar``` is  created. 

## Design rationale
* The design mimics the ```java.util.function``` package, so when you migrate to JDK8 you can switch to this package with ease.
* This library is fully KISS/YAGNI-compliant (< 400 lines of code). You can read the whole source code in less than 10 minutes.
* It does not try to be theoretically elegant or full-featured, but focuses on being simple and practical.
* The more complex parts of the Stream API, like Collectors for instance, have been left out. Some collectors are implemented directly in the Stream class (check out ```groupBy```, ```partitionBy``` and ```toMap```).
