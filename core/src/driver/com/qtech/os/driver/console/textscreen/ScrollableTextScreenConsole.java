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
 
package com.qtech.os.driver.console.textscreen;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import com.qtech.os.driver.console.ConsoleManager;
import com.qtech.os.driver.console.ScrollableTextConsole;
import com.qtech.os.driver.input.KeyboardEvent;
import com.qtech.os.driver.input.PointerEvent;
import com.qtech.os.driver.textscreen.ScrollableTextScreen;

/**
 * @author Ewout Prangsma (epr@users.sourceforge.net)
 */
public class ScrollableTextScreenConsole extends TextScreenConsole implements ScrollableTextConsole {

    /**
     * @param mgr
     * @param name
     * @param screen
     */
    public ScrollableTextScreenConsole(ConsoleManager mgr, String name,
            ScrollableTextScreen screen, int options) {
        super(mgr, name, screen, options);
    }

    /**
     * Scroll a given number of rows up.
     *
     * @param rows
     */
    public void scrollUp(int rows) {
        final ScrollableTextScreen screen = getScrollableTextScreen();
        screen.scrollUp(rows);

        final int length = rows * screen.getWidth();
        screen.sync(screen.getHeight() * screen.getWidth() - length, length);
    }

    /**
     * Scroll a given number of rows down.
     *
     * @param rows
     */
    public void scrollDown(int rows) {
        final ScrollableTextScreen screen = getScrollableTextScreen();
        screen.scrollDown(rows);
        screen.sync(0, rows * screen.getWidth());
    }

    /**
     * Ensure that the given row is visible.
     * 
     * @param row
     */
    @Override
    public void ensureVisible(int row) {
        getScrollableTextScreen().ensureVisible(row, isFocused());
    }

    /**
     * @see com.qtech.os.driver.input.KeyboardListener#keyPressed(com.qtech.os.driver.input.KeyboardEvent)
     */
    @Override
    public void keyPressed(KeyboardEvent event) {
        if (isFocused() && !event.isConsumed()) {
            final int modifiers = event.getModifiers();
            if ((modifiers & InputEvent.SHIFT_DOWN_MASK) != 0) {
                switch (event.getKeyCode()) {
                    case KeyEvent.VK_PAGE_UP:
                        scrollUp(10);
                        event.consume();
                        break;
                    case KeyEvent.VK_PAGE_DOWN:
                        scrollDown(10);
                        event.consume();
                        break;
                    case KeyEvent.VK_UP:
                        scrollUp(1);
                        event.consume();
                        break;
                    case KeyEvent.VK_DOWN:
                        scrollDown(1);
                        event.consume();
                        break;
                }
            }
        }
        if (!event.isConsumed()) {
            super.keyPressed(event);
        }
    }

    /**
     * @see com.qtech.os.driver.input.PointerListener#pointerStateChanged(com.qtech.os.driver.input.PointerEvent)
     */
    @Override
    public void pointerStateChanged(PointerEvent event) {
        if (isFocused() && (event.getZ() != 0)) {
            final int z = event.getZ();
            if (z < 0) {
                scrollUp(Math.abs(z));
            } else {
                scrollDown(Math.abs(z));
            }
            event.consume();
        }
        if (!event.isConsumed()) {
            super.pointerStateChanged(event);
        }
    }
    
    private final ScrollableTextScreen getScrollableTextScreen() {
        return (ScrollableTextScreen) getScreen();
    }
}
