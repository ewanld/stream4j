package com.github.stream4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Stream<T> {
	private final Iterator<T> iterator;
	private final int size;
	private final static int SIZE_UNKNOWN = -1;

	public Stream(Collection<T> wrapped) {
		this(wrapped.iterator(), wrapped.size());
	}

	public Stream(Iterator<T> iterator) {
		this(iterator, SIZE_UNKNOWN);
	}

	private Stream(Iterator<T> iterator, int size) {
		this.iterator = iterator;
		this.size = size;
	}

	/**
	 * Convenience method
	 */
	public static <T> Stream<T> of(T... items) {
		return new Stream<T>(Arrays.asList(items));
	}

	/**
	 * Returns whether all elements of this stream match the provided predicate.
	 * May not evaluate the predicate on all elements if not necessary for
	 * determining the result. If the stream is empty then true is returned and
	 * the predicate is not evaluated. <br>
	 * This is a short-circuiting terminal operation.
	 * 
	 * @param predicate
	 *            a non-interfering, stateless predicate to apply to elements of
	 *            this stream.
	 * @return true if either all elements of the stream match the provided
	 *         predicate or the stream is empty, otherwise false.
	 */
	public boolean allMatch(Predicate<? super T> predicate) {
		while (iterator.hasNext()) {
			final T t = iterator.next();
			if (!predicate.test(t)) {
				return false;
			}
		}
		return true;
	}

	public boolean anyMatch(Predicate<? super T> predicate) {
		while (iterator.hasNext()) {
			final T t = iterator.next();
			if (predicate.test(t)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the count of elements in this stream.
	 */
	public long count() {
		if (size != SIZE_UNKNOWN)
			return size;
		long res = 0;
		while (iterator.hasNext()) {
			iterator.next();
			res++;
		}
		return res;
	}

	public boolean noneMatch(Predicate<? super T> predicate) {
		while (iterator.hasNext()) {
			final T t = iterator.next();
			if (predicate.test(t)) {
				return false;
			}
		}
		return true;
	}

	public T findFirst() {
		return iterator.hasNext() ? iterator.next() : null;
	}

	public T findAny() {
		return findFirst();
	}

	public <R> Stream<R> map(final Function<T, R> transformer) {
		final TransformIterator<T, R> it = new TransformIterator<T, R>(iterator, transformer);
		final Stream<R> res = new Stream<R>(it);
		return res;
	}

	public List<T> toList() {
		final List<T> res = new ArrayList<T>(getCapacityHint());
		while (iterator.hasNext()) {
			res.add(iterator.next());
		}
		return res;
	}

	private int getCapacityHint() {
		return size != SIZE_UNKNOWN ? size : 10;
	}

	public Stream<T> filter(Predicate<T> predicate) {
		final FilterableIterator<T> it = new FilterableIterator<T>(iterator, predicate);
		return new Stream<T>(it);
	}

	public <K> Map<K, T> toMap(Function<T, K> toKey) {
		final Map<K, T> res = new HashMap<K, T>();
		while (iterator.hasNext()) {
			final T t = iterator.next();
			res.put(toKey.apply(t), t);
		}
		return res;
	}

	public void forEach(Consumer<? super T> action) {
		while (iterator.hasNext()) {
			final T t = iterator.next();
			action.accept(t);
		}
	}

	public void partitionBy(Predicate<T> predicate, Collection<T> matched, Collection<T> notMatched) {
		while (iterator.hasNext()) {
			final T t = iterator.next();
			if (predicate.test(t)) {
				matched.add(t);
			} else {
				notMatched.add(t);
			}
		}
	}

	public <K> Map<K, List<T>> groupBy(Function<T, K> classifier) {
		final Map<K, List<T>> res = new HashMap<K, List<T>>();
		while (iterator.hasNext()) {
			final T t = iterator.next();
			final K key = classifier.apply(t);
			List<T> l = res.get(key);
			if (l == null) {
				l = new ArrayList<T>();
				res.put(key, l);
			}
			l.add(t);
		}
		return res;
	}

	public T max(Comparator<? super T> comparator) {
		T res = null;
		while (iterator.hasNext()) {
			final T t = iterator.next();
			if (res == null || comparator.compare(t, res) > 0) {
				res = t;
			}
		}
		return res;
	}

	public T min(Comparator<? super T> comparator) {
		T res = null;
		while (iterator.hasNext()) {
			final T t = iterator.next();
			if (res == null || comparator.compare(t, res) < 0) {
				res = t;
			}
		}
		return res;
	}

	private static class TransformIterator<T, R> implements Iterator<R> {
		private final Function<T, R> transformer;
		private final Iterator<T> wrapped;

		private TransformIterator(Iterator<T> wrapped, Function<T, R> transformer) {
			this.wrapped = wrapped;
			this.transformer = transformer;
		}

		@Override
		public boolean hasNext() {
			return wrapped.hasNext();
		}

		@Override
		public R next() {
			return transformer.apply(wrapped.next());
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	private static class FilterableIterator<T> implements Iterator<T> {
		private final Iterator<T> wrapped;
		private final Predicate<T> predicate;
		private T nextItem;

		public FilterableIterator(Iterator<T> wrapped, Predicate<T> predicate) {
			this.wrapped = wrapped;
			this.predicate = predicate;
		}

		@Override
		public boolean hasNext() {
			nextItem = findNext();
			return nextItem != null;
		}

		@Override
		public T next() {
			final T res = nextItem != null ? nextItem : findNext();
			nextItem = null;
			return res;
		}

		private T findNext() {
			while (wrapped.hasNext()) {
				final T curr = wrapped.next();
				if (predicate.test(curr)) {
					return curr;
				}
			}
			return null;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
}
