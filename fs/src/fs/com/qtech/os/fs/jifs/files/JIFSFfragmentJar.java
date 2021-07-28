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
 
package com.qtech.os.fs.jifs.files;

import com.qtech.os.fs.FSDirectory;
import com.qtech.os.plugin.PluginManager;
import com.qtech.os.plugin.model.PluginDescriptorModel;
import com.qtech.os.naming.InitialNaming;

import javax.naming.NameNotFoundException;

/**
 * File, which contains information about the plugin with the same name.
 * 
 * @author Levente S\u00e1ntha
 */
public class JIFSFfragmentJar extends JIFSFpluginJar {
    private String pluginId;
    private String fragmentId;

    public JIFSFfragmentJar() {
        return;
    }

    public JIFSFfragmentJar(String pluginId, String fragmentId, FSDirectory parent) {
        super(fragmentId, parent);
        this.pluginId = pluginId;
        this.fragmentId = fragmentId;
        refresh();
    }

    public void refresh() {
        try {
            final PluginManager mgr = InitialNaming.lookup(PluginManager.NAME);
            PluginDescriptorModel pdm =
                    (PluginDescriptorModel) mgr.getRegistry().getPluginDescriptor(pluginId);
            if (pdm != null) {
                isvalid = false;
                for (PluginDescriptorModel fdm : pdm.fragments()) {
                    if (fdm.getId().equals(fragmentId)) {
                        buffer = fdm.getJarFile().getBuffer();
                        isvalid = buffer != null;
                    }
                }
            } else {
                isvalid = false;
            }
        } catch (NameNotFoundException e) {
            System.err.println(e);
        }
    }
}
