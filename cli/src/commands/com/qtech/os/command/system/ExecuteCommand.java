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
 
package com.qtech.os.command.system;

import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.Permission;
import java.security.Permissions;
import java.security.PrivilegedAction;
import java.security.Security;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import com.qtech.os.command.util.FileUtils;
import com.qtech.os.shell.AbstractCommand;
import com.qtech.os.shell.syntax.Argument;
import com.qtech.os.shell.syntax.FileArgument;
import com.qtech.os.shell.syntax.StringArgument;

/**
 * Halts the system
 * 
 * @author Ewout Prangsma (epr@users.sourceforge.net)
 */
public class ExecuteCommand extends AbstractCommand {

    private static final String HELP_SUPER = "Execute a executable jar file.";
    private static final String HELP_JAR = "URL to .jar file.";
    private static final String HELP_ARGS = "Arguments to execute jar file.";
    private static final String err_not_static = "The 'main' method for this class is not static";
    private static final String err_not_public = "The 'main' method for this class is not public";
    private static final String err_no_class = "Cannot find the requested class: %s%n";
    private static final String err_no_method = "Cannot find a 'void main(String[])' method for %s%n";
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

    @SuppressWarnings("deprecation")
    @Override
    public void execute() {
        File file = argJar.getValue();
        final PrintWriter err = getError().getPrintWriter(true);
        PrintWriter out = getOutput().getPrintWriter(true);
//        if (!FileUtils.isValidJar(file)) {
//            err.println("Invalid Jar file.");
//            return;
//        }

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
            if (attributes.isEmpty()) {
                err.println("Main attributes are empty.");
                return;
            }

            String className = attributes.getValue(Attributes.Name.MAIN_CLASS);

            // Close jar file.
            jarFile.close();

            // Build our classloader
            final ClassLoader parent_cl = Thread.currentThread().getContextClassLoader();
            ClassLoader cl = URLClassLoader.newInstance(new URL[]{argJar.getValue().toURL()}, parent_cl);
            JCClassLoader cl1 = new JCClassLoader(parent_cl, new String[]{"./"});

            final Method[] mainMethod = new Method[1];
            try {
                // Find (if necessary load) the class to be executed.
                final Class<?> cls = cl.loadClass(className);
                boolean flag = AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
                    public Boolean run() {
                        // Lookup and check the 'main' method.
                        try {
                            mainMethod[0] = cls.getMethod("main", String[].class);
                            if ((mainMethod[0].getModifiers() & Modifier.STATIC) == 0) {
                                err.println(err_not_static);
                                exit(1);
                            }
                            if ((mainMethod[0].getModifiers() & Modifier.PUBLIC) == 0) {
                                err.println(err_not_public);
                                exit(1);
                            }

                            // Disable access checking so that we can execute the method
                            // is the class is not 'public'.  (Strangely, Sun's 'java' command
                            // allows you to run a non-public class. So we should allow this too.)
                            mainMethod[0].setAccessible(true);

                            String[] mainArgs = argArgs.isSet() ? argArgs.getValues() : new String[0];
                            mainMethod[0].invoke(null, new Object[]{mainArgs});
                            return true;
                        } catch (Throwable t) {
                            try {
                                err.println("Error occurred when executing jar file");
                                err.println(t.toString());

                                File file1 = new File("/jnode/etc/command/" + getClass().getName() + "/error.log");
                                if (file1.exists()) {
                                    //noinspection ResultOfMethodCallIgnored
                                    file1.delete();
                                }
                                if (!file1.getParentFile().exists()) {
                                    //noinspection ResultOfMethodCallIgnored
                                    file1.getParentFile().mkdirs();
                                }
                                try {
                                    boolean newFileFlag = file1.createNewFile();
                                    if (!newFileFlag) {
                                        err.println("Couldn't create error.log");
                                        return false;
                                    }
                                } catch (Throwable s) {
                                    err.println(s.toString());
                                    return false;
                                }
                                err.println("Writing to file: " + file1.getAbsolutePath());
                                FileWriter writer = new FileWriter(file1);
                                PrintWriter out1 = new PrintWriter(writer);
                                t.printStackTrace(out1);
                                out1.flush();
                                writer.flush();
                                writer.close();
                                try {
                                    out1.close();
                                } catch (Throwable ignored) {

                                }
                                err.println("Error.log written to " + file1.getAbsolutePath());
                            } catch (IOException e) {
                                e.printStackTrace();
                                err.println("Could not create error log.");
                            }
                            return false;
                        }
                    }
                });
                if (!flag) {
                    exit(1);
                }
            } catch (ClassNotFoundException ex) {
                err.format(err_no_class, argArgs.getValue());
                exit(1);
            }
