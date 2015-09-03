package com.github.stream4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TestStream {
	private static final List<Integer> emptyList = Collections.<Integer> emptyList();
	private static final Predicate<Integer> gt2 = new Predicate<Integer>() {

		@Override
		public boolean test(Integer t) {
			return t > 2;
		}
	};

	private static final Comparator<Integer> intComparator = new Comparator<Integer>() {

		@Override
		public int compare(Integer o1, Integer o2) {
			return o1 - o2;
		}
	};

	public void testAll() {
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
		assert Stream.of(emptyList).findAny() == null;
		assert Stream.of(1, 2, 3).findAny() == 1;
	}

	private void findFirst() {
		assert Stream.of(emptyList).findFirst() == null;
		assert Stream.of(1, 2, 3).findFirst() == 1;
	}

	private static class Employee {
		private final Set<String> roles;

		public Employee(String... roles) {
			this.roles = new HashSet<String>(Arrays.asList(roles));
		}

		public Set<String> getRoles() {
			return roles;
		}
	}

	/**
	 * Returns a stream representing the roles of the employees, or null if the employee has no roles.
	 */
	private static final Function<Employee, Stream<String>> getRoles = new Function<Employee, Stream<String>>() {

		@Override
		public Stream<String> apply(Employee t) {
			final Set<String> roles = t.getRoles();
			return roles.size() == 0 ? null : Stream.of(roles);
		}
	};

	private void flatMap() {
		final List<Employee> noEmployees = Collections.<Employee> emptyList();
		final List<String> noRoles = Collections.<String> emptyList();
		assert Stream.of(noEmployees).flatMap(getRoles).toList().equals(noRoles);

		final List<Employee> employees = new ArrayList<Employee>();
		employees.add(new Employee("role1", "role2"));
		employees.add(new Employee());
		employees.add(new Employee());
		employees.add(new Employee());
		employees.add(new Employee("role3"));
		employees.add(new Employee());
		final Set<String> actual = new HashSet<String>(Stream.of(employees).flatMap(getRoles).toList());
		final HashSet<String> expected = new HashSet<String>(Arrays.asList("role1", "role2", "role3"));
		assert actual.equals(expected);
	}

	private static class Add extends Consumer<Integer> {
		private final List<Integer> list;

		public Add(List<Integer> list) {
			this.list = list;
		}

		@Override
		public void accept(Integer t) {
			list.add(t);
		}

	}

	private void forEach() {
		{
			final List<Integer> l = new ArrayList<Integer>();
			final Add add = new Add(l);
			Stream.of(emptyList).forEach(add);
			assert l.equals(emptyList);
		}
		{
			final List<Integer> l = new ArrayList<Integer>();
			final Add add = new Add(l);
			final List<Integer> expected = Arrays.asList(1, 2, 3);
			Stream.of(expected).forEach(add);
			assert l.equals(expected);
		}
	}

	private static final Function<String, Character> firstChar = new Function<String, Character>() {

		@Override
		public Character apply(String t) {
			return t.charAt(0);
		}

	};

	private void groupBy() {
		{
			final Map<Character, List<String>> actual = Stream.of(Collections.<String> emptyList())
					.groupBy(firstChar);
			assert actual.equals(Collections.<String, Character> emptyMap());
		}
		{
			final Map<Character, List<String>> actual = Stream.of("ab", "ac", "bb").groupBy(firstChar);
			final Map<Character, List<String>> expected = new HashMap<Character, List<String>>();
			expected.put('a', Arrays.asList("ab", "ac"));
			expected.put('b', Arrays.asList("bb"));
			assert actual.equals(expected);
		}
	}

	private void limit() {
		assert Stream.of(emptyList).limit(0).toList().equals(emptyList);
		assert Stream.of(emptyList).limit(3).toList().equals(emptyList);
		assert Stream.of(1, 2, 3).limit(0).toList().equals(emptyList);
		assert Stream.of(1, 2, 3).limit(1).toList().equals(Arrays.asList(1));
		assert Stream.of(1, 2, 3).limit(2).toList().equals(Arrays.asList(1, 2));
		assert Stream.of(1, 2, 3).limit(3).toList().equals(Arrays.asList(1, 2, 3));
		assert Stream.of(1, 2, 3).limit(4).toList().equals(Arrays.asList(1, 2, 3));
	}

	private static <T> Function<T, String> toStr() {
		return new Function<T, String>() {

			@Override
			public String apply(T t) {
				return t.toString();
			}
		};
	}

	private void map() {
		assert Stream.of(emptyList).map(toStr()).toList().equals(emptyList);
		assert Stream.of(1, 2, 3).map(toStr()).toList().equals(Arrays.asList("1", "2", "3"));
	}

	private void max() {
		assert Stream.of(emptyList).max() == null;
		assert Stream.of(emptyList).max(intComparator) == null;
		
		assert Stream.of(1).max() == 1;
		assert Stream.of(1).max(intComparator) == 1;
		
		assert Stream.of(1,2,3).max() == 3;
		assert Stream.of(1,2,3).max(intComparator) == 3;
		
		assert Stream.of(1,3,2).max() == 3;
		assert Stream.of(1,3,2).max(intComparator) == 3;
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
