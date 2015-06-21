# stream4j
Simple substitute for package java.util.stream, when working with legacy Java versions (&lt; 8).

## Quickstart
```java
Function<Integer, String> stringifyInt =/*						@formatter:off*/ new Function<Integer, String>() { @Override public String apply(Integer t)  {
  return "the number " + t; 										}};  /*@formatter:on*/

Predicate<Integer> gt2 =/* 										@formatter:off*/ new Predicate<Integer>() { @Override public boolean test(Integer t){
  return t > 2; 												}}; /* @formatter:on */

Stream<Integer> stream = Stream.of(1, 3, 2, 56);
List<String> l = stream.filter(gt2).map(stringifyInt).toList();
// l is: ["the number 3", "the number 56"]
```
