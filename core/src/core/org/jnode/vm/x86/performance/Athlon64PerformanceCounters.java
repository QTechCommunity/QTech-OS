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
 
package org.jnode.vm.x86.performance;

import static org.jnode.vm.performance.PresetEvent.BR_INS;
import static org.jnode.vm.performance.PresetEvent.BR_MIS;
import static org.jnode.vm.performance.PresetEvent.BR_TKN;
import static org.jnode.vm.performance.PresetEvent.BR_TKN_MIS;
import static org.jnode.vm.performance.PresetEvent.FP_INS;
import static org.jnode.vm.performance.PresetEvent.TOT_CYC;
import static org.jnode.vm.performance.PresetEvent.TOT_INS;

import org.jnode.vm.x86.VmX86Processor;

/**
 * Athlon64 specific events & MSR's for performance monitoring.
 * <p/>
 * Source: BIOS and Kernel Developer's Guide for AMD AthlonTM 64 and AMD
 * OpteronTM Processors.
 *
 * @author Ewout Prangsma (epr@users.sourceforge.net)
 */
final class Athlon64PerformanceCounters extends DualMSRPerformanceCounters {

    private static final int[] SELECT_MSR_INDEXES = {0xC0010000, 0xC0010001,
        0xC0010002, 0xC0010003};

    private static final int[] COUNT_MSR_INDEXES = {0xC0010004, 0xC0010005,
        0xC0010006, 0xC0010007};

    private static final DualMSREvent[] EVENTS = {
        new DualMSREvent(BR_INS, 0xC2, 0x00),
        new DualMSREvent(BR_MIS, 0xC3, 0x00),
        new DualMSREvent(BR_TKN, 0xC4, 0x00),
        new DualMSREvent(BR_TKN_MIS, 0xC5, 0x00),
        new DualMSREvent(FP_INS, 0xCB, 0x01),
        new DualMSREvent(TOT_CYC, 0xC1, 0x00),
        new DualMSREvent(TOT_INS, 0xC0, 0x00),
    };

    /**
     * @param cpu
     * @param selectMSRIndexes
     * @param countMSRIndexes
     * @param events
     */
    public Athlon64PerformanceCounters(VmX86Processor cpu) {
        super(cpu, SELECT_MSR_INDEXES, COUNT_MSR_INDEXES, EVENTS);
    }
}
