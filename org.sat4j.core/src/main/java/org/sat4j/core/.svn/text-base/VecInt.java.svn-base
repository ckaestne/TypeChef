/*******************************************************************************
 * SAT4J: a SATisfiability library for Java Copyright (C) 2004-2008 Daniel Le Berre
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU Lesser General Public License Version 2.1 or later (the
 * "LGPL"), in which case the provisions of the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of the LGPL, and not to allow others to use your version of
 * this file under the terms of the EPL, indicate your decision by deleting
 * the provisions above and replace them with the notice and other provisions
 * required by the LGPL. If you do not delete the provisions above, a recipient
 * may use your version of this file under the terms of the EPL or the LGPL.
 * 
 * Based on the original MiniSat specification from:
 * 
 * An extensible SAT solver. Niklas Een and Niklas Sorensson. Proceedings of the
 * Sixth International Conference on Theory and Applications of Satisfiability
 * Testing, LNCS 2919, pp 502-518, 2003.
 *
 * See www.minisat.se for the original solver in C++.
 * 
 *******************************************************************************/
package org.sat4j.core;

import java.util.NoSuchElementException;

import org.sat4j.specs.IVecInt;
import org.sat4j.specs.IteratorInt;

/*
 * Created on 9 oct. 2003
 */

/**
 * A vector specific for primitive integers, widely used in the solver. Note
 * that if the vector has a sort method, the operations on the vector DO NOT
 * preserve sorting.
 * 
 * @author leberre
 */
public final class VecInt implements IVecInt {
	// MiniSat -- Copyright (c) 2003-2005, Niklas Een, Niklas Sorensson
	//
	// Permission is hereby granted, free of charge, to any person obtaining a
	// copy of this software and associated documentation files (the
	// "Software"), to deal in the Software without restriction, including
	// without limitation the rights to use, copy, modify, merge, publish,
	// distribute, sublicense, and/or sell copies of the Software, and to
	// permit persons to whom the Software is furnished to do so, subject to
	// the following conditions:
	//
	// The above copyright notice and this permission notice shall be included
	// in all copies or substantial portions of the Software.
	//
	// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
	// OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
	// MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
	// NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
	// LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
	// OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
	// WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

	private static final long serialVersionUID = 1L;

	public static final IVecInt EMPTY = new IVecInt() {

		/**
         * 
         */
		private static final long serialVersionUID = 1L;

		public int size() {
			return 0;
		}

		public void shrink(int nofelems) {
		}

		public void shrinkTo(int newsize) {
		}

		public IVecInt pop() {
			throw new UnsupportedOperationException();
		}

		public void growTo(int newsize, int pad) {
		}

		public void ensure(int nsize) {
		}

		public IVecInt push(int elem) {
			throw new UnsupportedOperationException();
		}

		public void unsafePush(int elem) {
			throw new UnsupportedOperationException();
		}

		public void clear() {
		}

		public int last() {
			throw new UnsupportedOperationException();
		}

		public int get(int i) {
			throw new UnsupportedOperationException();
		}

		public void set(int i, int o) {
			throw new UnsupportedOperationException();
		}

		public boolean contains(int e) {
			return false;
		}

		public void copyTo(IVecInt copy) {
		}

		public void copyTo(int[] is) {
		}

		public void moveTo(IVecInt dest) {
		}

		public void moveTo2(IVecInt dest) {
		}

		public void moveTo(int[] dest) {
		}

		public void insertFirst(int elem) {
			throw new UnsupportedOperationException();
		}

		public void remove(int elem) {
			throw new UnsupportedOperationException();
		}

		public int delete(int i) {
			throw new UnsupportedOperationException();
		}

		public void sort() {
		}

		public void sortUnique() {
		}

		public int unsafeGet(int eleem) {
			throw new UnsupportedOperationException();
		}

		public int containsAt(int e) {
			throw new UnsupportedOperationException();
		}

		public int containsAt(int e, int from) {
			throw new UnsupportedOperationException();
		}

		public void moveTo(int dest, int source) {
			throw new UnsupportedOperationException();
		}

		public boolean isEmpty() {
			return true;
		}

		public IteratorInt iterator() {
			return new IteratorInt() {

				public boolean hasNext() {
					return false;
				}

				public int next() {
					throw new UnsupportedOperationException();
				}
			};
		}

		public int[] toArray() {
			throw new UnsupportedOperationException();
		}

		public int indexOf(int e) {
			return -1;
		}

		@Override
		public String toString() {
			return "[]";
		}

		public void moveTo(int sourceStartingIndex, int[] dest) {
			throw new UnsupportedOperationException();
		}

	};

	public VecInt() {
		this(5);
	}

	public VecInt(int size) {
		myarray = new int[size];
	}

	/**
	 * Adapter method to translate an array of int into an IVecInt.
	 * 
	 * The array is used inside the VecInt, so the elements may be modified
	 * outside the VecInt. But it should not take much memory.The size of the
	 * created VecInt is the length of the array.
	 * 
	 * @param lits
	 *            a filled array of int.
	 */
	public VecInt(int[] lits) { // NOPMD
		myarray = lits;
		nbelem = lits.length;
	}

