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
 
package com.qtech.os.emu;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;

import javax.naming.NamingException;

import com.qtech.os.emu.naming.BasicNameSpace;
import com.qtech.os.emu.plugin.model.DummyExtensionPoint;
import com.qtech.os.naming.InitialNaming;
import com.qtech.os.nanoxml.XMLElement;
import com.qtech.os.shell.ShellManager;
import com.qtech.os.shell.alias.AliasManager;
import com.qtech.os.shell.alias.def.DefaultAliasManager;
import com.qtech.os.shell.def.DefaultShellManager;
import com.qtech.os.shell.help.HelpFactory;
import com.qtech.os.shell.help.def.DefaultHelpFactory;
import com.qtech.os.shell.syntax.DefaultSyntaxManager;
import com.qtech.os.shell.syntax.SyntaxBundle;
import com.qtech.os.shell.syntax.SyntaxManager;
import com.qtech.os.shell.syntax.SyntaxSpecAdapter;
import com.qtech.os.shell.syntax.SyntaxSpecLoader;
import com.qtech.os.shell.syntax.XMLSyntaxSpecAdapter;

/**
 * Emu is the core of a light-weight JNode emulator that allows (some) JNode
 * applications to be run using a classic JVM in the context of a JNode development sandbox.
 * <p>
 * An Emu is also a large flightless bird ... which seems kind of appropriate.
 * 
 * @author Levente S\u00e1ntha
 * @author crawley@jnode.org
 */
public class Emu {
    // FIXME configuring a hard-coded list of projects is a bad idea.
    private static final String[] ALL_PROJECTS = new String[]{
        "cli", "core", "distr", "fs", "gui", "net", "shell", "sound", "textui"
    };

    // FIXME configuring a hard-coded list of command plugins is a bad idea.
    private static final String[] DEFAULT_PLUGIN_IDS = new String[] {
        "com.qtech.os.command.archive",
        "com.qtech.os.command.common",
        "com.qtech.os.command.dev.ant",
        "com.qtech.os.command.dev",
        "com.qtech.os.command.file",
        "com.qtech.os.command.net",
        "com.qtech.os.command.system",
        "com.qtech.os.command.util",
        "com.qtech.os.shell.command.driver.console",
        "com.qtech.os.apps.editor",
        "com.qtech.os.apps.edit",
        "com.qtech.os.apps.commander",
        "com.qtech.os.apps.console",
    };
    
    private final File root;
    private final AliasManager aliasMgr;
    private final SyntaxManager syntaxMgr;
    private final HashSet<String> configuredPlugins = new HashSet<String>();
    
    public Emu(File root) throws EmuException {
        this(root, DEFAULT_PLUGIN_IDS);
    }

    /**
     * The constructor initializes a minimal subset of JNode services to allow us to run JNode commands.
     *
     * @param root the notional JNode sandbox root directory or <code>null</code>.
     * @throws EmuException
     */
    public Emu(File root, String[] pluginNames) throws EmuException {
        if (root == null) {
            root = new File("").getAbsoluteFile();
            System.err.println("Assuming that the QTech OS root is '" + root + "'");
        }
        this.root = root;
        InitialNaming.setNameSpace(new BasicNameSpace());

        try {
            InitialNaming.bind(DeviceManager.NAME, DeviceManager.INSTANCE);
            aliasMgr = new DefaultAliasManager(new DummyExtensionPoint()).createAliasManager();
            syntaxMgr = new DefaultSyntaxManager(new DummyExtensionPoint()).createSyntaxManager();
            for (String pluginName : pluginNames) {
                configurePlugin(pluginName);
            }
            System.setProperty("jnode.invoker", "thread");
            System.setProperty("jnode.interpreter", "redirecting");
            System.setProperty("jnode.debug", "true");
            InitialNaming.bind(AliasManager.NAME, aliasMgr);
            InitialNaming.bind(ShellManager.NAME, new DefaultShellManager());
            InitialNaming.bind(SyntaxManager.NAME, syntaxMgr);
            InitialNaming.bind(HelpFactory.NAME, new DefaultHelpFactory());
        } catch (NamingException ex) {
            throw new EmuException("Problem setting up InitialNaming bindings", ex);
        }
    }

