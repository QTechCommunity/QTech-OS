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
 
package com.qtech.os.test.fs.driver.context;

import org.jmock.MockObjectTestCase;
import com.qtech.os.driver.block.ramdisk.RamDiskDevice;
import com.qtech.os.driver.block.ramdisk.RamDiskDriver;
import com.qtech.os.test.fs.driver.BlockDeviceAPIContext;
import com.qtech.os.test.fs.driver.BlockDeviceAPITestConfig;
import com.qtech.os.test.fs.driver.stubs.StubDeviceManager;
import com.qtech.os.test.support.TestConfig;

public class RamDiskDriverContext extends BlockDeviceAPIContext {
    public RamDiskDriverContext() {
        super("RamDiskDriver");
    }

    public void init(TestConfig config, MockObjectTestCase testCase) throws Exception {
        super.init(config, testCase);

        BlockDeviceAPITestConfig cfg = (BlockDeviceAPITestConfig) config;
        String name = "RamDiskDevice-Tests";
        final RamDiskDevice device =
            new RamDiskDevice(StubDeviceManager.INSTANCE.getSystemBus(), name, cfg.getDeviceSize());
        final RamDiskDriver driver = new RamDiskDriver(name);
        init(null, driver, device);
    }
}
