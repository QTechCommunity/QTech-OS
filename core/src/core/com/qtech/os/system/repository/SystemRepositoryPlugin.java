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
 
package com.qtech.os.system.repository;

import java.security.AccessController;
import java.security.PrivilegedAction;

import javax.naming.NamingException;

import com.qtech.os.naming.InitialNaming;
import com.qtech.os.plugin.Plugin;
import com.qtech.os.plugin.PluginDescriptor;
import com.qtech.os.plugin.PluginException;
import com.qtech.os.system.repository.plugins.PluginLoaderPlugin;

public final class SystemRepositoryPlugin extends Plugin {

    // private static final Logger log =
    // Logger.getLogger(SystemRepositoryPlugin.class);
    private Repository repository;

    /**
     * @param descriptor
     */
    public SystemRepositoryPlugin(PluginDescriptor descriptor) {
        super(descriptor);
    }

    /**
     * @see com.qtech.os.plugin.Plugin#startPlugin()
     */
    @Override
    protected void startPlugin() throws PluginException {
        final Repository r = (Repository) AccessController
            .doPrivileged(new PrivilegedAction() {
                public Object run() {
                    RepositoryPlugin plugins = new PluginLoaderPlugin(null);

                    return new Repository(plugins);
                }
            });
        this.repository = r;
        try {
            InitialNaming.bind(SystemRepository.NAME, r);
        } catch (NamingException ex) {
            throw new PluginException(ex);
        }
        r.start();
    }

    /**
     * @see com.qtech.os.plugin.Plugin#stopPlugin()
     */
    @Override
    protected void stopPlugin() throws PluginException {
        final Repository r = this.repository;
        this.repository = null;
        if (r != null) {
            r.stop();
        }
        InitialNaming.unbind(SystemRepository.class);
    }
}
