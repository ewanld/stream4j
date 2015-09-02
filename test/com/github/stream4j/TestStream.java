package com.github.stream4j;

import java.util.ArrayList;
import java.util.Arrays;

public class TestStream {
	private static final ArrayList<Integer> emptyList = new ArrayList<Integer>();
	private static  final Predicate<Integer> gt2 = new Predicate<Integer>() {

		@Override
		public boolean test(Integer t) {
			return t > 2;
		}
	};

	public  void testAll() {
		allMatch();
		anyMatch();
		count();
		filter();
		findAny();
		findFirst();
		flatMap();
		forEach();
		groupBy();
		limit();
		map();
		max();
		min();
		noneMatch();
		partitionBy();
		skip();
		sorted();
		sorted_withComparator();
		toList();
		toMap();
		toSortedMap();
	}
	
	private void allMatch() {
		assert Stream.of(emptyList).allMatch(gt2);
		assert Stream.of(emptyList.iterator()).allMatch(gt2);
		assert !Stream.of(1, 2, 3).allMatch(gt2);
		assert !Stream.of(0, 1).allMatch(gt2);
		assert Stream.of(3, 4, 5).allMatch(gt2);
	}

	private void anyMatch() {
		assert !Stream.of(emptyList).anyMatch(gt2);
		assert !Stream.of(emptyList.iterator()).anyMatch(gt2);
		assert Stream.of(1, 2, 3).anyMatch(gt2);
		assert !Stream.of(0, 1).anyMatch(gt2);
		assert Stream.of(3, 4, 5).anyMatch(gt2);
	}

	private void count() {
		assert Stream.of(emptyList).count() == 0;
		assert Stream.of(emptyList.iterator()).count() == 0;
		assert Stream.of(1, 2, 3).count() == 3;
		assert Stream.of(1, 2, 3).count() == 3;
	}

	private void filter() {
		assert Stream.of(emptyList).filter(gt2).count() == 0;
		assert Stream.of(emptyList.iterator()).filter(gt2).count() == 0;
		assert Stream.of(1, 2, 3).filter(gt2).toList().equals(Arrays.asList(3));

	}

	private void findAny() {

	}

	private void findFirst() {
		//TODO
	}

	private void flatMap() {
		//TODO
	}

	private void forEach() {
		//TODO
	}

	private void groupBy() {
		//TODO
	}

	private void limit() {
		//TODO
	}

	private void map() {
		//TODO
	}

	private void max() {
		//TODO
	}

	private void min() {
		//TODO
	}

	private void noneMatch() {
		assert Stream.of(emptyList).noneMatch(gt2);
		assert Stream.of(emptyList.iterator()).noneMatch(gt2);
		assert !Stream.of(1, 2, 3).noneMatch(gt2);
		assert Stream.of(0, 1).noneMatch(gt2);
		assert !Stream.of(3, 4, 5).noneMatch(gt2);
	}

	private void partitionBy() {
		//TODO
	}

	private void skip() {
		//TODO
	}

	private void sorted() {
		//TODO
	}

	private void sorted_withComparator() {
		//TODO
	}

	private void toList() {
		//TODO
	}

	private void toMap() {
		//TODO
	}

	private void toSortedMap() {
		//TODO
	}

}
