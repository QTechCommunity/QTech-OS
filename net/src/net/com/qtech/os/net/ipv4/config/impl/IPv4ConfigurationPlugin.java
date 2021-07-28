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
 
package com.qtech.os.net.ipv4.config.impl;

import javax.naming.NamingException;

import com.qtech.os.driver.DeviceManager;
import com.qtech.os.driver.DeviceUtils;
import com.qtech.os.naming.InitialNaming;
import com.qtech.os.net.ipv4.config.IPv4ConfigurationService;
import com.qtech.os.plugin.Plugin;
import com.qtech.os.plugin.PluginDescriptor;
import com.qtech.os.plugin.PluginException;
import com.qtech.os.work.Work;
import com.qtech.os.work.WorkUtils;

/**
 * @author Ewout Prangsma (epr@users.sourceforge.net)
 */
public class IPv4ConfigurationPlugin extends Plugin {

    private NetConfigurationData config;
    private ConfigurationServiceImpl service;
    private NetDeviceMonitor monitor;
    private DeviceManager devMan;
    private ConfigurationProcessor processor;

    /**
     * @param descriptor
     */
    public IPv4ConfigurationPlugin(PluginDescriptor descriptor) {
        super(descriptor);
    }    

    /**
     * @see com.qtech.os.plugin.Plugin#startPlugin()
     */
    protected void startPlugin() throws PluginException {
        this.processor = new ConfigurationProcessor();
        this.config = new NetConfigurationData(getPreferences());
        this.service = new ConfigurationServiceImpl(processor, config);
        this.monitor = new NetDeviceMonitor(processor, config);
        try {
            InitialNaming.bind(IPv4ConfigurationService.NAME, service);
            devMan = DeviceUtils.getDeviceManager();
            devMan.addListener(monitor);
            WorkUtils.add(new Work("Network device configuration") {
                public void execute() {
                    monitor.configureDevices(devMan);
                }
            });
        } catch (NamingException ex) {
            throw new PluginException(ex);
        }
        processor.start();
    }

    /**
     * @see com.qtech.os.plugin.Plugin#stopPlugin()
     */
    protected void stopPlugin() throws PluginException {
        processor.stop();
        devMan.removeListener(monitor);
        InitialNaming.unbind(IPv4ConfigurationService.NAME);
        this.monitor = null;
        this.service = null;
        this.devMan = null;
        this.processor = null;
    }    
}
