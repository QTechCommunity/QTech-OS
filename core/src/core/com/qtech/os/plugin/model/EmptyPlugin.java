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
 
package com.qtech.os.plugin.model;

import com.qtech.os.plugin.Plugin;
import com.qtech.os.plugin.PluginDescriptor;
import com.qtech.os.plugin.PluginException;

/**
 * @author epr
 */
final class EmptyPlugin extends Plugin {

    /**
     * Initialize a new instance
     *
     * @param descriptor
     */
    public EmptyPlugin(PluginDescriptor descriptor) {
        super(descriptor);
    }

    /**
     * @see com.qtech.os.plugin.Plugin#startPlugin()
     */
    protected void startPlugin() throws PluginException {
        // Do nothing
    }

    /**
     * @see com.qtech.os.plugin.Plugin#stopPlugin()
     */
    protected void stopPlugin() throws PluginException {
        // Do nothing
    }
}
