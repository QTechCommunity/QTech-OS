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
 
package com.qtech.os.test.shell;

import javax.naming.NamingException;

import org.apache.log4j.BasicConfigurator;
import com.qtech.os.emu.naming.BasicNameSpace;
import com.qtech.os.emu.plugin.model.DummyExtensionPoint;
import com.qtech.os.naming.InitialNaming;
import com.qtech.os.shell.ShellManager;
import com.qtech.os.shell.alias.AliasManager;
import com.qtech.os.shell.alias.def.DefaultAliasManager;
import com.qtech.os.shell.def.DefaultShellManager;
import com.qtech.os.shell.help.HelpFactory;
import com.qtech.os.shell.help.def.DefaultHelpFactory;

/**
 * This class assembles a minimal set of JNode services to support unit
 * testing some shell and syntax infrastructure.
 * <p>
 * A Cassowary is another large flightless bird ... like an Emu but different.
 *
 * @author crawley@jnode.org
 */
public class Cassowary {
    private static boolean initialized;

    public static void initEnv() throws NamingException {
        if (initialized) {
            return;
        }
        InitialNaming.setNameSpace(new BasicNameSpace());
        InitialNaming.bind(DeviceManager.NAME, DeviceManager.INSTANCE);
        AliasManager alias_mgr =
            new DefaultAliasManager(new DummyExtensionPoint()).createAliasManager();
        InitialNaming.bind(AliasManager.NAME, alias_mgr);
        InitialNaming.bind(ShellManager.NAME, new DefaultShellManager());
        InitialNaming.bind(HelpFactory.NAME, new DefaultHelpFactory());

        BasicConfigurator.configure();
        initialized = true;
    }
}
