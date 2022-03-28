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
 
package org.jnode.build.packager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.jnode.build.BuildException;

/**
 * Build a jnode scripts from both a linux/unix scripts, from msdos scripts or, if none is found,
 * from scratch (with the help of {@link MainFinder}).
 * 
 * @author fabien
 *
 */
public class ScriptBuilder extends PackagerTask {
    private static final String SET_COMMAND = "propset ";
    private static final String JAVA = "java ";
    private static final String DEFINE_SYS_PROPERTY = "-D";
    
    /**
     * jnode script extension.
     */
    private static final String JNODE_SCRIPT = ".jns";
    
    /**
     * Main method : search for existing (unix/linux, msdos) scripts and build jnode scripts
     * for launching the application given by its root directory.
     * 
     * @param applicationDir
     * @param properties
     */
    public static void build(File applicationDir, Properties properties) {
        List<Command> commands = new ArrayList<Command>();
        
        try {
            // build from unix scripts 
            if (!buildFromScripts(applicationDir, ".sh", "#", ":", commands)) {
                
                // build from msdos batches
                if (!buildFromScripts(applicationDir, ".bat", "REM", ";", commands)) {
                    
                    // build from scratch by searching for main classes in jars
                    buildFromScratch(applicationDir, commands);
                }
            }
            
            boolean overwriteScripts = "true".equals(properties.getProperty(FORCE_OVERWRITE_SCRIPTS));
            for (Command cmd : commands) {
                final File file = cmd.getScriptFile();
                
                if (!file.exists() || overwriteScripts) {
                    build(applicationDir, cmd, file);
                }
            }
        } catch (Throwable t) {
            throw new BuildException("failed to build scripts", t);
        }
    }

    /**
     * Build a jnode script for the given {@link Command} and application directory.
     * The generated script will be written in the given file.
     * 
     * @param applicationDir
     * @param cmd
     * @param file
     * @throws IOException
     */
    private static void build(File applicationDir, Command cmd, File file) throws IOException {
        //FIXME shouldn't be hard coded but use java.home system property instead
        // also see class AutoUnzipPlugin, which has the same problem.
        // In our case, we might use something like ${java.home} 
        // but that feature is not yet support by the shell
        final File jnodeHome = new File("/jnode/");
        
        FileWriter fw = null;            
        boolean success = false;
        try {
            fw = new FileWriter(file);
            fw.write("# File automatically generated by JNode Packager\n");
            fw.write("\n");

            fw.write("# enable exception tracing\n");
            fw.write(SET_COMMAND + "jnode.debug true\n");
            fw.write("\n");
            
            fw.write("# set system properties\n");                
            final Map<String, String> properties = cmd.getSystemProperties();
            for (String name : properties.keySet()) {
                fw.write(SET_COMMAND + name + " " + properties.get(name) + "\n");
            }
            fw.write("\n");
            

            fw.write("# set classpath\n");           
            final String prefix = "classpath --add file://" + jnodeHome + "/" + 
                cmd.getApplicationName() + "/"; 
            for (String jarPath : cmd.getClasspath()) {
                fw.write(prefix + jarPath + "\n");
            }
            fw.write("\n");
            
            fw.write("# TODO : add permissions\n");
            fw.write("\n");
            
            fw.write("# launch the application\n");                
            fw.write("java " + cmd.getMainClass());
            for (String arg : cmd.getArguments()) {
                fw.write(" " + arg);    
            }
            fw.write("\n\n");
            
            success = true;
        } finally {
            if (fw != null) {
                fw.close();
            }
                            
            if (!success) {
                // in case of failure, delete the incomplete script
                file.delete();
            }                
        }
    }

    /**
     * 
     * @param applicationDir
     * @param commands
     * @throws IOException
     */
    private static void buildFromScratch(File applicationDir, List<Command> commands) throws IOException {
        final List<String> jars = new ArrayList<String>();
        final List<String> mains = new ArrayList<String>();
        final int rootLength = (applicationDir.getAbsolutePath() + File.separator).length();
        searchJars(rootLength, applicationDir, jars, mains);
        
        for (String main : mains) {
            int idx = main.lastIndexOf('.') + 1;
            File file = new File(applicationDir, main.substring(idx) + JNODE_SCRIPT);            
            
            
            Command cmd = new Command(main, file);
            for (String jar : jars) {
                cmd.addToClasspath(jar);
            }
            addCommand(commands, cmd, applicationDir);
        }
    }

    private static void searchJars(int rootLength, File currentDir, List<String> jars, 
            List<String> mains) throws IOException {
        File[] files = currentDir.listFiles(new FileFilter() {

            @Override
            public boolean accept(File pathname) {
                return pathname.getName().endsWith(".jar") || pathname.isDirectory();
            }
            
        });

        for (File file : files) {
            if (file.isDirectory()) {
                // recursively search in sub directory 
                searchJars(rootLength, file, jars, mains);
            } else {
                // add the jar and its main classes
                String relativePath = file.getAbsolutePath().substring(rootLength);
                jars.add(relativePath);
                
                mains.addAll(MainFinder.searchMain(file));
            }
        }        
    }

