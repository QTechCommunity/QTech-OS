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
 
package org.jnode.vm.x86.compiler.stub;

import org.jnode.vm.classmgr.VmCompiledCode;
import org.jnode.vm.compiler.GCMapIterator;
import org.vmmagic.unboxed.Address;
import org.vmmagic.unboxed.Offset;

/**
 * @author Ewout Prangsma (epr@users.sourceforge.net)
 */
final class EmptyGCMapIterator extends GCMapIterator {

    /**
     * @see org.jnode.vm.compiler.GCMapIterator#getNextReferenceAddress()
     */
    @Override
    public Address getNextReferenceAddress() {
        return Address.zero();
    }

    /**
     * @see org.jnode.vm.compiler.GCMapIterator#iterationComplete()
     */
    @Override
    public void iterationComplete() {
        // Do nothing
    }

    /**
     * @see org.jnode.vm.compiler.GCMapIterator#setupIteration(org.jnode.vm.classmgr.VmCompiledCode,
     * org.vmmagic.unboxed.Offset, org.vmmagic.unboxed.Address)
     */
    @Override
    public void setupIteration(VmCompiledCode method, Offset instructionOffset, Address framePtr) {
        // Do nothing
    }
}