//            ClassLoader loader = URLClassLoader.newInstance(new URL[]{argJar.getValue().toURL()}, cl);
//            final Class<?> clazz;
//            try {
//                clazz = loader.loadClass(className);
//            } catch (ClassNotFoundException e) {
//                getError().getPrintWriter().println("Class not found occurred when loading main class.");
//                return;
//            }
//
//            RuntimePermission runtimePermission = new RuntimePermission("accessClassInPackage");
//
//            Method main = AccessController.doPrivileged(
//                new PrivilegedAction<Method>() {
//                    @Override
//                    public Method run() {
//                        Method method;
//                        try {
//                            method = clazz.getDeclaredMethod("main", String[].class);
//                            return method;
//                        } catch (NoSuchMethodException e) {
//                            ExecuteCommand.this.getError().getPrintWriter().println("Main method not found.");
//                            return null;
//                        }
//                    }
//                });
//
//            main.setAccessible(true);
//
//            if (main == null) {
//                return;
//            }
//
//            if (!Modifier.isPublic(main.getModifiers())) {
//                ExecuteCommand.this.getError().getPrintWriter()
//                    .println("Main method of main class is not public, exiting...");
//                return;
//            }
//            if (!Modifier.isStatic(main.getModifiers())) {
//                ExecuteCommand.this.getError().getPrintWriter()
//                    .println("Main method of main class is not static, exiting...");
//                return;
//            }
//
//            try {
//                if (argArgs.isSet()) {
//                    main.invoke(null, (Object) argArgs.getValues());
//                } else {
//                    main.invoke(null, (Object) new String[]{});
//                }
//            } catch (IllegalAccessException e) {
//                err.println("Could not access main method of main class, exiting...");
//            } catch (InvocationTargetException e) {
//                e.getCause().printStackTrace();
//            }
        } catch (Throwable t) {
            try {
                err.println("Error occurred when executing jar file");
                err.println(t.toString());

                File file1 = new File("/jnode/etc/command/" + getClass().getName() + "/error.log");
                if (file1.exists()) {
                    //noinspection ResultOfMethodCallIgnored
                    file1.delete();
                }
                if (!file1.getParentFile().exists()) {
                    //noinspection ResultOfMethodCallIgnored
                    file1.getParentFile().mkdirs();
                }
                try {
                    boolean newFileFlag = file1.createNewFile();
                    if (!newFileFlag) {
                        err.println("Couldn't create error.log");
                        return;
                    }
                } catch (Throwable s) {
                    err.println(s.toString());
                    return;
                }
                err.println("Writing to file: " + file1.getAbsolutePath());
                FileWriter writer = new FileWriter(file1);
                PrintWriter out1 = new PrintWriter(writer);
                t.printStackTrace(out1);
                out1.flush();
                writer.flush();
                writer.close();
                try {
                    out1.close();
                } catch (Throwable ignored) {

                }
                err.println("Error.log written to " + file1.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
                err.println("Could not create error log.");
            }
        }
    }

    /**
     * This class loader looks in the supplied list of directories after
     * checking the parent class loader.
     */
    private static class JCClassLoader extends ClassLoader {
        private String dirs[];

        public JCClassLoader(ClassLoader parent, String[] dir) {
            super(parent);
            this.dirs = dir;
        }

        public Class<?> findClass(String name) throws ClassNotFoundException {
            byte[] b = loadClassData(name);
            return defineClass(name, b, 0, b.length);
        }

        protected URL findResource(String name) {
            try {
                return findResource(name, dirs);
            } catch (MalformedURLException e) {
                return null;
            }
        }

        private URL findResource(String name, String[] dirs)
                throws MalformedURLException {
            findResource:
            while (true) {
                for (int i = 0; i < dirs.length; i++) {
                    File d = new File(dirs[i]);
                    if (d.isDirectory()) {
                        dirs = d.list();
                        continue findResource;
                    } else if (d.getName().equals(name)) {
                        return d.toURI().toURL();
                    }
                }
                return null;
            }
        }

        private byte[] loadClassData(String name) throws ClassNotFoundException {
            String fn = name.replace('.', '/');
            File f = null;
            for (String dir : dirs) {
                f = new File(dir + fn + ".class");
                if (f.exists()) {
                    break;
                }
                f = null;
            }
            if (f == null) {
                throw new ClassNotFoundException(name);
            }
            byte[] data = new byte[(int) f.length()];
            try {
                FileInputStream fis = new FileInputStream(f);
                fis.read(data);
                return data;
            } catch (IOException ex) {
                throw new ClassNotFoundException(name, ex);
            }
        }
    }
}
