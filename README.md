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
You may have noticed the weird formatting of the 'stringifyInt and 'gt2' anonymous classes. The main barrier to writing functional code in Java 6/7 is the extreme verbosity of small anonymous classes. The formatting above pushes all the cruft to the right side, and keeps the useful part to the left; it enhances readability greatly (once you are used to it), at the expense of writability.

This example uses "@formatter" directives used by Eclipse to disable formatting (they must be enabled in the Java formatter settings, tab "Off/on tags"); adapt to your favorite formatter (if your formatter doesn't have on/off directives, change formatter. Seriously.)