    /**
     * Search for scripts with given extension, which uses the given comment, pathSeparator conventions
     * (depending on provided parameters, it can be msdos, linux/unix conventions or anything else).
     * Each found script, will be scanned for a "java" command line and will be used to build a new 
     * {@link Command} that will be added to the provided list.
     * 
     * @param applicationDir directory of the application
     * @param extension script's extension
     * @param comment {@link String} used for comments in the script (example for unix/linux : "#")
     * @param pathSeparator path separator for the classpath (example for unix/linux : ":")
     * @param commands list in which new commands will be added
     * @return
     * @throws IOException
     */
    private static boolean buildFromScripts(File applicationDir, final String extension, 
            String comment, String pathSeparator, List<Command> commands) throws IOException {
        File[] scripts = applicationDir.listFiles(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(extension);
            }
            
        });

        for (File script : scripts) {
            buildFromScript(applicationDir, script, extension, comment, pathSeparator, commands);
        }
            
        return (scripts.length > 0);
    }

    /**
     * Build a command from the given script file and add it to the provided list. 
     * 
     * @param applicationDir
     * @param script
     * @param extension
     * @param comment
     * @param pathSeparator
     * @param commands
     * @throws IOException
     */
    private static void buildFromScript(File applicationDir, File script, String extension, 
            String comment, String pathSeparator, List<Command> commands) throws IOException {
        String line;
        FileReader fr = null;
        BufferedReader br = null;
        
        try {
            fr = new FileReader(script);
            br = new BufferedReader(fr);
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty() && !line.startsWith(comment)) {
                    int idx = line.indexOf(JAVA);
                    if (idx >= 0) {
                        line = line.substring(idx + JAVA.length());
                        String[] tokens = line.split(" ");
                        
                        // build the Command from the line
                        Command cmd = buildCommand(script, extension, pathSeparator, tokens);
                        addCommand(commands, cmd, applicationDir);
                        
                        break;
                    }
                }
            }
        } finally {
            if (br != null) {
                br.close();
            }
            
            if (fr != null) {
                fr.close();
            }
        }
    }
    
    /**
     * Complete the command fill in and do additional checks regarding existing commands
     * in the list. 
     * 
     * @param commands
     * @param cmd
     * @param applicationDir
     */
    private static void addCommand(List<Command> commands, Command cmd, File applicationDir) {
        cmd.setApplicationName(applicationDir.getName());
        commands.add(cmd);
    }

    /**
     * Build a new command from the given parameters.
     * @param script
     * @param extension
     * @param pathSeparator
     * @param tokens
     * @return
     */
    private static Command buildCommand(File script, String extension, String pathSeparator, String[] tokens) {
        Command cmd = new Command();
        
        for (int i = 0; i < tokens.length; i++) {
            String token = tokens[i];
            if (token.startsWith("-")) {
                if ("-cp".equals(token) || "-classpath".equals(token)) {
                    token = tokens[++i];
                    
                    // split the classpath
                    for (String path : token.split(pathSeparator)) {
                        cmd.addToClasspath(path);
                    }
                } else if (token.startsWith(DEFINE_SYS_PROPERTY)) {
                    int beginName = DEFINE_SYS_PROPERTY.length();
                    int equalPosition = token.indexOf("=", beginName + 1);                                
                    cmd.addSystemProperty(token.substring(beginName, equalPosition), 
                            token.substring(equalPosition + 1));
                }
            } else {
                cmd.setMainClass(token);
                                            
                // add command arguments
                for (int j = (i + 1); j < tokens.length; j++) {
                    cmd.addArgument(tokens[j]);
                }
                
                String path = script.getAbsolutePath();
                path = path.substring(0, path.length() - extension.length());
                path += JNODE_SCRIPT;
                cmd.setScriptFile(new File(path));
                break;
            }
        }
        
        return cmd;
    }

    /**
     * That class is used to represent a "java" command in a script.
     * It contains what's needed to build a classpath, define system properties 
     * and launch the main class. 
     *  
     * @author fabien
     *
     */
    private static class Command {
        private final List<String> classpath = new ArrayList<String>();
        private final Map<String, String> systemProperties = new HashMap<String, String>(); 
        private final List<String> arguments = new ArrayList<String>();
        
        private String applicationName;
        private String mainClass;
        private File scriptFile;
        
        public Command() {
        }
        
        public Command(String mainClass, File scriptFile) {
            this.mainClass = mainClass;
            this.scriptFile = scriptFile;
        }
                
        public void setScriptFile(File scriptFile) {
            this.scriptFile = scriptFile;
        }
        
        public File getScriptFile() {        
            return scriptFile;
        }

        public void addArgument(String arg) {
            arguments.add(arg);
        }

        public List<String> getArguments() {
            return arguments;
        }

        public void addSystemProperty(String name, String value) {
            systemProperties.put(name, value);            
        }

        public Map<String, String> getSystemProperties() {
            return systemProperties;            
        }

        public void setMainClass(String mainClass) {
            this.mainClass = mainClass;
        }

        public void addToClasspath(String path) {
            classpath.add(path);
        }

        public String getMainClass() {
            return mainClass;
        }

        public List<String> getClasspath() {
            return classpath;
        }

        public String getApplicationName() {
            return applicationName;
        }

        public void setApplicationName(String applicationName) {
            this.applicationName = applicationName;
        }
        
        
    }
}
