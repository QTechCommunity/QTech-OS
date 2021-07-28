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
 
package com.qtech.os.test.fs;

import java.io.IOException;
import java.nio.ByteBuffer;
import javax.naming.NameNotFoundException;
import org.apache.log4j.Logger;
import com.qtech.os.driver.ApiNotFoundException;
import com.qtech.os.driver.Device;
import com.qtech.os.driver.DeviceManager;
import com.qtech.os.driver.DeviceNotFoundException;
import com.qtech.os.driver.block.BlockDeviceAPI;
import com.qtech.os.naming.InitialNaming;

/**
 * @author epr
 */
public class FloppyTest {

    /**
     * My logger
     */
    private static final Logger log = Logger.getLogger(FloppyTest.class);

    public static void main(String[] args) {

        try {
            final DeviceManager dm = InitialNaming.lookup(DeviceManager.NAME);
            final Device fd0 = dm.getDevice("fd0");
            final BlockDeviceAPI api = fd0.getAPI(BlockDeviceAPI.class);
            try {

                final ByteBuffer buf = ByteBuffer.allocate(512);
                api.read(0, buf);
            } catch (IOException ex) {
                log.error("Oops", ex);
            }
        } catch (ApiNotFoundException ex) {
            log.error("BlockDeviceAPI not found", ex);
        } catch (DeviceNotFoundException ex) {
            log.error("fd0 device not found", ex);
        } catch (NameNotFoundException ex) {
            log.error("device manager not found", ex);
        }
    }

}
