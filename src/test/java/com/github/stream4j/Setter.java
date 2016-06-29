package com.github.stream4j;

final class Setter<T> extends Consumer<T> {
	private final Pointer<T> value;

	public Setter(Pointer<T> value) {
		this.value = value;

	}

	@Override
	public void accept(T t) {
		value.set(t);
	}
}