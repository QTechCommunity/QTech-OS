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
 
package com.qtech.os.install;

import java.io.StringWriter;import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;import com.qtech.os.install.action.GrubInstallerAction;

/**
 * @author Levente S\u00e1ntha
 */
public abstract class AbstractInstaller {
    public enum Step {
        back, forth, quit
    }

    protected List<InstallerAction> actionList = new ArrayList<InstallerAction>();

    public void start() {
        InputContext in = getInputContext();
        OutputContext out = getOutputContext();
        if (actionList.isEmpty())
            return;

        ListIterator<InstallerAction> lit = actionList.listIterator();
        InstallerAction action = lit.next();

    out:
        while (true) {
            ActionInput input = action.getInput(in);
            if (input != null) {
                Step step = input.collect();
                if (step != null && step.equals(Step.quit))
                    break;
                if (step != null && step.equals(Step.back))
                    continue;
            }

            try {
                action.execute();
            } catch (Exception e) {
                StringWriter stringWriter = new StringWriter();
                GrubInstallerAction.PrintWriter1 printWriter1 = new GrubInstallerAction.PrintWriter1(stringWriter, true);
                e.printStackTrace(printWriter1);
                StringBuffer buffer = stringWriter.getBuffer();
                String s = buffer.toString();
                String[] split = s.split("\n");
                for (String str : split) {
                    System.err.println(str);
                    try {
                        Thread.sleep(2500);
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }
                }
                break;
            }

            ActionOutput output = action.getOutput(out);
            if (output != null) {
                Step step = output.show();
                if (step == null) step = Step.forth;
                switch (step) {
                    case back:
                        if (lit.hasPrevious())
                            action = lit.previous();
                        else
                            continue out;
                        break;
                    case forth:
                        if (lit.hasNext())
                            action = lit.next();
                        else
                            break out;
                        break;
                    case quit:
                        break out;
                }
            } else {
                if (lit.hasNext())
                    action = lit.next();
                else
                    break;
            }
        }
    }

    protected abstract InputContext getInputContext();

    protected abstract OutputContext getOutputContext();
}
