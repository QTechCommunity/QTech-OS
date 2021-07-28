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
 
package com.qtech.os.shell.command.driver;

import java.io.PrintWriter;
import java.util.TreeMap;

import javax.naming.NameNotFoundException;

import com.qtech.os.driver.ApiNotFoundException;
import com.qtech.os.driver.Device;
import com.qtech.os.driver.DeviceAPI;
import com.qtech.os.driver.DeviceInfoAPI;
import com.qtech.os.driver.DeviceManager;
import com.qtech.os.driver.DeviceNotFoundException;
import com.qtech.os.driver.DriverException;
import com.qtech.os.naming.InitialNaming;
import com.qtech.os.shell.AbstractCommand;
import com.qtech.os.shell.syntax.Argument;
import com.qtech.os.shell.syntax.DeviceArgument;
import com.qtech.os.shell.syntax.EnumArgument;

/**
 * @author epr
 * @author crawley@jnode.org
 */
public class DeviceCommand extends AbstractCommand {
    private static enum Action {
        start, stop, restart, remove;
    }

    private static class ActionArgument extends EnumArgument<Action> {
        public ActionArgument() {
            super("action", Argument.OPTIONAL, Action.class, "action to be performed on device");
        }

        @Override
        protected String argumentKind() {
            return "{start,stop,restart,remove}";
        }
    }

    private static ActionArgument ARG_ACTION = new ActionArgument();

    private final DeviceArgument ARG_DEVICE =
        new DeviceArgument("device", Argument.OPTIONAL, "the target device");

    public DeviceCommand() {
        super("Examine or manage a device");
        registerArguments(ARG_ACTION, ARG_DEVICE);
    }

    public static void main(String[] args) throws Exception {
        new DeviceCommand().execute(args);
    }

    @Override
    public void execute() throws Exception {
        PrintWriter out = getOutput().getPrintWriter();
        PrintWriter err = getError().getPrintWriter();
        if (ARG_ACTION.isSet()) {
            if (!ARG_DEVICE.isSet()) {
                err.println("No target device specified");
                exit(1);
            }
            switch (ARG_ACTION.getValue()) {
                case restart:
                    restartDevice(ARG_DEVICE.getValue());
                    break;
                case start:
                    startDevice(ARG_DEVICE.getValue());
                    break;
                case stop:
                    stopDevice(ARG_DEVICE.getValue());
                    break;
                case remove:
                    removeDevice(ARG_DEVICE.getValue());
                    break;
            }
        } else if (ARG_DEVICE.isSet()) {
            showDevice(out, ARG_DEVICE.getValue());
        } else {
            showDevices(out);
        } 
    }

    /**
     * Stop the given device.
     * @param device
     * @throws DriverException 
     * @throws DeviceNotFoundException 
     */
    private void stopDevice(Device device) throws DeviceNotFoundException, DriverException {
        device.getManager().stop(device);
    }

    /**
     * Stop and Remove the given device.
     * @param device
     * @throws DriverException 
     * @throws DeviceNotFoundException 
     */
    private void removeDevice(Device device) throws DeviceNotFoundException, DriverException {
        device.getManager().stop(device);
        device.getManager().unregister(device);
    }

    /**
     * Start the given device
     * @param device
     * @throws DriverException 
     * @throws DeviceNotFoundException 
     */
    private void startDevice(Device device) throws DeviceNotFoundException, DriverException {
        device.getManager().start(device);
    }

    /**
     * Stop and start the given device.
     * @param device
     * @throws DriverException 
     * @throws DeviceNotFoundException 
     */
    private void restartDevice(Device device) throws DeviceNotFoundException, DriverException  {
        device.getManager().stop(device);
        device.getManager().start(device);
    }

    /**
     * Display information about a given device
     * 
     * @param out
     * @param dev
     */
    protected void showDevice(PrintWriter out, Device dev) {
        final String prefix = "    ";
        out.println("Device: " + dev.getId());
        final String drvClassName = dev.getDriverClassName();
        if (dev.isStarted()) {
            out.println(prefix + "state:started");
        } else {
            out.println(prefix + "state:stopped");
        }
        if (drvClassName != null) {
            out.println(prefix + "driver:" + drvClassName);
        } else {
            out.println(prefix + "driver:none");
        }
        out.println(prefix + "implemented API's:");
        for (Class<? extends DeviceAPI> api : dev.implementedAPIs()) {
            out.println(prefix + prefix + api.getName());
        }
        out.println();
        final PrintWriter pw = new PrintWriter(out);
        try {
            final DeviceInfoAPI infoApi = dev.getAPI(DeviceInfoAPI.class);
            if (infoApi != dev) {
                infoApi.showInfo(pw);
            }
        } catch (ApiNotFoundException ex) {
            // Ignore
        }
        if (dev instanceof DeviceInfoAPI) {
            ((DeviceInfoAPI) dev).showInfo(pw);
        }
    }

    /**
     * Display information about all devices
     * 
     * @param out
     * @throws NameNotFoundException
     */
    protected void showDevices(PrintWriter out) throws NameNotFoundException {
        // Create a sorted list
        final TreeMap<String, Device> tm = new TreeMap<String, Device>();
        final DeviceManager dm = InitialNaming.lookup(DeviceManager.NAME);

        for (Device dev : dm.getDevices()) {
            tm.put(dev.getId(), dev);
        }
        for (Device dev : tm.values()) {
            out.print(dev.getId());
            final String drvClassName = dev.getDriverClassName();
            if (dev.isStarted()) {
                out.print("\tstarted");
            } else {
                out.print("\tstopped");
            }
            if (drvClassName != null) {
                out.print("\tdriver:" + drvClassName);
            } else {
                out.print("\tdriver:none");
            }
            out.println();
        }
    }
}
