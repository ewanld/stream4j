package com.github.stream4j;

/**
 * Represents a predicate (boolean-valued function) of one argument.
 * @param <T> the type of the input to the predicate
 */
public abstract class Predicate<T> {
	/**
	 * Evaluates this predicate on the given argument.
	 * @param t the input argument
	 * @return true if the input argument matches the predicate, otherwise false
	 */
	public abstract boolean test(T t);

	/**
	 * Returns a predicate that tests if two arguments are equal.
	 * @param targetRef the object reference with which to compare for equality, which may be null
	 */
	public static <T> Predicate<T> isEqual(final Object targetRef) {
		return new Predicate<T>() {
			@Override
			public boolean test(T t) {
				if (t != null) {
					 return t.equals(targetRef);
				} else {
					return targetRef == null;
				}
			}
		};
	}

	/**
	 * Returns a predicate that represents the logical negation of this predicate.
	 */
	public Predicate<T> negate() {
		final Predicate<T> that = this;
		return new Predicate<T>() {

			@Override
			public boolean test(T t) {
				return !that.test(t);
			}
		};
	}

	/**
	 * Returns a composed predicate that represents a short-circuiting logical AND of this predicate and another. When
	 * evaluating the composed predicate, if this predicate is false, then the other predicate is not evaluated.
	 * <p>
	 * Any exceptions thrown during evaluation of either predicate are relayed to the caller; if evaluation of this
	 * predicate throws an exception, the other predicate will not be evaluated.
	 * @param other a predicate that will be logically-ANDed with this predicate
	 * @return a composed predicate that represents the short-circuiting logical AND of this predicate and the other
	 *         predicate
	 * @throws NullPointerException if other is null
	 */
	public Predicate<T> and(final Predicate<? super T> other) {
		final Predicate<T> that = this;
		return new Predicate<T>() {

			@Override
			public boolean test(T t) {
				return that.test(t) && other.test(t);
			}
		};

	}

	/**
	 * Returns a composed predicate that represents a short-circuiting logical OR of this predicate and another. When
	 * evaluating the composed predicate, if this predicate is true, then the other predicate is not evaluated.
	 * <p>
	 * Any exceptions thrown during evaluation of either predicate are relayed to the caller; if evaluation of this
	 * predicate throws an exception, the other predicate will not be evaluated.
	 * @param other a predicate that will be logically-ORed with this predicate
	 * @return a composed predicate that represents the short-circuiting logical OR of this predicate and the other
	 *         predicate
	 * @throws NullPointerException if other is null
	 */
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
