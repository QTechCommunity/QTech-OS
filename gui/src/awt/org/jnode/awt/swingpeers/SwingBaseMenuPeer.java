/*
 * $Id$
 *
 * Copyright (C) 2020-2022 Ultreon Team
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
 
package org.jnode.awt.swingpeers;

import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.peer.MenuPeer;
import javax.swing.JComponent;

/**
 * @author Ewout Prangsma (epr@users.sourceforge.net)
 */
abstract class SwingBaseMenuPeer<awtT extends Menu, peerT extends JComponent>
    extends SwingBaseMenuItemPeer<awtT, peerT> implements MenuPeer {

    public SwingBaseMenuPeer(SwingToolkit toolkit, awtT menu, peerT jComponent) {
        super(toolkit, menu, jComponent);
    }

    public abstract void addItem(MenuItem item);

    public abstract void delItem(int index);

    public abstract void addSeparator();
}
