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
 
package com.qtech.os.driver.net.via_rhine;

import java.security.PrivilegedExceptionAction;
import com.qtech.os.driver.Device;
import com.qtech.os.driver.DriverException;
import com.qtech.os.driver.net.ethernet.spi.BasicEthernetDriver;
import com.qtech.os.driver.net.ethernet.spi.Flags;
import com.qtech.os.plugin.ConfigurationElement;
import com.qtech.os.system.resource.ResourceNotFreeException;
import com.qtech.os.util.AccessControllerUtils;

/**
 * @author Levente S\u00e1ntha
 */
public class ViaRhineDriver extends BasicEthernetDriver {
    /**
     * Create a new instance
     *
     * @param config configuartion desc
     */
    public ViaRhineDriver(ConfigurationElement config) {
        this(new ViaRhineFlags(config));
    }

    public ViaRhineDriver(ViaRhineFlags flags) {
        this.flags = flags;
    }

    /**
     * Create a new ViaRhineCore instance
     */
    protected ViaRhineCore newCore(final Device device, final Flags flags)
        throws DriverException, ResourceNotFreeException {
        try {
            return AccessControllerUtils.doPrivileged(new PrivilegedExceptionAction<ViaRhineCore>() {
                public ViaRhineCore run() throws Exception {
                    return new ViaRhineCore(ViaRhineDriver.this, device, device, flags);
                }
            });
        } catch (DriverException ex) {
            throw ex;
        } catch (ResourceNotFreeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new DriverException(ex);
        }
    }
}
