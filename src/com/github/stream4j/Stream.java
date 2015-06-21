package com.github.stream4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

public class Stream<T> {
	private final Iterator<? extends T> iterator;
	private final long size;
	private final static int SIZE_UNKNOWN = -1;

	public Stream(Collection<? extends T> wrapped) {
		this(wrapped.iterator(), wrapped.size());
	}

	public Stream(Iterator<? extends T> iterator) {
		this(iterator, SIZE_UNKNOWN);
	}

	private Stream(Iterator<? extends T> iterator, long size) {
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

	public <R> Stream<R> map(final Function<? super T, ? extends R> transformer) {
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
		return size != SIZE_UNKNOWN ? (int) size : 10;
	}

	public Stream<T> filter(Predicate<? super T> predicate) {
		final FilterableIterator<T> it = new FilterableIterator<T>(iterator, predicate);
		return new Stream<T>(it);
	}

	public <K> Map<K, T> toMap(Function<? super T, ? extends K> toKey) {
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

	public void partitionBy(Predicate<? super T> predicate, Collection<? super T> matched, Collection<? super T> notMatched) {
		while (iterator.hasNext()) {
			final T t = iterator.next();
			if (predicate.test(t)) {
				matched.add(t);
			} else {
				notMatched.add(t);
			}
		}
	}

	public <K> Map<K, List<T>> groupBy(Function<? super T, ? extends K> classifier) {
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

	public <R> Stream<R> flatMap(Function<? super T, ? extends Stream<? extends R>> mapper) {
		final List<Iterator<? extends R>> iterators = new ArrayList<Iterator<? extends R>>(getCapacityHint());
		long size = 0;
		while (iterator.hasNext()) {
			final T t = iterator.next();
			Stream<? extends R> mapped = mapper.apply(t);
			size = mapped.size == SIZE_UNKNOWN ? SIZE_UNKNOWN : size + mapped.size;
			iterators.add(mapped.iterator);
		}
		final Iterator<? extends R> compositeIterator = new CompositeIterator<R>(iterators);
		return new Stream<R>(compositeIterator, size);
	}

	public Stream<T> limit(long maxSize) {
		if (maxSize < 0)
			throw new IllegalArgumentException("maxSize must be positive");
		if (size != SIZE_UNKNOWN && size <= maxSize) {
			return this;
		} else {
			long newSize = size == SIZE_UNKNOWN ? SIZE_UNKNOWN : maxSize;
			return new Stream<T>(new LimitIterator<T>(iterator, maxSize), newSize);
		}
	}

	public Stream<T> skip(long n) {
		if (n < 0)
			throw new IllegalArgumentException("maxSize must be positive");
		int i = 0;
		while (i++ < n && iterator.hasNext()) {
			iterator.next();
		}
		return this;
	}

	private static class CompositeIterator<T> implements Iterator<T> {
		private final List<Iterator<? extends T>> iterators;
		private int index = 0;

		public CompositeIterator(List<Iterator<? extends T>> iterators) {
			assert iterators != null;
			this.iterators = iterators;
		}

		@Override
		public boolean hasNext() {
			if (iterators.size() == 0)
				return false;
			boolean res = iterators.get(index).hasNext();
			if (!res && index < iterators.size() - 1)
				res = iterators.get(++index).hasNext();
			return res;
		}

		@Override
		public T next() {
			if (iterators.size() == 0)
				throw new NoSuchElementException();
			while (true) {
				if (iterators.get(index).hasNext()) {
					return iterators.get(index).next();
				} else if (index < iterators.size() - 1) {
					++index;
				} else {
					throw new NoSuchElementException();
				}
			}
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

	}

	private static class TransformIterator<T, R> implements Iterator<R> {
		private final Function<? super T, ? extends R> transformer;
		private final Iterator<? extends T> wrapped;

		private TransformIterator(Iterator<? extends T> wrapped, Function<? super T, ? extends R> transformer) {
			assert wrapped != null;
			assert transformer != null;

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
		private final Iterator<? extends T> wrapped;
		private final Predicate<? super T> predicate;
		private T nextItem;

		public FilterableIterator(Iterator<? extends T> wrapped, Predicate<? super T> predicate) {
			assert wrapped != null;
			assert predicate != null;

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

	private static class LimitIterator<T> implements Iterator<T> {

		private final long maxSize;
		private int count = 0;
		private final Iterator<? extends T> wrapped;

		public LimitIterator(Iterator<? extends T> wrapped, long maxSize) {
			assert wrapped != null;

			this.wrapped = wrapped;
			this.maxSize = maxSize;
		}

		@Override
		public boolean hasNext() {
			return count >= maxSize ? false : wrapped.hasNext();
		}

		@Override
		public T next() {
			if (count >= maxSize)
				throw new NoSuchElementException();
			count++;
			return wrapped.next();
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

	}
}
