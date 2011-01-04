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
package org.sat4j.minisat.core;

import java.io.Serializable;

import org.sat4j.core.VecInt;
import org.sat4j.specs.IVecInt;

/**
 * Heap implementation used to maintain the variables order in some heuristics.
 * 
 * @author daniel
 * 
 */
public final class Heap implements Serializable {

	/*
	 * default serial version id
	 */
	private static final long serialVersionUID = 1L;

	private static final int left(int i) {
		return i << 1;
	}

	private static final int right(int i) {
		return (i << 1) ^ 1;
	}

	private static final int parent(int i) {
		return i >> 1;
	}

	private final boolean comp(int a, int b) {
		return activity[a] > activity[b];
	}

	private final IVecInt heap = new VecInt(); // heap of ints

	private final IVecInt indices = new VecInt(); // int -> index in heap

	private final double[] activity;

	final void percolateUp(int i) {
		int x = heap.get(i);
		while (parent(i) != 0 && comp(x, heap.get(parent(i)))) {
			heap.set(i, heap.get(parent(i)));
			indices.set(heap.get(i), i);
			i = parent(i);
		}
		heap.set(i, x);
		indices.set(x, i);
	}

	final void percolateDown(int i) {
		int x = heap.get(i);
		while (left(i) < heap.size()) {
			int child = right(i) < heap.size()
					&& comp(heap.get(right(i)), heap.get(left(i))) ? right(i)
					: left(i);
			if (!comp(heap.get(child), x))
				break;
			heap.set(i, heap.get(child));
			indices.set(heap.get(i), i);
			i = child;
		}
		heap.set(i, x);
		indices.set(x, i);
	}

	boolean ok(int n) {
		return n >= 0 && n < indices.size();
	}

	public Heap(double[] activity) { // NOPMD
		this.activity = activity;
		heap.push(-1);
	}

	public void setBounds(int size) {
		assert (size >= 0);
		indices.growTo(size, 0);
	}

	public boolean inHeap(int n) {
		assert (ok(n));
		return indices.get(n) != 0;
	}

	public void increase(int n) {
		assert (ok(n));
		assert (inHeap(n));
		percolateUp(indices.get(n));
	}

	public boolean empty() {
		return heap.size() == 1;
	}

	public void insert(int n) {
		assert (ok(n));
		indices.set(n, heap.size());
		heap.push(n);
		percolateUp(indices.get(n));
	}

	public int getmin() {
		int r = heap.get(1);
		heap.set(1, heap.last());
		indices.set(heap.get(1), 1);
		indices.set(r, 0);
		heap.pop();
		if (heap.size() > 1)
			percolateDown(1);
		return r;
	}

	public boolean heapProperty() {
		return heapProperty(1);
	}

	public boolean heapProperty(int i) {
		return i >= heap.size()
				|| ((parent(i) == 0 || !comp(heap.get(i), heap.get(parent(i))))
						&& heapProperty(left(i)) && heapProperty(right(i)));
	}

}
