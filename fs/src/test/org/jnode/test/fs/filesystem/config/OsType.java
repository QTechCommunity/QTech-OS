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
 
package org.jnode.test.fs.filesystem.config;

import org.jnode.util.OsUtils;

public enum OsType {
    /**
     * An OS that is the UltreonOS OS
     */
    JNODE_OS("JNodeOS"),

    /**
     * An OS that is or is not the UltreonOS OS
     */
    BOTH_OS("BothOS"),

    /**
     * An OS that is not the UltreonOS OS
     */
    OTHER_OS("OtherOS");

    private OsType(String name) {
        this.name = name;
    }

    /**
     * @return
     */
    public boolean isCurrentOS() {
        boolean isCurrOS = false;

        if (this == BOTH_OS)
            isCurrOS = true;
        else if (OsUtils.isUltreonOS() && (this == JNODE_OS))
            isCurrOS = true;
        else if (!OsUtils.isUltreonOS() && (this == OTHER_OS))
            isCurrOS = true;
        else
            isCurrOS = false;

        return isCurrOS;
    }

    public String toString() {
        return name;
    }

    private String name;
}
