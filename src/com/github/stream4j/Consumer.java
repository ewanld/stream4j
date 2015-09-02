package com.github.stream4j;

/**
 * Represents an operation that accepts a single input argument and returns no result. Unlike most other functional
 * interfaces, Consumer is expected to operate via side-effects.
 * @param <T> the type of the input to the operation
 */
public abstract class Consumer<T> {
	/**
	 * Performs this operation on the given argument.
	 * @param t the input argument
	 */
	public abstract void accept(T t);

	/**
	 * Returns a composed Consumer that performs, in sequence, this operation followed by the after operation. If
	 * performing either operation throws an exception, it is relayed to the caller of the composed operation. If
	 * performing this operation throws an exception, the after operation will not be performed.
	 * @param after the operation to perform after this operation
	 * @return a composed Consumer that performs in sequence this operation followed by the after operation
	 * @throws NullPointerException if after is null
	 */
	public Consumer<T> andThen(final Consumer<? super T> after) {
		final Consumer<T> that = this;
		return new Consumer<T>() {

			@Override
			public void accept(T t) {
				that.accept(t);
				after.accept(t);
			}
		};
	}
}
