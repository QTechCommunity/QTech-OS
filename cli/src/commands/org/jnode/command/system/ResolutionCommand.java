/*
 * $Id$
 *
 * Copyright (C) 2003-2015 JNode.org
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
 
package org.jnode.command.system;

import gnu.java.security.action.SetPropertyAction;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.DirectColorModel;
import java.security.AccessController;
import org.jnode.awt.JNodeFrameBufferDevice;
import org.jnode.awt.JNodeGraphicsConfiguration;
import org.jnode.awt.JNodeToolkit;
import org.jnode.awt.image.JNodeBufferedImage;
import org.jnode.driver.video.FrameBufferConfiguration;
import org.jnode.shell.AbstractCommand;
import org.jnode.shell.CommandLine;
import org.jnode.shell.ShellUtils;
import org.jnode.shell.alias.AliasManager;
import org.jnode.shell.alias.NoSuchAliasException;
import org.jnode.shell.syntax.Argument;
import org.jnode.shell.syntax.IntegerArgument;

/**
 * @author Ewout Prangsma (epr@users.sourceforge.net)
 */
public class ResolutionCommand extends AbstractCommand {

    private static final String help_resolution = "Stop all services and devices, then halt the computer";
    private final IntegerArgument argWidth;
    private final IntegerArgument argHeight;

    public ResolutionCommand() {
        super(help_resolution);

        argWidth  = new IntegerArgument("alias", Argument.MANDATORY, help_resolution);
        argHeight  = new IntegerArgument("className", Argument.MANDATORY, help_resolution);
        registerArguments(argWidth, argHeight);
    }

    public static void main(String[] args) throws Exception {
        new ResolutionCommand().execute(args);
    }

    @Override
    public void execute() {

        final AliasManager aliasMgr = ShellUtils.getCurrentAliasManager();

        if (!argWidth.isSet()) {
            System.err.println("The width is not set.");
        } else if (!argHeight.isSet()) {
            System.err.println("The height is not set.");
        }
        CommandLine commandLine = this.getCommandLine();
        String[] arguments = commandLine.getArguments();

        int w, h, bits;
        try {
            w = Integer.parseInt(arguments[0]);
        } catch (NumberFormatException e) {
            System.err.println("Invalid width! (Argument 1)");
            return;
        }
        try {
            h = Integer.parseInt(arguments[1]);
        } catch (NumberFormatException e) {
            System.err.println("Invalid height! (Argument 2)");
            return;
        }

        try {
            bits = Integer.parseInt(arguments[2]);
        } catch (NumberFormatException e) {
            System.err.println("Invalid color bits! (Argument 3)");
            return;
        }

        if (w < 480) {
            System.err.println("Width is too small: " + w + " need min. 480. (Argument 1)");
        }

        if (h < 320) {
            System.err.println("Width is too small: " + w + " need min. 320. (Argument 2)");
        }

        if (bits != 32 && bits != 24) {
            System.err.println("Color bits is not 32 or 24. (Argument 3)");
        }

        try {
            JNodeGraphicsConfiguration config = new JNodeGraphicsConfiguration(JNodeFrameBufferDevice.getInstance(),
                new FrameBufferConfiguration(w, h, new DirectColorModel(bits, 0xff0000, 0x00ff00, 0x0000ff)) {
                    @Override
                    public JNodeBufferedImage createCompatibleImage(int w, int h, int transparency) {
                        return new JNodeBufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
                    }
                });
            ((JNodeToolkit) Toolkit.getDefaultToolkit()).changeScreenSize(config);
            AccessController.doPrivileged(new SetPropertyAction("jnode.awt.screensize", config.toString()));
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