    /**
     * Configure any command classes specified by a given plugin's descriptor
     *
     * @param pluginId the plugin to be processed
     * @throws EmuException
     */
    public void configurePlugin(String pluginId) throws EmuException {
        if (!configuredPlugins.contains(pluginId)) {
            XMLElement pluginDescriptor = loadPluginDescriptor(pluginId);
            extractAliases(pluginDescriptor);
            extractSyntaxBundles(pluginDescriptor);
            configuredPlugins.add(pluginId);
        }
    }

    /**
     * Populate the supplied syntax manager with syntax entries from a plugin descriptor.
     *
     * @param pluginDescriptor the plugin descriptor's root XML element
     * @throws EmuException
     */
    private void extractSyntaxBundles(XMLElement pluginDescriptor)
        throws EmuException {
        XMLElement syntaxesDescriptor = findExtension(pluginDescriptor, SyntaxManager.SYNTAXES_EP_NAME);
        if (syntaxesDescriptor == null) {
            return;
        }
        SyntaxSpecLoader loader = new SyntaxSpecLoader();
        for (XMLElement syntaxDescriptor : syntaxesDescriptor.getChildren()) {
            if (!syntaxDescriptor.getName().equals("syntax")) {
                continue;
            }
            SyntaxSpecAdapter adaptedElement = new XMLSyntaxSpecAdapter(syntaxDescriptor);
            try {
                SyntaxBundle bundle = loader.loadSyntax(adaptedElement);
                if (bundle != null) {
                    syntaxMgr.add(bundle);
                }
            } catch (Exception ex) {
                throw new EmuException("problem in syntax", ex);
            }
        }
    }

    /**
     * Populate the supplied alias manager with aliases from a plugin descriptor.
     *
     * @param pluginDescriptor the plugin descriptor's root XML element
     * @throws EmuException
     */
    private void extractAliases(XMLElement pluginDescriptor) {
        XMLElement aliasesDescriptor = findExtension(pluginDescriptor, AliasManager.ALIASES_EP_NAME);
        if (aliasesDescriptor == null) {
            return;
        }
        for (XMLElement aliasDescriptor : aliasesDescriptor.getChildren()) {
            if (aliasDescriptor.getName().equals("alias")) {
                String alias = aliasDescriptor.getStringAttribute("name");
                String className = aliasDescriptor.getStringAttribute("class");
                aliasMgr.add(alias, className);
            }
        }
    }

    /**
     * Locate the descriptor for a given extension point.
     *
     * @param pluginDescriptor the plugin descriptor
     * @param epName           the extension point's name
     * @return
     */
    private XMLElement findExtension(XMLElement pluginDescriptor, String epName) {
        for (XMLElement child : pluginDescriptor.getChildren()) {
            if (child.getName().equals("extension") &&
                epName.equals(child.getStringAttribute("point"))) {
                return child;
            }
        }
        return null;
    }

    /**
     * Locate and load a plugin descriptor.  We search the "descriptors" directory of
     * each of the projects listed in ALL_PROJECTS
     *
     * @param pluginName the name of the plugin we're trying to locate
     * @return the loaded plugin descriptor or <code>null</code>
     * @throws EmuException
     */
    private XMLElement loadPluginDescriptor(String pluginName)
        throws EmuException {
        File file = null;
        for (String projectName : ALL_PROJECTS) {
            file = new File(new File(new File(root, projectName), "descriptors"), pluginName + ".xml");
            if (file.exists()) {
                break;
            }
        }
        if (!file.exists()) {
            throw new EmuException("Cannot find plugin descriptor file for '" +
                pluginName + "': " + file.getAbsolutePath());
        }
        BufferedReader br = null;
        try {
            XMLElement elem = new XMLElement();
            br = new BufferedReader(new FileReader(file));
            elem.parseFromReader(br);
            if (!elem.getName().equals("plugin")) {
                throw new EmuException("File does not contain a 'plugin' descriptor: " + file);
            }
            return elem;
        } catch (IOException ex) {
            throw new EmuException("Problem reading / parsing plugin descriptor file " + file, ex);
        } finally {
            try {
                br.close();
            } catch (IOException ex) {
                // ignore
            }
        }
    }
}
