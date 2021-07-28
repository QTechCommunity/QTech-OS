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
 
package com.qtech.os.driver.textscreen.fb;

import java.util.Collection;

import org.apache.log4j.Logger;
import com.qtech.os.driver.Device;
import com.qtech.os.driver.DeviceManagerListener;
import com.qtech.os.driver.DeviceUtils;
import com.qtech.os.driver.textscreen.TextScreenManager;
import com.qtech.os.driver.video.FrameBufferAPI;
import com.qtech.os.driver.video.FrameBufferConfiguration;
import com.qtech.os.naming.InitialNaming;

/**
 * @author Levente S\u00e1ntha
 */
class FBConsole {
    private static final Logger log = Logger.getLogger(FBConsole.class);

    public static void start() throws Exception {
        final Collection<Device> devs = DeviceUtils.getDevicesByAPI(FrameBufferAPI.class);
        int dev_count = devs.size();
        if (dev_count > 0) {
            startFBConsole(devs.iterator().next());
        } else {
            DeviceUtils.getDeviceManager().addListener(new DeviceManagerListener() {
                
                public void deviceRegistered(Device device) {
                    if (device.implementsAPI(FrameBufferAPI.class)) {
                        startFBConsole(device);
                    }
                }

                public void deviceUnregister(Device device) {
                    // TODO stop the FBConsole
                }
            });
        }
    }

    private static void startFBConsole(Device dev) {
        FbTextScreenManager fbTsMgr = null;
        try {
            final FrameBufferAPI api = dev.getAPI(FrameBufferAPI.class);
            final FrameBufferConfiguration conf = api.getConfigurations()[0];

            fbTsMgr = new FbTextScreenManager(api, conf);
            InitialNaming.unbind(TextScreenManager.NAME);
            InitialNaming.bind(TextScreenManager.NAME, fbTsMgr);
        } catch (Throwable ex) {
            log.error("Error in FBConsole", ex);
            if (fbTsMgr != null) {
                log.info("Close graphics");
                fbTsMgr.ownershipLost();
            }
        }
    }
}
