/*
 * Copyright (c) 1998, 2001, Oracle and/or its affiliates. All rights reserved.
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

import java.awt.Component;
import java.awt.Graphics;
import java.io.Serializable;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.plaf.UIResource;

/**
 * CheckboxIcon implementation for OrganicCheckBoxUI
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases. The current serialization support is
 * appropriate for short term storage or RMI between applications running
 * the same version of Swing.  As of 1.4, support for long term storage
 * of all JavaBeans<sup><font size="-2">TM</font></sup>
 * has been added to the <code>java.beans</code> package.
 * Please see {@link java.beans.XMLEncoder}.
 *
 * @author Steve Wilson
 */
public class QCheckBoxIcon implements Icon, UIResource, Serializable {

    protected int getControlSize() {
        return 13;
    }

    public void paintIcon(Component c, Graphics g, int x, int y) {

        JCheckBox cb = (JCheckBox) c;
        ButtonModel model = cb.getModel();
        int controlSize = getControlSize();

        boolean drawCheck = model.isSelected();

        if (model.isEnabled()) {
            if (cb.isBorderPaintedFlat()) {
                g.setColor(QUILookAndFeel.getControlDarkShadow());
                g.drawRect(x + 1, y, controlSize - 1, controlSize - 1);
            }
            if (model.isPressed() && model.isArmed()) {
                if (cb.isBorderPaintedFlat()) {
                    g.setColor(QUILookAndFeel.getControlShadow());
                    g.fillRect(x + 2, y + 1, controlSize - 2, controlSize - 2);
                } else {
                    g.setColor(QUILookAndFeel.getControlShadow());
                    g.fillRect(x, y, controlSize - 1, controlSize - 1);
                    QUIUtils.drawPressed3DBorder(g, x, y, controlSize, controlSize);
                }
            } else if (!cb.isBorderPaintedFlat()) {
                QUIUtils.drawFlush3DBorder(g, x, y, controlSize, controlSize);
            }
            g.setColor(QUILookAndFeel.getControlInfo());
        } else {
            g.setColor(QUILookAndFeel.getControlShadow());
            g.drawRect(x, y, controlSize - 1, controlSize - 1);
        }


        if (drawCheck) {
            if (cb.isBorderPaintedFlat()) {
                x++;
            }
            drawCheck(c, g, x, y);
        }
    }

    protected void drawCheck(Component c, Graphics g, int x, int y) {
        int controlSize = getControlSize();
        g.fillRect(x + 3, y + 5, 2, controlSize - 8);
        g.drawLine(x + (controlSize - 4), y + 3, x + 5, y + (controlSize - 6));
        g.drawLine(x + (controlSize - 4), y + 4, x + 5, y + (controlSize - 5));
    }

    public int getIconWidth() {
        return getControlSize();
    }

    public int getIconHeight() {
        return getControlSize();
    }
}
