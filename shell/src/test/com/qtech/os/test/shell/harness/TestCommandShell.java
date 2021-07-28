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
 
package com.qtech.os.test.shell.harness;

import java.io.InputStream;
import java.io.PrintStream;

import com.qtech.os.shell.CommandLine;
import com.qtech.os.shell.CommandShell;
import com.qtech.os.shell.ShellException;
import com.qtech.os.shell.io.CommandIO;
import com.qtech.os.shell.io.CommandInput;
import com.qtech.os.shell.io.CommandInputOutput;
import com.qtech.os.shell.io.CommandOutput;
import com.qtech.os.shell.io.NullInputStream;
import com.qtech.os.shell.io.NullOutputStream;

/**
 * This class extends CommandShell to modify the shell's stream resolution mechanism.
 * The modified resolveStream resolves in/out/err to streams that are supplied in the
 * constructor.
 *
 * @author crawley@jnode.org
 */
public class TestCommandShell extends CommandShell {

    private final CommandInput cin;
    private final CommandOutput cout;
    private final CommandOutput cerr;

    public TestCommandShell(InputStream in, PrintStream out, PrintStream err)
        throws ShellException {
        super();
        this.cin = new CommandInput(in);
        this.cout = new CommandOutput(out);
        this.cerr = new CommandOutput(err);
    }

    @Override
    protected CommandIO resolveStream(CommandIO stream) {
        if (stream == CommandLine.DEFAULT_STDIN) {
            return cin;
        } else if (stream == CommandLine.DEFAULT_STDOUT) {
            return cout;
        } else if (stream == CommandLine.DEFAULT_STDERR) {
            return cerr;
        } else if (stream == CommandLine.DEVNULL || stream == null) {
            return new CommandInputOutput(new NullInputStream(), new NullOutputStream());
        } else {
            return stream;
        }
    }
}
