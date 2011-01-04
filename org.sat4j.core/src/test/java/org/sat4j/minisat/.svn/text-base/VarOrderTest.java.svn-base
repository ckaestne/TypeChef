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

package org.sat4j.minisat;

import junit.framework.TestCase;

import org.sat4j.minisat.constraints.ClausalDataStructureWL;
import org.sat4j.minisat.core.ILits;
import org.sat4j.minisat.core.IOrder;
import org.sat4j.minisat.orders.VarOrderHeap;

/**
 * @author leberre
 * 
 * To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class VarOrderTest extends TestCase {

    /*
     * Class to test for void newVar()
     */
    public void testNewVar() {
        int p = voc.getFromPool(-1);
        order.init();
        assertEquals(p, order.select());
        voc.satisfies(2); // satisfying literal 1
        assertEquals(ILits.UNDEFINED, order.select());
    }

    /*
     * Class to test for void newVar(int)
     */
    public void testNewVarint() {
    }

    public void testSelect() {
    }

    public void testSetVarDecay() {
    }

    public void testUndo() {
    }

    public void testUpdateVar() {
    }

    public void testVarDecayActivity() {
    }

    public void testNumberOfInterestingVariables() {
    }

    public void testGetVocabulary() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        voc = new ClausalDataStructureWL().getVocabulary();
        voc.ensurePool(5);
        order = new VarOrderHeap();
        order.setLits(voc);
    }

    private ILits voc;

    private IOrder order;
}
