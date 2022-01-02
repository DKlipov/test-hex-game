package org.openjfx.utils;

import java.util.*;

public class SortedMultiset<E> extends AbstractSet<E> {

    private final TreeMap<E, Set<E>> set;

    private int size = 0;

    public SortedMultiset(Comparator<? super E> comparator) {
        this.set = new TreeMap<>(comparator);
    }

    @Override
    public Iterator<E> iterator() {
        return new MultisetIterator<>(set);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean contains(Object o) {
        var result = set.get(o);
        if (result == null) {
            return false;
        }
        return result.contains(o);
    }

    @Override
    public boolean add(E e) {
        size++;
        if (e == null) {
            throw new NullPointerException();
        }
        set.compute(e, (k, v) -> {
            if (v == null) {
                HashSet<E> set = new HashSet<>();
                set.add(e);
                return set;
            } else {
                v.add(e);
                return v;
            }
        });
        return true;
    }

    @Override
    public void clear() {
        size=0;
        set.clear();
    }

    private static class MultisetIterator<E> implements Iterator<E> {

        private final Iterator<Set<E>> set;

        private int i = 0;

        private Object[] arr = null;

        MultisetIterator(TreeMap<E, Set<E>> set) {
            this.set = set.values().iterator();
        }

        @Override
        public boolean hasNext() {
            if (arr == null || i >= arr.length) {
                return set.hasNext();
            }
            return true;
        }

        @Override
        public E next() {
            if (arr == null || i >= arr.length) {
                arr = set.next().toArray();
                i = 0;
            }
            var a = arr[i];
            i++;
            return (E) a;
        }
    }
}
