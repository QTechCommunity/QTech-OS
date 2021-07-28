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
 
package com.qtech.os.fs.jifs.directories;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.naming.NameNotFoundException;

import com.qtech.os.fs.FSDirectory;
import com.qtech.os.fs.FSEntry;
import com.qtech.os.fs.jifs.JIFSDirectory;
import com.qtech.os.fs.jifs.files.JIFSFfragmentJar;
import com.qtech.os.fs.jifs.files.JIFSFpluginJar;
import com.qtech.os.naming.InitialNaming;
import com.qtech.os.plugin.PluginDescriptor;
import com.qtech.os.plugin.PluginManager;
import com.qtech.os.plugin.model.PluginDescriptorModel;

/**
 * @author Levente S\u00e1ntha
 */
public class JIFSDpluginJars extends JIFSDirectory {

    public JIFSDpluginJars() throws IOException {
        super("lib");
        refresh();
    }

    public JIFSDpluginJars(FSDirectory parent) throws IOException {
        this();
        setParent(parent);
    }

    public void refresh() {
        // this has to be improved
        // just add new ones and delete old ones
        // now it does delete all files and (re)create all ones
        super.clear();
        final ArrayList<String> rows = new ArrayList<String>();
        try {
            final PluginManager mgr = InitialNaming.lookup(PluginManager.NAME);
            for (PluginDescriptor descr : mgr.getRegistry()) {
                String id = descr.getId();
                rows.add(id);
                List<?> fragments = ((PluginDescriptorModel) descr).fragments();
                if (fragments.size() > 0) {
                    for (Object o : fragments) {
                        PluginDescriptor pd = (PluginDescriptor) o;
                        rows.add(pd.getId() + "\b" + id);
                    }
                }
            }
            Collections.sort(rows);
            for (String row : rows) {
                int index = row.indexOf('\b');
                if (index > 0) {
                    addFSE(new JIFSFfragmentJar(row.substring(index + 1), row.substring(0, index),
                            this));
                } else {
                    addFSE(new JIFSFpluginJar(row, this));
                }
            }
        } catch (NameNotFoundException N) {
            System.err.println(N);
        }
    }

    public FSEntry getEntry(String name) {
        return super.getEntry(name);
    }
}
