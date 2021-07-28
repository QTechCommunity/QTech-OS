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
 
package com.qtech.os.test.shell;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.TreeSet;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import com.qtech.os.shell.CommandShell;
import com.qtech.os.shell.DefaultCommandInvoker;
import com.qtech.os.shell.DefaultInterpreter;
import com.qtech.os.shell.RedirectingInterpreter;
import com.qtech.os.shell.ShellUtils;
import com.qtech.os.shell.ThreadCommandInvoker;
import com.qtech.os.shell.alias.AliasManager;
import com.qtech.os.shell.proclet.ProcletCommandInvoker;
import com.qtech.os.shell.syntax.ArgumentSyntax;
import com.qtech.os.shell.syntax.EmptySyntax;
import com.qtech.os.shell.syntax.OptionSyntax;
import com.qtech.os.shell.syntax.SequenceSyntax;
import com.qtech.os.shell.syntax.SyntaxBundle;
import com.qtech.os.shell.syntax.SyntaxManager;
import com.qtech.os.test.shell.syntax.TestAliasManager;
import com.qtech.os.test.shell.syntax.TestSyntaxManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static com.qtech.os.test.shell.CompletionHelper.checkCompletions;

/**
 * Test command completion using various interpreters and commands.
 *
 * @author crawley@jnode.org
 */
// FIXME
@Ignore
public class CompletionTest {

    private String userDirName = System.getProperty("user.dir");
    private File testDir;
    private String[] aliasCompletions;

    static {
        try {
            Cassowary.initEnv();
        } catch (NamingException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Before
    public void setUp() throws Exception {
        // Setup a temporary home directory for filename completion
        File tempDir = new File(System.getProperty("java.io.tmpdir"));
        testDir = new File(tempDir, "CompletionTestDir");
        testDir.mkdir();
        touch(testDir, "One");
        touch(testDir, "Two");
        touch(testDir, "Three");
        new File(testDir, "Four").mkdir();
        System.setProperty("user.dir", testDir.getAbsolutePath());
    }

    private void touch(File dir, String name) throws IOException {
        File file = new File(dir, name);
        FileWriter fw = new FileWriter(file);
        fw.close();
    }

    @After
    public void tearDown() throws Exception {
        for (File f : testDir.listFiles()) {
            f.delete();
        }
        testDir.delete();
        System.setProperty("user.dir", userDirName);
    }

    public class MyTestCommandShell extends CompletionHelper.TestCommandShell {

        public MyTestCommandShell() throws NameNotFoundException {
            super(new TestAliasManager(), new TestSyntaxManager());
            ShellUtils.getShellManager().registerShell(this);

            ShellUtils.registerCommandInvoker(DefaultCommandInvoker.FACTORY);
            ShellUtils.registerCommandInvoker(ThreadCommandInvoker.FACTORY);
            ShellUtils.registerCommandInvoker(ProcletCommandInvoker.FACTORY);
            ShellUtils.registerCommandInterpreter(DefaultInterpreter.FACTORY);
            ShellUtils.registerCommandInterpreter(RedirectingInterpreter.FACTORY);

            AliasManager am = this.getAliasManager();
            am.add("gc", "com.qtech.os.command.system.GcCommand");
            am.add("cpuid", "com.qtech.os.command.system.CpuIDCommand");
            am.add("set", "com.qtech.os.command.system.SetCommand");
            am.add("dir", "com.qtech.os.test.shell.MyDirCommand");
            am.add("duh", "com.qtech.os.test.shell.MyDuhCommand");
            am.add("cat", "com.qtech.os.test.shell.MyCatCommand");
            am.add("alias", "com.qtech.os.test.shell.MyAliasCommand");
            aliasCompletions = new String[]{"alias ", "cat ", "cpuid ", "dir ", "duh ", "gc ", "set "};

            SyntaxManager sm = this.getSyntaxManager();
            sm.add(new SyntaxBundle("set",
                new SequenceSyntax(new ArgumentSyntax("key"), new ArgumentSyntax("value"))));
            sm.add(new SyntaxBundle("duh", new ArgumentSyntax("path")));
            sm.add(new SyntaxBundle("cpuid", new SequenceSyntax()));
            sm.add(new SyntaxBundle("alias",
                new EmptySyntax(null, "Print all available aliases and corresponding classnames"),
                new SequenceSyntax(null, "Set an aliases for given classnames",
                    new ArgumentSyntax("alias"), new ArgumentSyntax("classname")),
                new OptionSyntax("remove", 'r', null, "Remove an alias")));
        }
    }

    @Test
    public void testDefaultInterpreterNewSyntax() throws Exception {
        MyTestCommandShell cs = new MyTestCommandShell();
        cs.setProperty(CommandShell.INTERPRETER_PROPERTY_NAME, "default");

        final String[] propertyCompletions = getExpectedPropertyNameCompletions();

        checkCompletions(cs, "set ", propertyCompletions, -1);
        checkCompletions(cs, "set a", new String[]{"awt.toolkit "}, -1);
        checkCompletions(cs, "set u", new String[]{
            "user.country ", "user.dir ", "user.home ",
            "user.language ", "user.name ", "user.timezone "}, 4);
        checkCompletions(cs, "set a ", new String[]{}, -1);
        checkCompletions(cs, "set a b", new String[]{}, 6);
        checkCompletions(cs, "set a b ", new String[]{}, -1);

        checkCompletions(cs, "cpuid ", new String[]{}, -1);

        checkCompletions(cs, "duh ", new String[]{"Four/", "One ", "Three ", "Two "}, -1);
        checkCompletions(cs, "duh T", new String[]{"Three ", "Two "}, 4);

        checkCompletions(cs, "alias -", new String[]{"-r "}, 6);

        String[] aliasesPlusR = new String[aliasCompletions.length + 1];
        System.arraycopy(aliasCompletions, 0, aliasesPlusR, 1, aliasCompletions.length);
        aliasesPlusR[0] = "-r ";
        checkCompletions(cs, "alias ", aliasesPlusR, -1);
    }

    /**
     * Snarf the system property names in the form we expect for
     * property name completion.
     */
    private String[] getExpectedPropertyNameCompletions() {
        TreeSet<String> tmp = new TreeSet<String>();
        for (Object key : System.getProperties().keySet()) {
            tmp.add(key + " ");
        }
        return tmp.toArray(new String[tmp.size()]);
    }

}
