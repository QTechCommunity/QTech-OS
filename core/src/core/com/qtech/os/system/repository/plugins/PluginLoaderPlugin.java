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
 
package com.qtech.os.system.repository.plugins;

import java.nio.ByteBuffer;

import javax.naming.CompositeName;
import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.NameNotFoundException;

import org.apache.log4j.Logger;
import com.qtech.os.naming.InitialNaming;
import com.qtech.os.plugin.PluginLoader;
import com.qtech.os.plugin.PluginManager;
import com.qtech.os.plugin.PluginReference;
import com.qtech.os.plugin.PluginRegistry;
import com.qtech.os.system.repository.RepositoryPlugin;
import com.qtech.os.system.repository.spi.SystemRepositoryProvider;

public final class PluginLoaderPlugin extends RepositoryPlugin {

    /**
     * Name of the Plugins sub-node in the repository
     */
    private static final Name PLUGINS;

    /**
     * Empty array of providers
     */
    static final SystemRepositoryProvider[] EMPTY_ARR = new SystemRepositoryProvider[0];

    private PluginRegistry registry;

    /**
     * My logger
     */
    private static final Logger log = Logger
        .getLogger(PluginLoaderPlugin.class);

    static {
        try {
            PLUGINS = new CompositeName("plugins");
        } catch (InvalidNameException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * @param next
     */
    public PluginLoaderPlugin(RepositoryPlugin next) {
        super(next);
    }

    /**
     * @see com.qtech.os.system.repository.RepositoryPlugin
     * #providerAdded(com.qtech.os.system.repository.spi.SystemRepositoryProvider[],
     *      com.qtech.os.system.repository.spi.SystemRepositoryProvider)
     */
    @Override
    protected void providerAdded(SystemRepositoryProvider[] providers,
                                 SystemRepositoryProvider provider) {
        // TODO Auto-generated method stub
        super.providerAdded(providers, provider);
    }

    /**
     * @see com.qtech.os.system.repository.RepositoryPlugin
     * #providerRemoved(com.qtech.os.system.repository.spi.SystemRepositoryProvider[],
     *      com.qtech.os.system.repository.spi.SystemRepositoryProvider)
     */
    @Override
    protected void providerRemoved(SystemRepositoryProvider[] providers,
                                   SystemRepositoryProvider provider) {
        // TODO Auto-generated method stub
        super.providerRemoved(providers, provider);
    }

    /**
     * @see com.qtech.os.system.repository.RepositoryPlugin#start()
     */
    @Override
    protected void start() {
        try {
            final PluginManager pm;
            pm = InitialNaming.lookup(PluginManager.NAME);
            registry = pm.getRegistry();
        } catch (NameNotFoundException ex) {
            log.error("Cannot find PluginManager; PluginLoader not installed");
        }
    }

    /**
     * @see com.qtech.os.system.repository.RepositoryPlugin#stop()
     */
    @Override
    protected void stop() {
        registry = null;
    }

    /**
     * The actual loader implementation.
     *
     * @author Ewout Prangsma (epr@users.sourceforge.net)
     */
    private static class Loader extends PluginLoader {

        /**
         * List of providers that have a plugins sub-node
         */
        private SystemRepositoryProvider[] pluginProviders = EMPTY_ARR;

        /**
         * @see com.qtech.os.plugin.PluginLoader#getPluginBuffer(java.lang.String,
         *      java.lang.String)
         */
        @Override
        public ByteBuffer getPluginBuffer(PluginReference pluginReference) {
            final Name name;
            try {
                name = ((Name) PLUGINS.clone()).add(getPluginFileName(pluginReference));
            } catch (InvalidNameException ex) {
                log.debug("Cannot combine name", ex);
                return null;
            }
            final SystemRepositoryProvider[] pluginProviders = this.pluginProviders;
            for (SystemRepositoryProvider prov : pluginProviders) {
                //todo empty ?
            }
            // TODO Auto-generated method stub
            return null;
        }
    }
}
