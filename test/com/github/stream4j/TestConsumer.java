package com.github.stream4j;

import java.util.Arrays;

public class TestConsumer {
	private static final class Setter<T> extends Consumer<T> {
		private final Pointer<T> value;

		public Setter(Pointer<T> value) {
			this.value = value;

		}

		@Override
		public void accept(T t) {
			value.set(t);
		}

	}

	private static final Pointer<Integer> globalVal1 = new Pointer<Integer>();
	private static final Setter<Integer> setter1 = new Setter<Integer>(globalVal1);
	private static final Pointer<Integer> globalVal2 = new Pointer<Integer>();
	private static final Setter<Integer> setter2 = new Setter<Integer>(globalVal2);

	public void testAll() {
		accept();
		andThen();
	}

	private void accept() {
		for (Integer i : Arrays.asList(null, 1)) {
			setter1.accept(i);
			assert globalVal1.get() == i;
		}
	}

	private void andThen() {
		for (Integer i : Arrays.asList(null, 1)) {
			setter1.andThen(setter2).accept(i);
			assert globalVal1.get() == i;
			assert globalVal2.get() == i;

			setter1.andThen(setter1).accept(i);
			assert globalVal1.get() == i;
		}
	}
}
