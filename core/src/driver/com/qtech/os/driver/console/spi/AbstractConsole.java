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
 
package com.qtech.os.driver.console.spi;

import java.util.ArrayList;
import com.qtech.os.driver.console.Console;
import com.qtech.os.driver.console.ConsoleEvent;
import com.qtech.os.driver.console.ConsoleListener;
import com.qtech.os.driver.console.ConsoleManager;
import com.qtech.os.driver.input.KeyboardEvent;
import com.qtech.os.driver.input.KeyboardListener;
import com.qtech.os.driver.input.PointerEvent;
import com.qtech.os.driver.input.PointerListener;
import com.qtech.os.system.event.FocusEvent;
import com.qtech.os.util.QueueProcessor;
import com.qtech.os.util.QueueProcessorThread;

/**
 * @author epr
 */
public abstract class AbstractConsole implements Console {

    private final ArrayList<KeyboardListener> keyboardListeners = new ArrayList<KeyboardListener>();

    private final ArrayList<PointerListener> pointerListeners = new ArrayList<PointerListener>();

    private final ArrayList<ConsoleListener> consoleListeners = new ArrayList<ConsoleListener>();

    private final String consoleName;

    private final ConsoleManager mgr;

    private int acceleratorKeyCode = 0;

    /**
     * Initialize this instance.
     *
     * @param mgr
     * @param name
     */
    public AbstractConsole(ConsoleManager mgr, String name) {
        this.mgr = mgr;
        this.consoleName = name;
        this.keyboardEventProcessor.start();
    }

    /**
     * Does this console has the focus?
     *
     * @return True if this console has the focus, false otherwise
     */
    public boolean isFocused() {
        return (mgr.getFocus() == this);
    }

    /**
     * Add a PointerListener
     *
     * @param l
     */
    public void addPointerListener(PointerListener l) {
        synchronized (pointerListeners) {
            if (!pointerListeners.contains(l)) {
                pointerListeners.add(l);
            }
        }
    }

    /**
     * Remove a PointerListener
     *
     * @param l
     */
    public void removePointerListener(PointerListener l) {
        synchronized (pointerListeners) {
            pointerListeners.remove(l);
        }
    }

    /**
     * Send the PointerEvent to all the PointerListeners
     *
     * @param event
     */
    protected void dispatchPointerEvent(PointerEvent event) {
        if (event.isConsumed()) {
            return;
        }
        synchronized (pointerListeners) {
            for (PointerListener l : pointerListeners) {
                l.pointerStateChanged(event);
                if (event.isConsumed()) {
                    break;
                }
            }
        }
    }

    /**
     * @param l
     * @see com.qtech.os.driver.console.Console#addKeyboardListener(com.qtech.os.driver.input.KeyboardListener)
     */
    public void addKeyboardListener(KeyboardListener l) {
        synchronized (keyboardListeners) {
            if (!keyboardListeners.contains(l)) {
                keyboardListeners.add(l);
            }
        }
    }

    /**
     * @param l
     * @see com.qtech.os.driver.console.Console#removeKeyboardListener(com.qtech.os.driver.input.KeyboardListener)
     */
    public void removeKeyboardListener(KeyboardListener l) {
        synchronized (keyboardListeners) {
            keyboardListeners.remove(l);
        }
    }

    /**
     * @param l
     * @see com.qtech.os.driver.console.Console#addConsoleListener(com.qtech.os.driver.console.ConsoleListener)
     */
    public void addConsoleListener(ConsoleListener l) {
        synchronized (consoleListeners) {
            if (!consoleListeners.contains(l)) {
                consoleListeners.add(l);
            }
        }
    }

    /**
     * @param l
     * @see com.qtech.os.driver.console.Console#removeConsoleListener(com.qtech.os.driver.console.ConsoleListener)
     */
    public void removeConsoleListener(ConsoleListener l) {
        synchronized (consoleListeners) {
            consoleListeners.remove(l);
        }
    }

    /**
     * Dispatch a given keyboard event to all known listeners.
     *
     * @param event
     */
    protected void dispatchConsoleEvent(ConsoleEvent event) {
        if (event.isConsumed()) {
            return;
        }

        synchronized (consoleListeners) {
            for (ConsoleListener l : consoleListeners) {
                l.consoleClosed(event);
            }
        }
    }

    /**
     * @param event
     * @see com.qtech.os.driver.input.KeyboardListener#keyPressed(com.qtech.os.driver.input.KeyboardEvent)
     */
    public void keyPressed(KeyboardEvent event) {
        if (isFocused()) {
            keyboardEventProcessor.getQueue().add(event);
        }
    }

    /**
     * @param event
     * @see com.qtech.os.driver.input.KeyboardListener#keyReleased(com.qtech.os.driver.input.KeyboardEvent)
     */
    public void keyReleased(KeyboardEvent event) {
        if (isFocused()) {
            keyboardEventProcessor.getQueue().add(event);
        }
    }

    /**
     * @param event
     * @see com.qtech.os.system.event.FocusListener#focusGained(com.qtech.os.system.event.FocusEvent)
     */
    public void focusGained(FocusEvent event) {
    }

    /**
     * @param event
     * @see com.qtech.os.system.event.FocusListener#focusLost(com.qtech.os.system.event.FocusEvent)
     */
    public void focusLost(FocusEvent event) {
    }

    private QueueProcessorThread<KeyboardEvent> keyboardEventProcessor =
        new QueueProcessorThread<KeyboardEvent>("console-keyboard-event-processor",
            new QueueProcessor<KeyboardEvent>() {
                public void process(KeyboardEvent event) throws Exception {
                    dispatchKeyboardEvent(event);
                }
            });

    /**
     * Dispatch a given keyboard event to all known listeners.
     *
     * @param event
     */
    protected void dispatchKeyboardEvent(KeyboardEvent event) {
        if (event.isConsumed()) {
            return;
        }

        synchronized (keyboardListeners) {
            for (KeyboardListener l : keyboardListeners) {
                if (event.isKeyPressed()) {
                    l.keyPressed(event);
                } else if (event.isKeyReleased()) {
                    l.keyReleased(event);
                }
                if (event.isConsumed()) {
                    break;
                }
            }
        }
    }

    /**
     * Respond to scroll events from the mouse.
     *
     * @param event
     * @see com.qtech.os.driver.input.PointerListener#pointerStateChanged(com.qtech.os.driver.input.PointerEvent)
     */
    public void pointerStateChanged(PointerEvent event) {
        if (isFocused()) {
            dispatchPointerEvent(event);
        }
    }

    /**
     * Close this console.
     *
     * @see com.qtech.os.driver.console.Console#close()
     */
    public void close() {
        mgr.unregisterConsole(this);
        keyboardEventProcessor.stopProcessor();
        dispatchConsoleEvent(new ConsoleEvent(this));
    }

    public void setAcceleratorKeyCode(int keyCode) {
        this.acceleratorKeyCode = keyCode;
        if (mgr instanceof AbstractConsoleManager)
            ((AbstractConsoleManager) mgr).restack(this);
    }

    public int getAcceleratorKeyCode() {
        return acceleratorKeyCode;
    }

    /**
     * @return Returns the consoleName.
     */
    public String getConsoleName() {
        return consoleName;
    }

    /**
     * @see com.qtech.os.driver.console.Console#getManager()
     */
    public final ConsoleManager getManager() {
        return mgr;
    }
}
