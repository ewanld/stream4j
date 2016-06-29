package com.github.stream4j;

/**
 * Represents a function that accepts one argument and produces a result.
 * @param <T> the type of the input to the function
 * @param <R> the type of the result of the function
 */
public abstract class Function<T, R> {
	public abstract R apply(T t);

	/**
	 * Returns a composed function that first applies this function to its input, and then applies the after function
	 * to the result.
	 * @param after the function to apply after this function is applied.
	 * @return a composed function that first applies this function and then applies the after function
	 * @throws NullPointerException if after is null
	 * @see #compose(Function)
	 */
	public <V> Function<T, V> andThen(final Function<? super R, ? extends V> after) {
		return new Function<T, V>() {

			@Override
			public V apply(T t) {
				return after.apply(Function.this.apply(t));
			}

		};
	}

	/**
	 * Returns a composed function that first applies the before function to its input, and then applies this function
	 * to the result. If evaluation of either function throws an exception, it is relayed to the caller of the
	 * composed function.
	 * @param before the function to apply before this function is applied
	 * @return a composed function that first applies the before function and then applies this function
	 * @throws NullPointerException if before is null
	 * @see #andThen(Function)
	 */
	public <V> Function<V, R> compose(final Function<? super V, ? extends T> before) {
		return new Function<V, R>() {

			@Override
			public R apply(V t) {
				return Function.this.apply(before.apply(t));
			}
		};
	}

	/**
	 * Returns a function that always returns its input argument.
	 * @return a function that always returns its input argument
	 */
	public static <T> Function<T, T> identity() {
		return new Function<T, T>() {
			@Override
			public T apply(T t) {
				return t;
			}
		};
	}
}
