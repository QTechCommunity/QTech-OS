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
 
package com.qtech.os.shell.command.driver.system.bus;

import java.io.IOException;
import java.io.PrintWriter;

import javax.naming.NameNotFoundException;

import com.qtech.os.driver.bus.smbus.SMBusControler;
import com.qtech.os.naming.InitialNaming;
import com.qtech.os.shell.AbstractCommand;

/**
 * @author Francois-Frederic Ozog
 */
public class SMBusCommand extends AbstractCommand {
    
    public SMBusCommand() {
        super("Dump information about the SMBus");
    }

    public static void main(String[] args) throws Exception {
        new SMBusCommand().execute(args);
    }
    
    @Override
    public void execute() {
        SMBusControler smbusctrl = null;
        PrintWriter out = getOutput().getPrintWriter();
        try {
            smbusctrl = InitialNaming.lookup(SMBusControler.NAME);
        } catch (NameNotFoundException ex) {
            out.println("Could not find the SMBusControler: " + ex.getMessage());
            exit(1);
        }

        for (byte i = 0; i < 8; i++) {
            try {
                // TODO - can someone explain why we are shifting and ORing?  It looks like a bug to me.
                byte res = smbusctrl.readByte((byte) (0xa0 | (i << 1)), (byte) 2);
                out.println("DIMM " + i + " : type = " + Integer.toHexString(res));
            } catch (IOException ex) {
                out.println("DIMM " + i + " : not present");
            }
        }
    }
}

