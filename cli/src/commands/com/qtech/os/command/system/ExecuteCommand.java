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
 
package com.qtech.os.command.system;

import com.qtech.os.command.util.FileUtils;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import com.qtech.os.shell.AbstractCommand;
import com.qtech.os.shell.syntax.Argument;
import com.qtech.os.shell.syntax.FileArgument;
import com.qtech.os.shell.syntax.StringArgument;
import com.qtech.os.vm.classmgr.Modifier;

/**
 * Halts the system
 * 
 * @author Ewout Prangsma (epr@users.sourceforge.net)
 */
public class ExecuteCommand extends AbstractCommand {

    private static final String HELP_SUPER = "Execute a executable jar file.";
    private static final String HELP_JAR = "URL to .jar file.";
    private static final String HELP_ARGS = "Arguments to execute jar file.";
    private final FileArgument argJar;
    private final StringArgument argArgs;

    public ExecuteCommand() {
        super(HELP_SUPER);

        argJar = new FileArgument("jar", Argument.MANDATORY, HELP_JAR);
        argArgs  = new StringArgument("arg", Argument.OPTIONAL | Argument.MULTIPLE, HELP_ARGS);
        registerArguments(argJar, argArgs);
    }

    public static void main(String[] args) throws Exception {
        new ExecuteCommand().execute(args);
    }

    @Override
    public void execute() {
        File file = argJar.getValue();
        PrintWriter err = getError().getPrintWriter(true);
        if (!FileUtils.isValidJar(file)) {
            err.println("Invalid Jar file.");
            return;
        }

        try {
            // Loading jar file.
            JarFile jarFile = new JarFile(argJar.getValue());

            // Loading manifest.
            Manifest manifest = jarFile.getManifest();
            if (manifest == null) {
                getError().getPrintWriter().println("Jar has no manifest, exiting...");
                return;
            }

            // Reading main class attribute.
            Attributes attributes = jarFile.getManifest().getMainAttributes();
            String mainClassName = attributes.getValue(Attributes.Name.MAIN_CLASS);

            // Close jar file.
            jarFile.close();

            ClassLoader loader = URLClassLoader.newInstance(
                new URL[]{argJar.getValue().toURL()},
                getClass().getClassLoader()
            );
            Class<?> clazz;
            try {
                clazz = loader.loadClass(mainClassName);
            } catch (ClassNotFoundException e) {
                getError().getPrintWriter().println("Class not found occurred when loading main class.");
                return;
            }

            Method main;
            try {
                main = clazz.getDeclaredMethod("main", String[].class);
            } catch (NoSuchMethodException e) {
                getError().getPrintWriter().println("Main method not found.");
                return;
            }
            if (!Modifier.isPublic(main.getModifiers())) {
                getError().getPrintWriter().println("Main method of main class is not public, exiting...");
                return;
            }
            if (!Modifier.isStatic(main.getModifiers())) {
                getError().getPrintWriter().println("Main method of main class is not static, exiting...");
                return;
            }

            try {
                if (argArgs.isSet()) {
                    main.invoke(null, (Object) argArgs.getValues());
                } else {
                    main.invoke(null, (Object) new String[]{});
                }
            } catch (IllegalAccessException e) {
                err.println("Could not access main method of main class, exiting...");
            } catch (InvocationTargetException e) {
                e.getCause().printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Throwable t) {
            t.printStackTrace();;
        }
    }
}
