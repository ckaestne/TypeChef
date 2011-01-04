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

import org.sat4j.specs.IVecInt;
import org.sat4j.specs.IteratorInt;

/**
 * Utility class to allow Read Only access only to an IVecInt.
 * 
 * @author daniel
 * 
 */
public final class ReadOnlyVecInt implements IVecInt {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final IVecInt vec;

	public ReadOnlyVecInt(IVecInt vec) {
		this.vec = vec;
	}

	public void clear() {
		throw new UnsupportedOperationException();
	}

	public boolean contains(int e) {
		return vec.contains(e);
	}

	public int containsAt(int e) {
		return vec.containsAt(e);
	}

	public int containsAt(int e, int from) {
		return vec.containsAt(e, from);
	}

	public void copyTo(IVecInt copy) {
		vec.copyTo(copy);
	}

	public void copyTo(int[] is) {
		vec.copyTo(is);
	}

	public int delete(int i) {
		throw new UnsupportedOperationException();
	}

	public void ensure(int nsize) {
		throw new UnsupportedOperationException();
	}

	public int get(int i) {
		return vec.get(i);
	}

	public void growTo(int newsize, int pad) {
		throw new UnsupportedOperationException();
	}

	public void insertFirst(int elem) {
		throw new UnsupportedOperationException();
	}

	public boolean isEmpty() {
		return vec.isEmpty();
	}

	public IteratorInt iterator() {
		return vec.iterator();
	}

	public int last() {
		return vec.last();
	}

	public void moveTo(IVecInt dest) {
		throw new UnsupportedOperationException();
	}

	public void moveTo(int[] dest) {
		throw new UnsupportedOperationException();
	}

	public void moveTo(int dest, int source) {
		throw new UnsupportedOperationException();
	}

	public void moveTo2(IVecInt dest) {
		throw new UnsupportedOperationException();
	}

	public IVecInt pop() {
		throw new UnsupportedOperationException();
	}

	public IVecInt push(int elem) {
		throw new UnsupportedOperationException();
	}

	public void remove(int elem) {
		throw new UnsupportedOperationException();
	}

	public void set(int i, int o) {
		throw new UnsupportedOperationException();
	}

	public void shrink(int nofelems) {
		throw new UnsupportedOperationException();
	}

	public void shrinkTo(int newsize) {
		throw new UnsupportedOperationException();
	}

	public int size() {
		return vec.size();
	}

	public void sort() {
		throw new UnsupportedOperationException();
	}

	public void sortUnique() {
		throw new UnsupportedOperationException();
	}

	public int unsafeGet(int eleem) {
		return vec.unsafeGet(eleem);
	}

	public void unsafePush(int elem) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @since 2.1
	 */
	public int[] toArray() {
		throw new UnsupportedOperationException();
	}

	/**
	 * @since 2.2
	 */
	public int indexOf(int e) {
		return vec.indexOf(e);
	}

	@Override
	public String toString() {
		return vec.toString();
	}

	public void moveTo(int sourceStartingIndex, int[] dest) {
		throw new UnsupportedOperationException();
	}

}
