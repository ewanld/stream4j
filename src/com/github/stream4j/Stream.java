package com.github.stream4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @param <T> the type of the stream elements
 */
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
	 * Returns a sequential ordered stream whose elements are the specified values.
	 */
	public static <T> Stream<T> of(T... items) {
		return new Stream<T>(Arrays.asList(items));
	}

	/**
	 * Returns a sequential ordered stream whose elements are the specified values.<br>
	 * Not part of the Java Stream API.
	 */
	public static <T> Stream<T> of(Collection<? extends T> wrapped) {
		return new Stream<T>(wrapped);
	}

	/**
	 * Returns a sequential ordered stream whose elements are the specified values.
	 * Not part of the Java Stream API.
	 */
	public static <T> Stream<T> of(Iterator<? extends T> wrapped) {
		return new Stream<T>(wrapped);
	}

	/**
	 * Returns whether all elements of this stream match the provided predicate.
	 * May not evaluate the predicate on all elements if not necessary for
	 * determining the result. If the stream is empty then true is returned and
	 * the predicate is not evaluated. <br>
	 * This is a short-circuiting terminal operation.
	 * @param predicate
	 *        a non-interfering, stateless predicate to apply to elements of
	 *        this stream.
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

	/**
	 * Returns whether any elements of this stream match the provided predicate. May not evaluate the predicate on all
	 * elements if not necessary for determining the result. If the stream is empty then false is returned and the
	 * predicate is not evaluated.<br>
	 * This is a short-circuiting terminal operation.
	 * @param predicate a non-interfering, stateless predicate to apply to elements of this stream
	 * @return true if any elements of the stream match the provided predicate, otherwise false
	 */
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
		if (size != SIZE_UNKNOWN) return size;
		long res = 0;
		while (iterator.hasNext()) {
			iterator.next();
			res++;
		}
		return res;
	}

	/**
	 * Returns whether no elements of this stream match the provided predicate. May not evaluate the predicate on all
	 * elements if not necessary for determining the result. If the stream is empty then true is returned and the
	 * predicate is not evaluated.<br>
	 * This is a short-circuiting terminal operation.
	 * @param predicate a non-interfering, stateless predicate to apply to elements of this stream
	 * @return true if either no elements of the stream match the provided predicate or the stream is empty, otherwise
	 *         false
	 */
	public boolean noneMatch(Predicate<? super T> predicate) {
		while (iterator.hasNext()) {
			final T t = iterator.next();
			if (predicate.test(t)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Return the first element of this stream, or null if the stream is empty.<br>
	 * This is a short-circuiting terminal operation.<br>
	 * Not part of the Java Stream API.
	 */
	public T findFirst() {
		return iterator.hasNext() ? iterator.next() : null;
	}

	/**
	 * Return the first element of this stream, or null if the stream is empty.<br>
	 * This is a short-circuiting terminal operation.<br>
	 * Not part of the Java Stream API.
	 */
	public T findAny() {
		return findFirst();
	}

	/**
	 * Returns a stream consisting of the results of applying the given function to the elements of this stream.
	 * This is an intermediate operation.
	 * @param mapper a non-interfering, stateless function to apply to each element
	 * @return the new stream
	 */
	public <R> Stream<R> map(final Function<? super T, ? extends R> mapper) {
		final TransformIterator<T, R> it = new TransformIterator<T, R>(iterator, mapper);
		final Stream<R> res = new Stream<R>(it);
		return res;
	}

	/**
	 * Not part of the Java Stream API.
	 */
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

	/**
	 * Returns a stream consisting of the elements of this stream that match the given predicate.<br>
	 * This is an intermediate operation.
	 * @param predicate a non-interfering, stateless predicate to apply to each element to determine if it should be
	 *        included
	 * @return the new stream
	 */
	public Stream<T> filter(Predicate<? super T> predicate) {
		final FilterableIterator<T> it = new FilterableIterator<T>(iterator, predicate);
		return new Stream<T>(it);
	}

	/**
	 * Not part of the Java Stream API.
	 */
	public <K> Map<K, T> toMap(Function<? super T, ? extends K> toKey) {
		final Map<K, T> res = new HashMap<K, T>();
		toMap(toKey, res);
		return res;
	}

	/**
	 * Not part of the Java Stream API.
	 */
	public <K> SortedMap<K, T> toSortedMap(Function<? super T, ? extends K> toKey) {
		final SortedMap<K, T> res = new TreeMap<K, T>();
		toMap(toKey, res);
		return res;
	}

	/**
	 * Not part of the Java Stream API.
	 */
	public <K> void toMap(Function<? super T, ? extends K> toKey, Map<K, T> map) {
		while (iterator.hasNext()) {
			final T t = iterator.next();
			map.put(toKey.apply(t), t);
		}
	}

	/**
	 * Performs an action for each element of this stream.<br>
	 * This is a terminal operation.
	 * @param action a non-interfering action to perform on the elements
	 */
	public void forEach(Consumer<? super T> action) {
		while (iterator.hasNext()) {
			final T t = iterator.next();
			action.accept(t);
		}
	}

	/**
	 * Not part of the Java Stream API.
	 */
	public void partitionBy(Predicate<? super T> predicate, Collection<? super T> matched,
			Collection<? super T> notMatched) {
		while (iterator.hasNext()) {
			final T t = iterator.next();
			if (predicate.test(t)) {
				matched.add(t);
			} else {
				notMatched.add(t);
			}
		}
	}

	/**
	 * Not part of the Java Stream API.
	 */
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

	/**
	 * Returns the maximum element of this stream according to the provided Comparator. This is a special case of a
	 * reduction.<br>
	 * This is a terminal operation.<br>
	 * The method from the Java Stream API has a different signature:
	 * {@code Optional<T> max(Comparator<? super T> comparator)}
	 */
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

	public T max() {
		return max(createComparator());
	}
	
	/**
	 * Returns the minimum element of this stream according to the provided Comparator. This is a special case of a
	 * reduction.<br>
	 * This is a terminal operation.<br>
	 * The method from the Java Stream API has a different signature:
	 * {@code Optional<T> max(Comparator<? super T> comparator)}
	 */
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
	
	public T min() {
		return min(createComparator());
	}

	/**
	 * Returns a stream consisting of the results of replacing each element of this stream with the contents of a
	 * mapped stream produced by applying the provided mapping function to each element. Each mapped stream is closed
	 * after its contents have been placed into this stream. (If a mapped stream is null an empty stream is used,
	 * instead.)<br>
	 * This is an intermediate operation.
	 * @param mapper a non-interfering, stateless function to apply to each element which produces a stream of new
	 *        values
	 * @return the new stream
	 */
	public <R> Stream<R> flatMap(Function<? super T, ? extends Stream<? extends R>> mapper) {
		final List<Iterator<? extends R>> iterators = new ArrayList<Iterator<? extends R>>(getCapacityHint());
		long totalSize = 0;
		while (iterator.hasNext()) {
			final T t = iterator.next();
			Stream<? extends R> mapped = mapper.apply(t);
			if (mapped == null) mapped = Stream.of(Collections.<R> emptyList());
			totalSize = mapped.size == SIZE_UNKNOWN || totalSize == SIZE_UNKNOWN ? SIZE_UNKNOWN : totalSize + mapped.size;
			iterators.add(mapped.iterator);
		}
		final Iterator<? extends R> compositeIterator = new CompositeIterator<R>(iterators);
		return new Stream<R>(compositeIterator, totalSize);
	}

	/**
	 * Returns a stream consisting of the elements of this stream, truncated to be no longer than maxSize in length.<br>
	 * This is a short-circuiting stateful intermediate operation.
	 * @param maxSize the number of elements the stream should be limited to
	 * @return the new stream
	 */
	public Stream<T> limit(long maxSize) {
		if (maxSize < 0) throw new IllegalArgumentException("maxSize must be positive");
		if (size != SIZE_UNKNOWN && size <= maxSize) {
			return this;
		} else {
			final long newSize = size == SIZE_UNKNOWN ? SIZE_UNKNOWN : maxSize;
			return new Stream<T>(new LimitIterator<T>(iterator, maxSize), newSize);
		}
	}

	/**
	 * Returns a stream consisting of the remaining elements of this stream after discarding the first n elements of
	 * the stream. If this stream contains fewer than n elements then an empty stream will be returned.<br>
	 * This is a stateful intermediate operation.
	 * @param n the number of leading elements to skip
	 * @return the new stream
	 * @throws IllegalArgumentException if n is negative
	 */
	public Stream<T> skip(long n) {
		if (n < 0) throw new IllegalArgumentException("maxSize must be positive");
		int i = 0;
		while (i++ < n && iterator.hasNext()) {
			iterator.next();
		}
		return this;
	}

	/**
	 * Returns a stream consisting of the elements of this stream, sorted according to the provided Comparator.<br>
	 * For ordered streams, the sort is stable. For unordered streams, no stability guarantees are made.<br>
	 * This is a stateful intermediate operation.<br>
	 * @param comparator a non-interfering, stateless Comparator to be used to compare stream elements
	 * @return the new stream
	 */
	public Stream<T> sorted(Comparator<? super T> comparator) {
		final List<T> list = toList();
		Collections.sort(list, comparator);
		final Stream<T> res = new Stream<T>(list);
		return res;
	}

	/**
	 * Returns a stream consisting of the elements of this stream, sorted according to natural order. If the elements
	 * of this stream are not Comparable, a java.lang.ClassCastException may be thrown when the terminal operation is
	 * executed.<br>
	 * For ordered streams, the sort is stable. For unordered streams, no stability guarantees are made.
	 * This is a stateful intermediate operation.<br>
	 * @return the new stream
	 */
	public Stream<T> sorted() {
		return sorted(createComparator());
	}

	private Comparator<T> createComparator() {
		return new Comparator<T>() {

			@Override
			public int compare(T o1, T o2) {
				@SuppressWarnings("unchecked") final Comparable<T> c1 = (Comparable<T>) o1;
				return c1.compareTo(o2);
			}
		};
	}

	private static class CompositeIterator<T> implements Iterator<T> {
		private final List<Iterator<? extends T>> iterators;
		/**
		 * Index of the current iterator in the {@code iterators} list.
		 */
		private int index = 0;

		public CompositeIterator(List<Iterator<? extends T>> iterators) {
			assert iterators != null;
			this.iterators = iterators;
		}

		@Override
		public boolean hasNext() {
			while (true) {
				if (index > iterators.size() - 1) {
					return false;
				} else if (iterators.get(index).hasNext()) {
					return true;
				} else {
					++index;
				}
			}
		}

		@Override
		public T next() {
			if (!hasNext()) throw new NoSuchElementException();
			return iterators.get(index).next();
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
			if (count >= maxSize) throw new NoSuchElementException();
			count++;
			return wrapped.next();
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

	}
}
