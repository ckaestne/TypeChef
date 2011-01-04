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
package org.sat4j.specs;

import java.io.Serializable;

/**
 * An abstraction for the vector of int used on the library.
 * 
 * @author leberre
 */
public interface IVecInt extends Serializable {

	public abstract int size();

	/**
	 * Remove the latest nofelems elements from the vector
	 * 
	 * @param nofelems
	 */
	public abstract void shrink(int nofelems);

	public abstract void shrinkTo(int newsize);

	/**
	 * depile le dernier element du vecteur. Si le vecteur est vide, ne fait
	 * rien.
	 */
	public abstract IVecInt pop();

	public abstract void growTo(int newsize, final int pad);

	public abstract void ensure(int nsize);

	public abstract IVecInt push(int elem);

	/**
	 * Push the element in the Vector without verifying if there is room for it.
	 * USE WITH CAUTION!
	 * 
	 * @param elem
	 */
	void unsafePush(int elem);

	int unsafeGet(int eleem);

	public abstract void clear();

	public abstract int last();

	public abstract int get(int i);

	public abstract void set(int i, int o);

	public abstract boolean contains(int e);

	/**
	 * @since 2.2
	 * @param e
	 * @return
	 */
	public abstract int indexOf(int e);

	/**
	 * returns the index of the first occurrence of e, else -1.
	 * 
	 * @param e
	 *            an integer
	 * @return the index i such that get(i)==e, else -1.
	 */
	public abstract int containsAt(int e);

	/**
	 * returns the index of the first occurence of e occurring after from
	 * (excluded), else -1.
	 * 
	 * @param e
	 *            an integer
	 * @param from
	 *            the index to start from (excluded).
	 * @return the index i such that i>from and get(i)==e, else -1
	 */
	public abstract int containsAt(int e, int from);

	/**
	 * C'est operations devraient se faire en temps constant. Ce n'est pas le
	 * cas ici.
	 * 
	 * @param copy
	 */
	public abstract void copyTo(IVecInt copy);

	/**
	 * @param is
	 */
	public abstract void copyTo(int[] is);

	/*
	 * Copie un vecteur dans un autre (en vidant le premier), en temps constant.
	 */
	public abstract void moveTo(IVecInt dest);

	public abstract void moveTo(int sourceStartingIndex, int[] dest);

	public abstract void moveTo2(IVecInt dest);

	public abstract void moveTo(int[] dest);

	/**
	 * Move elements inside the vector. The content of the method is equivalent
	 * to: <code>vec[dest] = vec[source]</code>
	 * 
	 * @param dest
	 *            the index of the destination
	 * @param source
	 *            the index of the source
	 */
	void moveTo(int dest, int source);

	/**
	 * Insert an element at the very begining of the vector. The former first
	 * element is appended to the end of the vector in order to have a constant
	 * time operation.
	 * 
	 * @param elem
	 *            the element to put first in the vector.
	 */
	public abstract void insertFirst(final int elem);

	/**
	 * Enleve un element qui se trouve dans le vecteur!!!
	 * 
	 * @param elem
	 *            un element du vecteur
	 */
	public abstract void remove(int elem);

	/**
	 * Delete the ith element of the vector. The latest element of the vector
	 * replaces the removed element at the ith indexer.
	 * 
	 * @param i
	 *            the indexer of the element in the vector
	 * @return the former ith element of the vector that is now removed from the
	 *         vector
	 */
	public abstract int delete(int i);

	public abstract void sort();

	public abstract void sortUnique();

	/**
	 * To know if a vector is empty
	 * 
	 * @return true iff the vector is empty.
	 * @since 1.6
	 */
	boolean isEmpty();

	IteratorInt iterator();

	/**
	 * Allow to access the internal representation of the vector as an array.
	 * Note that only the content of index 0 to size() should be taken into
	 * account. USE WITH CAUTION
	 * 
	 * @return the internal representation of the Vector as an array.
	 * @since 2.1
	 */
	int[] toArray();
}
