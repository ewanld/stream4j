package com.github.stream4j;

public class TestStream4j {
	/**
	 * Run the main with no arguments and with assertions enabled to test.<br>
	 * The output should be: All tests OK
	 */
	public static void main(String[] args) {
		new TestConsumer().testAll();
		new TestFunction().testAll();
		new TestPredicate().testAll();
		new TestStream().testAll();
		System.out.println("All tests OK");
	}
}
