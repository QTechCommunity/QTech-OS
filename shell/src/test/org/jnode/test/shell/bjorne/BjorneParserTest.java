/*
 * $Id$
 *
 * Copyright (C) 2020-2022 Ultreon Team
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
 
package org.jnode.test.shell.bjorne;

import java.io.StringReader;

import org.jnode.shell.ShellException;
import org.jnode.shell.bjorne.BjorneParser;
import org.jnode.shell.bjorne.BjorneTokenizer;
import org.junit.Assert;
import org.junit.Test;

public class BjorneParserTest {

    private static final boolean DEBUG = false;

    @Test
    public void testParser() throws ShellException {
        new BjorneParser(new BjorneTokenizer(""));
    }

    @Test
    public void test1() throws ShellException {
        Assert.assertEquals("SimpleCommand{nodeType=1,words=[WORD{foo}]}", doTest("foo"));
    }

    @Test
    public void test2() throws ShellException {
        Assert.assertEquals("SimpleCommand{nodeType=1,words=[WORD{ls},WORD{-l}]}", doTest("ls -l"));
    }

    @Test
    public void test3() throws ShellException {
        Assert.assertEquals(
                "SimpleCommand{nodeType=1,"
                        + "redirects=[Redirect{redirectionType=6,io=IO_NUMBER{1},arg=WORD{/tmp/foo}},"
                        + "Redirect{redirectionType=7,arg=WORD{/tmp/bar}}],"
                        + "words=[WORD{ls},WORD{-l}]}", doTest("ls -l 1< /tmp/foo > /tmp/bar"));
    }

    @Test
    public void test4() throws ShellException {
        Assert.assertEquals("ListCommand{nodeType=2,flags=0x10,"
                + "commands=[SimpleCommand{nodeType=1,assignments=[ASSIGNMENT{FOO=BAR}],"
                + "words=[WORD{ls},WORD{-l}]}," + "SimpleCommand{nodeType=1,words=[WORD{less}]}]}",
                doTest("FOO=BAR ls -l | less"));
    }

    @Test
    public void test5() throws ShellException {
        Assert.assertEquals("ListCommand{nodeType=2,flags=0x10,commands=["
                + "SimpleCommand{nodeType=1,words=[WORD{cat},WORD{foo}]},"
                + "ListCommand{nodeType=10,commands=[" + "ListCommand{nodeType=2,commands=["
                + "SimpleCommand{nodeType=1,words=[WORD{wc},WORD{1}]},"
                + "SimpleCommand{nodeType=1,flags=0x2,words=[WORD{wc},WORD{2}]}]},"
                + "ListCommand{nodeType=2,commands=["
                + "SimpleCommand{nodeType=1,words=[WORD{wc},WORD{3}]},"
                + "SimpleCommand{nodeType=1,flags=0x4,words=[WORD{wc},WORD{4}]}]}]}]}",
                doTest("cat foo | ( wc 1 && wc 2 ; wc 3 || wc 4 )"));
    }

    @Test
    public void test6() throws ShellException {
        Assert.assertEquals("ListCommand{nodeType=2,commands=["
                + "SimpleCommand{nodeType=1,flags=0x1,words=[WORD{cat},WORD{foo}]},"
                + "SimpleCommand{nodeType=1,words=[WORD{cat},WORD{bar}]},"
                + "SimpleCommand{nodeType=1,words=[WORD{cat},WORD{baz}]}]}",
                doTest("cat foo & cat bar ; cat baz ;"));
    }

    @Test
    public void test7() throws ShellException {
        Assert.assertEquals("LoopCommand{nodeType=3,var=NAME{i},"
                + "words=[WORD{1},WORD{2},WORD{3},WORD{4},WORD{5}],"
                + "body=SimpleCommand{nodeType=1,words=[WORD{echo},WORD{$i}]}}",
                doTest("for i in 1 2 3 4 5 ; do echo $i ; done"));
    }

    @Test
    public void test7a() throws ShellException {
        Assert.assertEquals("LoopCommand{nodeType=3,var=NAME{i},"
                + "words=[WORD{1},WORD{2},WORD{3},WORD{4},WORD{5}],"
                + "body=SimpleCommand{nodeType=1,words=[WORD{echo},WORD{$i}]}}",
                doTest("for i in 1 2 3 4 5 ; do \n echo $i ; done"));
    }

    @Test
    public void test8() throws ShellException {
        Assert.assertEquals("LoopCommand{nodeType=4,"
                + "cond=SimpleCommand{nodeType=1,words=[WORD{true}]},"
                + "body=SimpleCommand{nodeType=1,words=[WORD{echo},WORD{$i}]}}",
                doTest("while true ; do echo $i ; done"));
    }

    @Test
    public void test9() throws ShellException {
        Assert.assertEquals("LoopCommand{nodeType=5,"
                + "cond=SimpleCommand{nodeType=1,words=[WORD{true}]},"
                + "body=SimpleCommand{nodeType=1,words=[WORD{echo},WORD{$i}]}}",
                doTest("until true ; do echo $i ; done"));
    }

    @Test
    public void test10() throws ShellException {
        Assert.assertEquals("CaseCommand{nodeType=9,word=WORD{$1},caseItems=["
                + "CaseItem{pattern=[WORD{a}],body="
                + "SimpleCommand{nodeType=1,words=[WORD{ls},WORD{-l}]}},"
                + "CaseItem{pattern=[WORD{b}],body="
                + "SimpleCommand{nodeType=1,words=[WORD{ls},WORD{-a}]}}]}",
                doTest("case $1 in ( a ) ls -l ;; b ) ls -a ; esac"));
    }

    @Test
    public void test11() throws ShellException {
        Assert.assertEquals("IfCommand{nodeType=6,cond="
                + "SimpleCommand{nodeType=1,words=[WORD{true}]},thenPart="
                + "SimpleCommand{nodeType=1,words=[WORD{echo},WORD{yes}]}}",
                doTest("if true ; then echo yes ; fi"));
    }

    @Test
    public void test12() throws ShellException {
        Assert.assertEquals("IfCommand{nodeType=6,cond="
                + "SimpleCommand{nodeType=1,words=[WORD{true}]},thenPart="
                + "SimpleCommand{nodeType=1,words=[WORD{echo},WORD{yes}]},elsePart="
                + "SimpleCommand{nodeType=1,words=[WORD{echo},WORD{false}]}}",
                doTest("if true ; then echo yes ; else echo false ; fi"));
    }

    private String doTest(String input) throws ShellException {
        BjorneParser p = new BjorneParser(new BjorneTokenizer(new StringReader(input), DEBUG));
        String res = p.parse().toString();
        if (DEBUG) {
            System.err.println(res);
        }
        return res;
    }
}
