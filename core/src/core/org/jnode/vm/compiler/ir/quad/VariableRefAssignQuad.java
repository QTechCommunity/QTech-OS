/*
 * $Id$
 *
 * Copyright (C) 2020-2022 Ultreon Team
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public 
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; If not, write to the Free Software Foundation, Inc., 
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
 
package org.jnode.vm.compiler.ir.quad;

import org.jnode.vm.compiler.ir.CodeGenerator;
import org.jnode.vm.compiler.ir.ExceptionArgument;
import org.jnode.vm.compiler.ir.IRBasicBlock;
import org.jnode.vm.compiler.ir.Operand;
import org.jnode.vm.compiler.ir.Variable;

/**
 * @author Madhu Siddalingaiah
 */
public class VariableRefAssignQuad<T> extends AssignQuad<T> {
    /**
     * Right hand side of assignment
     */
    private Operand<T>[] refs;

    /**
     * @param address
     */
    public VariableRefAssignQuad(int address, IRBasicBlock<T> block, int lhsIndex, int rhsIndex) {
        super(address, block, lhsIndex);
        refs = new Operand[]{getOperand(rhsIndex)};
        getLHS().setType(refs[0].getType());
    }

    /**
     * @param lhs
     * @param rhs
     */
    public VariableRefAssignQuad(int address, IRBasicBlock<T> block, Variable<T> lhs, Variable<T> rhs) {
        super(address, block, lhs);
        refs = new Operand[]{rhs};
        getLHS().setType(refs[0].getType());
    }

    public Operand<T>[] getReferencedOps() {
        return refs;
    }

    public String toString() {
        return getAddress() + ": " + getLHS().toString() + " = " + refs[0];
    }

    /**
     * @return the RHS of the assignment
     */
    public Operand<T> getRHS() {
        return refs[0];
    }

    public Operand<T> propagate(Variable<T> operand) {
        if (!(refs[0] instanceof ExceptionArgument)) {
            boolean found = false;
            int address = getAddress();
            //check if operand is used in dominance frontier blocks possibly in a phi quad
            out:
            for(IRBasicBlock<T> bb : this.getBasicBlock().getDominanceFrontier()) {
                for (Quad quad : bb.getQuads()) {
                    if (!quad.isDeadCode()) {
                        Operand[] referencedOps = quad.getReferencedOps();
                        if (referencedOps != null) {
                            for (Operand op : referencedOps) {
                                if (op.equals(operand)) {
                                    found = true;
                                    break out;
                                }
                            }
                        }
                    }
                }
            }
            if (!found) {
                setDeadCode(true);
                return refs[0];
            } else {
                return operand;
            }
        } else {
            return operand;
        }
    }

    public void doPass2() {
        // This operation will almost always become dead code, but I wanted to play it
        // safe and compute liveness assuming it might survive.
        if (!(refs[0] instanceof ExceptionArgument))
            refs[0] = refs[0].simplify();
    }

    public void generateCode(CodeGenerator<T> cg) {
        cg.generateCodeFor(this);
    }

    public int getLHSLiveAddress() {
        return this.getAddress() + 1;
    }
}
