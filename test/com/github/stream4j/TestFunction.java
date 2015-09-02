package com.github.stream4j;

public class TestFunction {

	private final Function<Integer, Integer> x2 = new Function<Integer, Integer>() {

		@Override
		public Integer apply(Integer t) {
			return t * 2;
		}
	};
	
	private final Function<Integer, Integer> plus1 = new Function<Integer, Integer>() {
		
		@Override
		public Integer apply(Integer t) {
			return t + 1;
		}
	};
	
	public void testAll() {
		apply();
		andThen();
		compose();
		identity();
	}
	
	private void apply() {
		assert x2.apply(2) == 4;
	}

	private void andThen() {
		assert x2.andThen(plus1).apply(2) == 5;
	}

	private void compose() {
		assert x2.compose(plus1).apply(2) == 6;
	}

	private void identity() {
		assert Function.<Integer>identity().apply(1) == 1;
		assert Function.<String>identity().apply("a").equals("a");
	}
}