	/**
	 * Build a vector of a given initial size filled with an integer.
	 * 
	 * @param size
	 *            the initial size of the vector
	 * @param pad
	 *            the integer to fill the vector with
	 */
	public VecInt(int size, int pad) {
		myarray = new int[size];
		for (int i = 0; i < size; i++) {
			myarray[i] = pad;
		}
		nbelem = size;
	}

	public int size() {
		return nbelem;
	}

	/**
	 * Remove the latest nofelems elements from the vector
	 * 
	 * @param nofelems
	 */
	public void shrink(int nofelems) {
		// assert nofelems >= 0;
		// assert nofelems <= size();
		nbelem -= nofelems;
	}

	public void shrinkTo(int newsize) {
		// assert newsize >= 0;
		// assert newsize < nbelem;
		nbelem = newsize;
	}

	/**
	 * depile le dernier element du vecteur. Si le vecteur est vide, ne fait
	 * rien.
	 */
	public IVecInt pop() {
		// assert size() != 0;
		--nbelem;
		return this;
	}

	public void growTo(int newsize, final int pad) {
		// assert newsize > size();
		ensure(newsize);
		while (--newsize >= 0) {
			myarray[nbelem++] = pad;
		}
	}

	public void ensure(int nsize) {
		if (nsize >= myarray.length) {
			int[] narray = new int[Math.max(nsize, nbelem * 2)];
			System.arraycopy(myarray, 0, narray, 0, nbelem);
			myarray = narray;
		}
	}

	public IVecInt push(int elem) {
		ensure(nbelem + 1);
		myarray[nbelem++] = elem;
		return this;
	}

	public void unsafePush(int elem) {
		myarray[nbelem++] = elem;
	}

	public void clear() {
		nbelem = 0;
	}

	public int last() {
		// assert nbelem > 0;
		return myarray[nbelem - 1];
	}

	public int get(int i) {
		// assert i >= 0 && i < nbelem;
		return myarray[i];
	}

	public int unsafeGet(int i) {
		return myarray[i];
	}

	public void set(int i, int o) {
		assert i >= 0 && i < nbelem;
		myarray[i] = o;
	}

	public boolean contains(int e) {
		final int[] workArray = myarray; // dvh, faster access
		for (int i = 0; i < nbelem; i++) {
			if (workArray[i] == e)
				return true;
		}
		return false;
	}

	/**
	 * @since 2.2
	 */
	public int indexOf(int e) {
		final int[] workArray = myarray; // dvh, faster access
		for (int i = 0; i < nbelem; i++) {
			if (workArray[i] == e)
				return i;
		}
		return -1;
	}

	public int containsAt(int e) {
		return containsAt(e, -1);
	}

	public int containsAt(int e, int from) {
		final int[] workArray = myarray; // dvh, faster access
		for (int i = from + 1; i < nbelem; i++) {
			if (workArray[i] == e)
				return i;
		}
		return -1;
	}

	/**
	 * Copy the content of this vector into another one. Non constant time
	 * operation.
	 * 
	 * @param copy
	 */
	public void copyTo(IVecInt copy) {
		VecInt ncopy = (VecInt) copy;
		int nsize = nbelem + ncopy.nbelem;
		ncopy.ensure(nsize);
		System.arraycopy(myarray, 0, ncopy.myarray, ncopy.nbelem, nbelem);
		ncopy.nbelem = nsize;
	}

	/**
	 * Copy the content of this vector into an array of integer. Non constant
	 * time operation.
	 * 
	 * @param is
	 */
	public void copyTo(int[] is) {
		// assert is.length >= nbelem;
		System.arraycopy(myarray, 0, is, 0, nbelem);
	}

	public void moveTo(IVecInt dest) {
		copyTo(dest);
		nbelem = 0;
	}

	public void moveTo2(IVecInt dest) {
		VecInt ndest = (VecInt) dest;
		int s = ndest.nbelem;
		int tmp[] = ndest.myarray;
		ndest.myarray = myarray;
		ndest.nbelem = nbelem;
		myarray = tmp;
		nbelem = s;
		nbelem = 0;
	}

	public void moveTo(int dest, int source) {
		myarray[dest] = myarray[source];
	}

	public void moveTo(int[] dest) {
		System.arraycopy(myarray, 0, dest, 0, nbelem);
		nbelem = 0;
	}

	public void moveTo(int sourceStartingIndex, int[] dest) {
		System.arraycopy(myarray, sourceStartingIndex, dest, 0, nbelem
				- sourceStartingIndex);
		nbelem = 0;
	}

	/**
	 * Insert an element at the very begining of the vector. The former first
	 * element is appended to the end of the vector in order to have a constant
	 * time operation.
	 * 
	 * @param elem
	 *            the element to put first in the vector.
	 */
	public void insertFirst(final int elem) {
		if (nbelem > 0) {
			push(myarray[0]);
			myarray[0] = elem;
			return;
		}
		push(elem);
	}

