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
import com.qtech.os.driver.Device;
import com.qtech.os.driver.block.ide.disk.IDEDiskDriver;
import com.qtech.os.driver.bus.ide.IDEDevice;
import com.qtech.os.driver.bus.ide.IDEDriverUtils;
import com.qtech.os.naming.InitialNaming;
import com.qtech.os.system.resource.ResourceManager;
import com.qtech.os.test.fs.driver.BlockDeviceAPIContext;
import com.qtech.os.test.fs.driver.BlockDeviceAPITestConfig;
import com.qtech.os.test.fs.driver.factories.MockIDEDeviceFactory;
import com.qtech.os.test.support.MockObjectFactory;
import com.qtech.os.test.support.TestConfig;

public class IDEDiskDriverContext extends BlockDeviceAPIContext {
    public IDEDiskDriverContext() {
        super("IDEDiskDriver");
    }

    public void init(TestConfig config, MockObjectTestCase testCase) throws Exception {
        super.init(config, testCase);

        IDEDiskDriver driver = new IDEDiskDriver();

        // set the current testCase for our factory
        MockIDEDeviceFactory factory = (MockIDEDeviceFactory)
            IDEDriverUtils.getIDEDeviceFactory();
        factory.setTestCase(testCase);

        // create stub resource manager
        MockObjectFactory.createResourceManager(testCase);

        // create stub IDE device
        Device parent = MockObjectFactory.createParentDevice();
        BlockDeviceAPITestConfig cfg = (BlockDeviceAPITestConfig) config;
        IDEDevice device = MockObjectFactory.createIDEDevice(parent, testCase, true, cfg.getDeviceSize());

        init(null, driver, device);
    }

    protected void destroyImpl() {
        InitialNaming.unbind(ResourceManager.NAME);
    }
}
