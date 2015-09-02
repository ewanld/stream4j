package com.github.stream4j;

public class TestPredicate {
	private static final Predicate<Integer> gt4 = new Predicate<Integer>() {

		@Override
		public boolean test(Integer t) {
			return t > 4;
		}
	};

	private static final Predicate<Integer> lt2 = new Predicate<Integer>() {

		@Override
		public boolean test(Integer t) {
			return t < 2;
		}
	};

	public void testAll() {
		and();
		negate();
		or();
		isEqual();
	}

	private void and() {
		assert !gt4.and(Predicate.isEqual(2)).test(2);
		assert gt4.and(Predicate.isEqual(5)).test(5);
	}

	private void negate() {
		final Predicate<Integer> lt5 = gt4.negate();
		assert lt5.test(1);
		assert !lt5.test(6);

	}

	private void isEqual() {
		final Predicate<Object> equals_a = Predicate.isEqual("a");
		final Predicate<Object> equals_null = Predicate.isEqual(null);

		assert equals_a.test("a");
		assert !equals_a.test("b");
		assert !equals_a.test(null);
		assert !equals_null.test("a");
		assert equals_null.test(null);
	}

	private void or() {
		final Predicate<Integer> not234 = gt4.or(lt2);
		assert not234.test(1);
		assert !not234.test(3);
		assert not234.test(5);
	}
}
