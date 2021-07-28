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
 
package com.qtech.os.shell.bjorne;

import com.qtech.os.shell.syntax.Argument;
import com.qtech.os.shell.syntax.ArgumentSyntax;
import com.qtech.os.shell.syntax.IntegerArgument;
import com.qtech.os.shell.syntax.OptionalSyntax;
import com.qtech.os.shell.syntax.SyntaxBundle;

/**
 * This class implements the 'return' built-in.  This is done by throwing a 
 * BjorneControlException with code 'BRANCH_RETURN'.
 * 
 * @author crawley@jnode.org
 */
final class ReturnBuiltin extends BjorneBuiltin {
    private static final SyntaxBundle SYNTAX =
        new SyntaxBundle("return", new OptionalSyntax(new ArgumentSyntax("rc")));
    
    static final Factory FACTORY = new Factory() {
        public BjorneBuiltinCommandInfo buildCommandInfo(BjorneContext context) {
            return new BjorneBuiltinCommandInfo("return", SYNTAX, new ReturnBuiltin(), context);
        }
    };

    private final IntegerArgument argRC = new IntegerArgument(
            "rc", Argument.OPTIONAL, 1, Integer.MAX_VALUE, "the return code");
    
    
    private ReturnBuiltin() {
        super("Return from the current shell function call");
        registerArguments(argRC);
    }

    @Override
    public void execute() throws Exception {
        int rc = argRC.isSet() ? argRC.getValue() : 0;
        throw new BjorneControlException(BjorneInterpreter.BRANCH_RETURN, rc);
    }
}
