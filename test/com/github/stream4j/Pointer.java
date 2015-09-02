package com.github.stream4j;

/**
 * Utility class to manage a reference to a variable. The main use case is to allow multiple output parameters from
 * methods. Example:
 * <pre>
 * void myFunction(String in1, Pointer<String> out1, Pointer<Integer> out2) {
 *	out1.set("ok");
 *	out2.set(1);
 * }
 * </pre>
 * 
 * To call the method:
 * <pre>
 * Pointer&lt;String&gt; pOut1 = new Pointer&lt;String&gt;();
 * Pointer&lt;Integer&gt; pOut2 = new Pointer&lt;Integer&gt;();
 * myFunction("toto", pOut1, pOut2);
 * String out1 = pOut1.get();
 * int out2 = pOut2.get();
 */
class Pointer<T> {

	private T value;

	public T get() {
		return value;
	}

	public void set(T value) {
		this.value = value;
	}
}
