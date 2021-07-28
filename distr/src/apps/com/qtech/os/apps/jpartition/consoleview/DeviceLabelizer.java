/*
 * $Id$
 *
 * Copyright (C) 2003-2015 QTech Community
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
 
package com.qtech.os.apps.jpartition.consoleview;

import com.qtech.os.apps.jpartition.consoleview.components.Labelizer;
import com.qtech.os.apps.jpartition.model.Device;
import com.qtech.os.util.NumberUtils;

class DeviceLabelizer implements Labelizer<Device> {
    static final DeviceLabelizer INSTANCE = new DeviceLabelizer();

    public String getLabel(Device device) {
        if (device == null) {
            throw new NullPointerException("device is null");
        }

        StringBuilder sb = new StringBuilder();

        sb.append(device.getName());
        sb.append(" (").append(NumberUtils.toBinaryByte(device.getSize())).append(')');

        return sb.toString();
    }
}
