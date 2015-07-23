package com.github.stream4j;

public abstract class Predicate<T> {
	public abstract boolean test(T t);

	public Predicate<T> isEqual(final Object targetRef) {
		return new Predicate<T>() {
			@Override
			public boolean test(T t) {
				return t.equals(targetRef);
			}
		};
	}

	public Predicate<T> negate() {
		final Predicate<T> that = this;
		return new Predicate<T>() {

			@Override
			public boolean test(T t) {
				return !that.test(t);
			}
		};
	}

	public Predicate<T> and(final Predicate<? super T> other) {
		final Predicate<T> that = this;
		return new Predicate<T>() {

			@Override
			public boolean test(T t) {
				return that.test(t) && other.test(t);
			}
		};

	}

	public Predicate<T> or(final Predicate<? super T> other) {
		final Predicate<T> that = this;
		return new Predicate<T>() {

			@Override
			public boolean test(T t) {
				return that.test(t) || other.test(t);
			}
		};
	}
}
