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
 
package com.qtech.os.test.shell.syntax;

import com.qtech.os.shell.AbstractCommand;
import com.qtech.os.shell.Command;
import com.qtech.os.shell.CommandInfo;
import com.qtech.os.shell.CommandLine;
import com.qtech.os.shell.CommandLine.Token;
import com.qtech.os.shell.syntax.AlternativesSyntax;
import com.qtech.os.shell.syntax.Argument;
import com.qtech.os.shell.syntax.CommandSyntaxException;
import com.qtech.os.shell.syntax.FileArgument;
import com.qtech.os.shell.syntax.FlagArgument;
import com.qtech.os.shell.syntax.IntegerArgument;
import com.qtech.os.shell.syntax.OptionSyntax;
import com.qtech.os.shell.syntax.Syntax;
import org.junit.Assert;

public class AlternativesSyntaxTest {

    public static class Test extends AbstractCommand {
        private final FileArgument fileArg = new FileArgument("fileArg", Argument.OPTIONAL +
                Argument.MULTIPLE);
        private final IntegerArgument intArg = new IntegerArgument("intArg", Argument.OPTIONAL +
                Argument.MULTIPLE);
        private final FlagArgument flagArg = new FlagArgument("flagArg", Argument.OPTIONAL +
                Argument.MULTIPLE);

        public Test() {
            registerArguments(fileArg, intArg, flagArg);
        }

        public void execute() throws Exception {
        }
    }

    @org.junit.Test
    public void testConstructor() {
        new AlternativesSyntax(new OptionSyntax("intArg", 'i'), new OptionSyntax("fileArg", 'f'),
                new OptionSyntax("flagArg", "xxx"));
    }

    @org.junit.Test
    public void testFormat() {
        Test test = new Test();
        Syntax syntax1 =
                new AlternativesSyntax(new OptionSyntax("intArg", 'i'), new OptionSyntax("fileArg",
                        'f'), new OptionSyntax("flagArg", "xxx"));
        Assert.assertEquals("( -i <intArg> ) | ( -f <fileArg> ) | --xxx",
                syntax1.format(test.getArgumentBundle()));
    }

    @org.junit.Test
    public void testOne() throws Exception {
        TestShell shell = new TestShell();
        shell.addAlias("cmd", "com.qtech.os.test.shell.syntax.AlternativesSyntaxTest$Test");
        shell.addSyntax("cmd", new AlternativesSyntax(new OptionSyntax("intArg", 'i'),
                new OptionSyntax("fileArg", 'f'), new OptionSyntax("flagArg", "xxx")));

        CommandLine cl;
        CommandInfo cmdInfo;
        Command cmd;

        try {
            cl = new CommandLine(new Token("cmd"), new Token[] {}, null);
            cmdInfo = cl.parseCommandLine(shell);
            Assert.fail("no exception");
        } catch (CommandSyntaxException ex) {
            // expected
        }

        cl =
                new CommandLine(new Token("cmd"), new Token[] {new Token("-f"), new Token("F1")},
                        null);
        cmdInfo = cl.parseCommandLine(shell);
        cmd = cmdInfo.createCommandInstance();
        Assert.assertEquals(1, cmd.getArgumentBundle().getArgument("fileArg").getValues().length);
        Assert.assertEquals("F1", cmd.getArgumentBundle().getArgument("fileArg").getValue()
                .toString());
        Assert.assertEquals(0, cmd.getArgumentBundle().getArgument("intArg").getValues().length);
        Assert.assertEquals(0, cmd.getArgumentBundle().getArgument("flagArg").getValues().length);

        cl =
                new CommandLine(new Token("cmd"), new Token[] {new Token("-i"), new Token("41")},
                        null);
        cmdInfo = cl.parseCommandLine(shell);
        cmd = cmdInfo.createCommandInstance();
        Assert.assertEquals(0, cmd.getArgumentBundle().getArgument("fileArg").getValues().length);
        Assert.assertEquals(1, cmd.getArgumentBundle().getArgument("intArg").getValues().length);
        Assert.assertEquals("41", cmd.getArgumentBundle().getArgument("intArg").getValue()
                .toString());
        Assert.assertEquals(0, cmd.getArgumentBundle().getArgument("flagArg").getValues().length);

        cl = new CommandLine(new Token("cmd"), new Token[] {new Token("--xxx")}, null);
        cmdInfo = cl.parseCommandLine(shell);
        cmd = cmdInfo.createCommandInstance();
        Assert.assertEquals(0, cmd.getArgumentBundle().getArgument("fileArg").getValues().length);
        Assert.assertEquals(0, cmd.getArgumentBundle().getArgument("intArg").getValues().length);
        Assert.assertEquals(1, cmd.getArgumentBundle().getArgument("flagArg").getValues().length);

        try {
            cl =
                    new CommandLine(new Token("cmd"), new Token[] {new Token("--xxx"),
                        new Token("-f"), new Token("F1")}, null);
            cl.parseCommandLine(shell);
            Assert.fail("no exception");
        } catch (CommandSyntaxException ex) {
            // expected
        }
    }
}
