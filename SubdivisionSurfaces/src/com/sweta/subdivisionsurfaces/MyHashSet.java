package com.sweta.subdivisionsurfaces;

import java.util.HashSet;
import java.util.Iterator;

public class MyHashSet<E> extends HashSet<E> {

	private static final long serialVersionUID = 1L;

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder("MySet [");
		final Iterator<E> iterator = this.iterator();
		while (iterator.hasNext()) {
			final E e = iterator.next();
			builder.append(e.toString());
			if (iterator.hasNext()) {
				builder.append(", ");
			}
		}
		builder.append("]");
		return builder.toString();
	}

	public MyHashSet<E> union(final MyHashSet<E> s) {
		final MyHashSet<E> retVal = new MyHashSet<>();
		for (final E e : this) {
			retVal.add(e);
		}
		for (final E e : s) {
			retVal.add(e);
		}
		return retVal;
	}

	public MyHashSet<E> intersection(final MyHashSet<E> s) {
		final MyHashSet<E> retVal = new MyHashSet<>();
		for (final E e : this) {
			if (s.contains(e)) {
				retVal.add(e);
			}
		}
		for (final E e : s) {
			if (this.contains(e)) {
				retVal.add(e);
			}
		}
		return retVal;
	}

	public MyHashSet<E> complement(final MyHashSet<E> s) {
		final MyHashSet<E> retVal = new MyHashSet<>();
		for (final E e : this) {
			if (!s.contains(e)) {
				retVal.add(e);
			}
		}
		return retVal;
	}
}
