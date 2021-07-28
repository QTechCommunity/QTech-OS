/*
 * Copyright (c) 1998, 2004, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package com.qtech.os.ui.old.dark;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.XMLEncoder;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicScrollPaneUI;


/**
 * A Metal L&F implementation of ScrollPaneUI.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases. The current serialization support is
 * appropriate for short term storage or RMI between applications running
 * the same version of Swing.  As of 1.4, support for long term storage
 * of all JavaBeans<sup><font size="-2">TM</font></sup>
 * has been added to the <code>java.beans</code> package.
 * Please see {@link XMLEncoder}.
 *
 * @author Steve Wilson
 */
public class QScrollPaneUI extends BasicScrollPaneUI {

    private PropertyChangeListener scrollBarSwapListener;

    public static ComponentUI createUI(JComponent x) {
        return new QScrollPaneUI();
    }

    public void installUI(JComponent c) {

        super.installUI(c);

        JScrollPane sp = (JScrollPane) c;
        updateScrollbarsFreeStanding();
    }

    public void uninstallUI(JComponent c) {
        super.uninstallUI(c);

        JScrollPane sp = (JScrollPane) c;
        JScrollBar hsb = sp.getHorizontalScrollBar();
        JScrollBar vsb = sp.getVerticalScrollBar();
        if (hsb != null) {
            hsb.putClientProperty(QScrollBarUI.FREE_STANDING_PROP, null);
        }
        if (vsb != null) {
            vsb.putClientProperty(QScrollBarUI.FREE_STANDING_PROP, null);
        }
    }


    public void installListeners(JScrollPane scrollPane) {
        super.installListeners(scrollPane);
        scrollBarSwapListener = createScrollBarSwapListener();
        scrollPane.addPropertyChangeListener(scrollBarSwapListener);
    }


    public void uninstallListeners(JScrollPane scrollPane) {
        super.uninstallListeners(scrollPane);

        scrollPane.removePropertyChangeListener(scrollBarSwapListener);
    }

    /**
     * If the border of the scrollpane is an instance of
     * <code>MetalBorders.ScrollPaneBorder</code>, the client property
     * <code>FREE_STANDING_PROP</code> of the scrollbars
     * is set to false, otherwise it is set to true.
     */
    private void updateScrollbarsFreeStanding() {
        if (scrollpane == null) {
            return;
        }
        Border border = scrollpane.getBorder();
        Object value;

        if (border instanceof QBorders.ScrollPaneBorder) {
            value = Boolean.FALSE;
        } else {
            value = Boolean.TRUE;
        }
        JScrollBar sb = scrollpane.getHorizontalScrollBar();
        if (sb != null) {
            sb.putClientProperty
                (QScrollBarUI.FREE_STANDING_PROP, value);
        }
        sb = scrollpane.getVerticalScrollBar();
        if (sb != null) {
            sb.putClientProperty
                (QScrollBarUI.FREE_STANDING_PROP, value);
        }
    }

    protected PropertyChangeListener createScrollBarSwapListener() {
        return new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent e) {
                String propertyName = e.getPropertyName();
                if (propertyName.equals("verticalScrollBar") ||
                    propertyName.equals("horizontalScrollBar")) {
                    JScrollBar oldSB = (JScrollBar) e.getOldValue();
                    if (oldSB != null) {
                        oldSB.putClientProperty(
                            QScrollBarUI.FREE_STANDING_PROP, null);
                    }
                    JScrollBar newSB = (JScrollBar) e.getNewValue();
                    if (newSB != null) {
                        newSB.putClientProperty(
                            QScrollBarUI.FREE_STANDING_PROP,
                            Boolean.FALSE);
                    }
                } else if ("border".equals(propertyName)) {
                    updateScrollbarsFreeStanding();
                }
            }
        };
    }

}
