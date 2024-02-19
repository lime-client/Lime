package lime.core.events;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

public class LightList<E> extends AbstractList<E> {
	private Object[] theArray;

	public LightList() {
		theArray = new Object[0];
	}

	@Override
	public int size() {
		return theArray.length;
	}

	@Override
	public boolean removeIf(Predicate<? super E> predicate) {
		for (int i = 0;i < theArray.length;i++) {
			E o = (E) theArray[i];
			if (predicate.test(o)) {
				removeElement(o);
			}
		}
		return false;
	}

	@Override
	public void replaceAll(UnaryOperator<E> unaryOperator) {

	}

	@Override
	public void sort(Comparator<? super E> comparator) {
		Arrays.sort(theArray, (Comparator<Object>) comparator);
	}

	public boolean add(E handler) {
		addElement(handler);
		return true;
	}

	public LightList<E> addElement(final E handler) {
		Object[] output = new Object[theArray.length+1];
		if (theArray.length == 0) {
			output[0] = handler;
			theArray = output;
			return this;
		}
		System.arraycopy(theArray,0,output,0,theArray.length);
		output[output.length-1] = handler;
		theArray = output;
		return this;
	}

	public E get(int index) {
		return (E) theArray[index];
	}

	@Override
	public void forEach(Consumer<? super E> consumer) {
		Object[] copy = theArray.clone();
		for (int i = 0;i < copy.length;i++) {
			consumer.accept((E) copy[i]);
		}
	}

	@Override
	public Spliterator<E> spliterator() {
		return null;
	}

	@Override
	public Stream<E> stream() {
		return null;
	}

	@Override
	public Stream<E> parallelStream() {
		return null;
	}

	public void removeElement(E handler) {
		for (int i = 0;i < theArray.length;i++) {
			if (theArray[i].equals(handler)) {
				removeElement(i);
			}
		}
	}

	public void removeElement(int index) {
		if (theArray == null || index < 0 || index >= theArray.length)	return;
		Object[] copy = new Object[theArray.length - 1];
		System.arraycopy(theArray, 0, copy, 0, index);
		System.arraycopy(theArray, index + 1, copy, index, theArray.length - index - 1);
		theArray = copy;
	}


	@Override
	public int indexOf(Object o) {
		Object[] copy = theArray.clone();
		for (int i = 0;i < copy.length;i++) {
			if (copy[i].equals(o)) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public int lastIndexOf(Object o) {
		Object[] copy = theArray.clone();
		for (int i = copy.length -1 ;i >= 0 ;i--) {
			if (copy[i].equals(o)) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public ListIterator<E> listIterator() {
		return null;
	}

	@Override
	public Iterator<E> iterator() {
		return (Iterator<E>) Arrays.stream(theArray).iterator();
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}