	/**
	 * Enleve un element qui se trouve dans le vecteur!!!
	 * 
	 * @param elem
	 *            un element du vecteur
	 */
	public void remove(int elem) {
		// assert size() > 0;
		int j = 0;
		for (; myarray[j] != elem; j++) {
			assert j < size();
		}
		System.arraycopy(myarray, j + 1, myarray, j, size() - j);
		pop();
	}

	/**
	 * Delete the ith element of the vector. The latest element of the vector
	 * replaces the removed element at the ith indexer.
	 * 
	 * @param i
	 *            the indexer of the element in the vector
	 * @return the former ith element of the vector that is now removed from the
	 *         vector
	 */
	public int delete(int i) {
		// assert i >= 0 && i < nbelem;
		int ith = myarray[i];
		myarray[i] = myarray[--nbelem];
		return ith;
	}

	private int nbelem;

	private int[] myarray;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.int#toString()
	 */
	@Override
	public String toString() {
		StringBuffer stb = new StringBuffer();
		for (int i = 0; i < nbelem - 1; i++) {
			stb.append(myarray[i]);
			stb.append(","); //$NON-NLS-1$
		}
		if (nbelem > 0) {
			stb.append(myarray[nbelem - 1]);
		}
		return stb.toString();
	}

	void selectionSort(int from, int to) {
		int i, j, best_i;
		int tmp;

		for (i = from; i < to - 1; i++) {
			best_i = i;
			for (j = i + 1; j < to; j++) {
				if (myarray[j] < myarray[best_i])
					best_i = j;
			}
			tmp = myarray[i];
			myarray[i] = myarray[best_i];
			myarray[best_i] = tmp;
		}
	}

	void sort(int from, int to) {
		int width = to - from;
		if (width <= 15)
			selectionSort(from, to);

		else {
			final int[] locarray = myarray;
			int pivot = locarray[width / 2 + from];
			int tmp;
			int i = from - 1;
			int j = to;

			for (;;) {
				do
					i++;
				while (locarray[i] < pivot);
				do
					j--;
				while (pivot < locarray[j]);

				if (i >= j)
					break;

				tmp = locarray[i];
				locarray[i] = locarray[j];
				locarray[j] = tmp;
			}

			sort(from, i);
			sort(i, to);
		}
	}

	/**
	 * sort the vector using a custom quicksort.
	 */
	public void sort() {
		sort(0, nbelem);
	}

	public void sortUnique() {
		int i, j;
		int last;
		if (nbelem == 0)
			return;

		sort(0, nbelem);
		i = 1;
		int[] locarray = myarray;
		last = locarray[0];
		for (j = 1; j < nbelem; j++) {
			if (last < locarray[j]) {
				last = locarray[i] = locarray[j];
				i++;
			}
		}

		nbelem = i;
	}

	/**
	 * Two vectors are equals iff they have the very same elements in the order.
	 * 
	 * @param obj
	 *            an object
	 * @return true iff obj is a VecInt and has the same elements as this vector
	 *         at each index.
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof VecInt) {
			VecInt v = (VecInt) obj;
			if (v.nbelem != nbelem)
				return false;
			for (int i = 0; i < nbelem; i++) {
				if (v.myarray[i] != myarray[i]) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		long sum = 0;
		for (int i = 0; i < nbelem; i++) {
			sum += myarray[i];
		}
		return (int) sum / nbelem;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sat4j.specs.IVecInt2#pushAll(org.sat4j.specs.IVecInt2)
	 */
	public void pushAll(IVecInt vec) {
		VecInt nvec = (VecInt) vec;
		int nsize = nbelem + nvec.nbelem;
		ensure(nsize);
		System.arraycopy(nvec.myarray, 0, myarray, nbelem, nvec.nbelem);
		nbelem = nsize;
	}

	/**
	 * to detect that the vector is a subset of another one. Note that the
	 * method assumes that the two vectors are sorted!
	 * 
	 * @param vec
	 *            a vector
	 * @return true iff the current vector is a subset of vec
	 */
	public boolean isSubsetOf(VecInt vec) {
		int i = 0;
		int j = 0;
		while ((i < this.nbelem) && (j < vec.nbelem)) {
			while ((j < vec.nbelem) && (vec.myarray[j] < this.myarray[i])) {
				j++;
			}
			if (j == vec.nbelem || this.myarray[i] != vec.myarray[j])
				return false;
			i++;
		}
		return true;
	}

	public IteratorInt iterator() {
		return new IteratorInt() {
			private int i = 0;

			public boolean hasNext() {
				return i < nbelem;
			}

			public int next() {
				if (i == nbelem)
					throw new NoSuchElementException();
				return myarray[i++];
			}
		};
	}

	public boolean isEmpty() {
		return nbelem == 0;
	}

	/**
	 * @since 2.1
	 */
	public int[] toArray() {
		return myarray;
	}
}
