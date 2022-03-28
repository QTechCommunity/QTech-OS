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
 
package org.jnode.driver.input;

import java.util.ArrayList;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import org.apache.log4j.Logger;
import org.jnode.driver.console.KeyEventBindings;

/**
 * @author Ewout Prangsma (epr@users.sourceforge.net)
 */
public class KeyboardAPIAdapter implements KeyboardAPI {

    /**
     * My logger
     */
    private static final Logger log = Logger.getLogger(KeyboardAPIAdapter.class);

    /**
     * All listeners
     */
    private final ArrayList<KeyboardListener> listeners = new ArrayList<KeyboardListener>();
    
    /**
     * The interpreter
     */
    private KeyboardInterpreter interpreter = null/*new KeyboardInterpreter()*/;
    private final Set<Integer> pressed = new HashSet<Integer>();

    public boolean isKeyDown(int keyCode) {
        return pressed.contains(keyCode);
    }

    public Set<Integer> getKeysDown() {
        return Collections.unmodifiableSet(pressed);
    }

    public synchronized void addKeyboardListener(KeyboardListener l) {
        listeners.add(l);
    }

    /**
     * Claim to be the preferred listener.
     * The given listener must have been added by addKeyboardListener.
     * If there is a security manager, this method will call
     * <code>checkPermission(new DriverPermission("setPreferredListener"))</code>.
     *
     * @param l
     */
    public synchronized void setPreferredListener(KeyboardListener l) {
        final SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(SET_PREFERRED_LISTENER_PERMISSION);
        }
        if (listeners.remove(l)) {
            listeners.add(0, l);
        }
    }

    public KeyboardInterpreter getKbInterpreter() {
        return interpreter;
    }

    public synchronized void removeKeyboardListener(KeyboardListener l) {
        listeners.remove(l);
    }

    public void setKbInterpreter(KeyboardInterpreter kbInterpreter) {
        if (kbInterpreter == null) {
            throw new IllegalArgumentException("kbInterpreter==null");
        }
        this.interpreter = kbInterpreter;
    }

    /**
     * Remove all listeners.
     */
    public synchronized void clear() {
        listeners.clear();
    }

    /**
     * Fire a given pointer event to all known listeners.
     *
     * @param event
     */
    public synchronized void fireEvent(KeyboardEvent event) {
        if (event != null) {
            for (KeyboardListener l : listeners) {
                try {
                    if (event.isKeyPressed()) {
                        this.pressed.add(event.getKeyCode());
                        l.keyPressed(event);
                    } else if (event.isKeyReleased()) {
                        this.pressed.remove(event.getKeyCode());
                        l.keyReleased(event);
                    }
                } catch (Throwable ex) {
                    log.error("Exception in KeyboardListener", ex);
                }
                if (event.isConsumed()) {
                    break;
                }
            }
        }
    }
}
