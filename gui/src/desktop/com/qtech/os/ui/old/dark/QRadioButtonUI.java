/*
 * Copyright (c) 1998, 2005, Oracle and/or its affiliates. All rights reserved.
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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.plaf.basic.BasicRadioButtonUI;
import javax.swing.text.View;
import sun.awt.AppContext;
import sun.swing.SwingUtilities2;


/**
 * RadioButtonUI implementation for MetalRadioButtonUI
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
 * @author Michael C. Albers (Metal modifications)
 * @author Jeff Dinkins (original BasicRadioButtonCode)
 */
public class QRadioButtonUI extends BasicRadioButtonUI {

    private static final Object METAL_RADIO_BUTTON_UI_KEY = new Object();

    protected Color focusColor;
    protected Color selectColor;
    protected Color disabledTextColor;

    private boolean defaults_initialized = false;

    // ********************************
    //        Create PlAF
    // ********************************
    public static ComponentUI createUI(JComponent c) {
        AppContext appContext = AppContext.getAppContext();
        QRadioButtonUI qRadioButtonUI =
            (QRadioButtonUI) appContext.get(METAL_RADIO_BUTTON_UI_KEY);
        if (qRadioButtonUI == null) {
            qRadioButtonUI = new QRadioButtonUI();
            appContext.put(METAL_RADIO_BUTTON_UI_KEY, qRadioButtonUI);
        }
        return qRadioButtonUI;
    }

    // ********************************
    //        Install Defaults 
    // ********************************
    public void installDefaults(AbstractButton b) {
        super.installDefaults(b);
        if (!defaults_initialized) {
            focusColor = UIManager.getColor(getPropertyPrefix() + "focus");
            selectColor = UIManager.getColor(getPropertyPrefix() + "select");
            disabledTextColor = UIManager.getColor(getPropertyPrefix() + "disabledText");
            defaults_initialized = true;
        }
        LookAndFeel.installProperty(b, "opaque", Boolean.TRUE);
    }

    protected void uninstallDefaults(AbstractButton b) {
        super.uninstallDefaults(b);
        defaults_initialized = false;
    }

    // ********************************
    //         Default Accessors 
    // ********************************
    protected Color getSelectColor() {
        return selectColor;
    }

    protected Color getDisabledTextColor() {
        return disabledTextColor;
    }

    protected Color getFocusColor() {
        return focusColor;
    }


    // ********************************
    //        Paint Methods
    // ********************************
    public synchronized void paint(Graphics g, JComponent c) {

        AbstractButton b = (AbstractButton) c;
        ButtonModel model = b.getModel();

        Dimension size = c.getSize();

        int w = size.width;
        int h = size.height;

        Font f = c.getFont();
        g.setFont(f);
        FontMetrics fm = SwingUtilities2.getFontMetrics(c, g, f);

        Rectangle viewRect = new Rectangle(size);
        Rectangle iconRect = new Rectangle();
        Rectangle textRect = new Rectangle();

        Insets i = c.getInsets();
        viewRect.x += i.left;
        viewRect.y += i.top;
        viewRect.width -= (i.right + viewRect.x);
        viewRect.height -= (i.bottom + viewRect.y);

        Icon altIcon = b.getIcon();
        Icon selectedIcon = null;
        Icon disabledIcon = null;

        String text = SwingUtilities.layoutCompoundLabel(
            c, fm, b.getText(), altIcon != null ? altIcon : getDefaultIcon(),
            b.getVerticalAlignment(), b.getHorizontalAlignment(),
            b.getVerticalTextPosition(), b.getHorizontalTextPosition(),
            viewRect, iconRect, textRect, b.getIconTextGap());

        // fill background
        if (c.isOpaque()) {
            g.setColor(b.getBackground());
            g.fillRect(0, 0, size.width, size.height);
        }


        // Paint the radio button
        if (altIcon != null) {

            if (!model.isEnabled()) {
                if (model.isSelected()) {
                    altIcon = b.getDisabledSelectedIcon();
                } else {
                    altIcon = b.getDisabledIcon();
                }
            } else if (model.isPressed() && model.isArmed()) {
                altIcon = b.getPressedIcon();
                if (altIcon == null) {
                    // Use selected icon
                    altIcon = b.getSelectedIcon();
                }
            } else if (model.isSelected()) {
                if (b.isRolloverEnabled() && model.isRollover()) {
                    altIcon = b.getRolloverSelectedIcon();
                    if (altIcon == null) {
                        altIcon = b.getSelectedIcon();
                    }
                } else {
                    altIcon = b.getSelectedIcon();
                }
            } else if (b.isRolloverEnabled() && model.isRollover()) {
                altIcon = b.getRolloverIcon();
            }

            if (altIcon == null) {
                altIcon = b.getIcon();
            }

            altIcon.paintIcon(c, g, iconRect.x, iconRect.y);

        } else {
            getDefaultIcon().paintIcon(c, g, iconRect.x, iconRect.y);
        }


        // Draw the Text
        if (text != null) {
            View v = (View) c.getClientProperty(BasicHTML.propertyKey);
            if (v != null) {
                v.paint(g, textRect);
            } else {
                int mnemIndex = b.getDisplayedMnemonicIndex();
                if (model.isEnabled()) {
                    // *** paint the text normally
                    g.setColor(b.getForeground());
                } else {
                    // *** paint the text disabled
                    g.setColor(getDisabledTextColor());
                }
                SwingUtilities2.drawStringUnderlineCharAt(c, g, text,
                    mnemIndex, textRect.x, textRect.y + fm.getAscent());
            }
            if (b.hasFocus() && b.isFocusPainted() &&
                textRect.width > 0 && textRect.height > 0) {
                paintFocus(g, textRect, size);
            }
        }
    }

    protected void paintFocus(Graphics g, Rectangle t, Dimension d) {
        g.setColor(getFocusColor());
        g.drawRect(t.x - 1, t.y - 1, t.width + 1, t.height + 1);
    }
}
