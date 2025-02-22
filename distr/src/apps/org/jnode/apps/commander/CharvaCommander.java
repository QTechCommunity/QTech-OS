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
 
package org.jnode.apps.commander;

import charva.awt.BorderLayout;
import charva.awt.Color;
import charva.awt.EventQueue;
import charva.awt.Insets;
import charva.awt.Point;
import charva.awt.Toolkit;
import charva.awt.event.ActionEvent;
import charva.awt.event.ActionListener;
import charva.awt.event.KeyEvent;
import charva.awt.event.ScrollEvent;
import charvax.swing.JButton;
import charvax.swing.JFrame;
import charvax.swing.JLabel;
import charvax.swing.JList;
import charvax.swing.JOptionPane;
import charvax.swing.JPanel;
import charvax.swing.JScrollPane;
import charvax.swing.border.TitledBorder;
import charvax.swing.event.ListSelectionEvent;
import charvax.swing.event.ListSelectionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 * Charva based file manager.
 *
 * @author Levente S\u00e1ntha
 */
public class CharvaCommander extends JFrame {
    private static final Color BACKGROUND_COLOR = Color.black;
    private static final Color FOREGROUND_COLOR = Color.cyan;
    private static final Color HIGHLIGHT_COLOR = Color.yellow;
    private Pane leftPane;
    private Pane rightPane;
    private boolean exitted;

    /**
     * Startup method.
     *
     * @param argv ignored
     */
    public static void main(String[] argv) {
        CharvaCommander cmd = null;
        try {
            Toolkit.getDefaultToolkit().register();
            cmd = new CharvaCommander();
            cmd.setVisible(true);
            //Toolkit.getDefaultToolkit().waitTillFinished();
        } catch (Exception e) {
            e.printStackTrace();
            if (cmd != null)
                cmd.exit();
            else
                Toolkit.getDefaultToolkit().close();
        }
    }

    private CharvaCommander() {
        this._insets = new Insets(0, 0, 0, 0);
        setBackground(BACKGROUND_COLOR);
        setForeground(FOREGROUND_COLOR);
        JPanel content = (JPanel) getContentPane();
        new ButtonPanel();

        JPanel panel = new JPanel();
        content.add(panel, BorderLayout.CENTER);
        panel.setLayout(null);

        File startf = new File(".");
        String path = startf.getAbsolutePath();
        try {
            path = startf.getCanonicalPath();
        } catch (IOException e) {
            //ignore
        }

        leftPane = new Pane(38, 20);
        leftPane.setPath(path);
        leftPane.setLocation(0, 0);
        panel.add(leftPane);
        leftPane.label.setLocation(1, 22);
        panel.add(leftPane.label);

        rightPane = new Pane(38, 20);
        rightPane.setPath(path);
        rightPane.setLocation(40, 0);
        panel.add(rightPane);
        rightPane.label.setLocation(41, 22);
        panel.add(rightPane.label);

        setLocation(0, 0);
        setSize(80, 24);
        validate();

        leftPane.list.requestFocus();
        leftPane.border.setTitleColor(HIGHLIGHT_COLOR);
    }

    private class ButtonPanel extends JPanel implements ActionListener {
        {
            getContentPane().add(this, BorderLayout.SOUTH);
        }

        //final JButton help = createButton("1 Help", KeyEvent.VK_F1);
        final JButton mkfile = createButton("F2 New", KeyEvent.VK_F2);
        final JButton view = createButton("F3 View", KeyEvent.VK_F3);
        final JButton edit = createButton("F4 Edit", KeyEvent.VK_F4);
        final JButton copy = createButton("F5 Copy", KeyEvent.VK_F5);
        final JButton move = createButton("F6 Move", KeyEvent.VK_F6);
        final JButton mkdir = createButton("F7 MkDir", KeyEvent.VK_F7);
        final JButton delte = createButton("F8 Delete", KeyEvent.VK_F8);
        final JButton chdir = createButton("F9 ChDir", KeyEvent.VK_F9);
        final JButton exit = createButton("F10 Exit", KeyEvent.VK_F10);

        JButton createButton(String title, int accel) {
            JButton b = new MyButton(title);
            this.add(b);
            b.setMnemonic(accel);
            b.addActionListener(this);
            return b;
        }

        public void actionPerformed(ActionEvent ae) {
            try {
                Object source = ae.getSource();
                //if (source == help) {
                //} else
                if (source == mkfile) {
                    mkfile();
                } else if (source == view) {
                    view();
                } else if (source == edit) {
                    edit();
                } else if (source == copy) {
                    copy();
                } else if (source == move) {
                    move();
                } else if (source == mkdir) {
                    mkdir();
                } else if (source == delte) {
                    delete();
                } else if (source == chdir) {
                    chdir();
                } else if (source == exit) {
                    exit();
                }
            } finally {
                if (!exitted)
                    leftPane.requestFocus();
            }
        }
    }

    private synchronized void move() {
        String sel = leftPane.getSelection();
        if (sel == null)
            return;

        int conf = JOptionPane
            .showConfirmDialog(this, sel + " to " + rightPane.getPath(), "Move ?", JOptionPane.YES_NO_OPTION);
        if (conf != JOptionPane.YES_OPTION)
            return;

        int index = leftPane.list.getSelectedIndex();
        try {
            File source = new File(leftPane.getPath(), sel);
            if (copyRec(source, new File(rightPane.getPath(), sel), true))
                delete0(source);
        } finally {
            leftPane.setPath(leftPane.getPath());
            leftPane.list.setCurrentRow(index);
            leftPane.repaint();
            rightPane.setPath(rightPane.getPath(), sel);
            rightPane.repaint();
        }
    }

    private synchronized void view() {
        String sel = leftPane.getSelection();
        if (sel == null)
            return;

        File f = new File(leftPane.getPath(), sel);
        if (!f.isFile())
            return;

        callViewer(f);
    }

    private synchronized void mkfile() {
        String str = JOptionPane.showInputDialog(this, "File name: ", "New file", JOptionPane.OK_CANCEL_OPTION);
        if (str == null || str.trim().length() == 0)
            return;

        File file = new File(leftPane.getPath(), str);
        if (file.exists()) {
            int conf = JOptionPane
                .showConfirmDialog(this, file.getName(), "Continue? File already exits:", JOptionPane.YES_NO_OPTION);
            if (conf != JOptionPane.YES_OPTION)
                return;
        } else {
            try {
                file.createNewFile();
            } catch (IOException ioe) {
                JOptionPane.showMessageDialog(this, "Could not create file: " + file.getName());
                return;
            }
        }

        callEditor(file);
        leftPane.setPath(leftPane.getPath(), file.getName());
        leftPane.repaint();
    }

    private void callViewer(File file) {
        try {
            Class.forName("org.jnode.apps.editor.TextEditor").getMethod("main", String[].class).
                invoke(null, new Object[]{new String[]{file.getAbsolutePath(), "ro"}});
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this, "Viewer not found", "Warning", JOptionPane.DEFAULT_OPTION);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.DEFAULT_OPTION);
        }

    }

    private synchronized void edit() {
        String sel = leftPane.getSelection();
        if (sel == null)
            return;

        File f = new File(leftPane.getPath(), sel);
        if (!f.isFile())
            return;

        callEditor(f);
    }

    private void callEditor(File file) {
        try {
            Class.forName("org.jnode.apps.editor.TextEditor").getMethod("main", String[].class).
                invoke(null, new Object[]{new String[]{file.getAbsolutePath()}});
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this, "Editor not found", "Warning", JOptionPane.DEFAULT_OPTION);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.DEFAULT_OPTION);
        }
    }

    private synchronized void chdir() {
        String str =
            JOptionPane.showInputDialog(this, "Path to directory: ", "Change directory", JOptionPane.OK_CANCEL_OPTION);
        if (str == null || str.trim().length() == 0)
            return;

        File f = str.charAt(0) == '/' ? new File(str) :
            new File(leftPane.getPath(), str);

        if (!f.exists())
            JOptionPane.showMessageDialog(this, f.getAbsolutePath(), "Invalid directory", JOptionPane.DEFAULT_OPTION);
        else {
            if (!f.isDirectory())
                f = f.getParentFile();

            leftPane.setPath(f.getAbsolutePath());
            leftPane.repaint();
        }
    }

    private synchronized void delete() {
        String sel = leftPane.getSelection();
        if (sel == null)
            return;

        int conf = JOptionPane.showConfirmDialog(this, sel, "Delete ?", JOptionPane.YES_NO_OPTION);
        if (conf != JOptionPane.YES_OPTION)
            return;

        File f = new File(leftPane.getPath(), sel);
        if (f.isDirectory() && f.list().length > 0) {
            conf = JOptionPane
                .showConfirmDialog(this, "Directory is not empty: " + sel, "Delete ?", JOptionPane.YES_NO_OPTION);
            if (conf != JOptionPane.YES_OPTION)
                return;
        }

        int index = leftPane.list.getSelectedIndex();
        try {
            delete0(new File(leftPane.getPath(), sel));
        } finally {
            leftPane.setPath(leftPane.getPath());
            leftPane.list.setCurrentRow(index);
            leftPane.repaint();
        }
    }

    private void delete0(File f) {
        if (f.isDirectory())
            for (String s : f.list())
                delete0(new File(f.getAbsolutePath(), s));

        f.delete();
    }

    @SuppressWarnings("unused")
    private void notImplemented() {
        JOptionPane.showMessageDialog(this, "NOT IMPLEMENTED!", null, JOptionPane.DEFAULT_OPTION);
        leftPane.requestFocus();
    }

    private synchronized void copy() {
        String sel = leftPane.getSelection();
        if (sel == null)
            return;

        int conf = JOptionPane
            .showConfirmDialog(this, sel + " to " + rightPane.getPath(), "Copy ?", JOptionPane.YES_NO_OPTION);
        if (conf != JOptionPane.YES_OPTION)
            return;

        try {
            copyRec(new File(leftPane.getPath(), sel), new File(rightPane.getPath(), sel), true);
        } finally {
            rightPane.setPath(rightPane.getPath(), sel);
            rightPane.repaint();
        }
    }

    private boolean copyRec(File from, File to, boolean ask) {
        if (from.isFile()) {
            return copy0(from, to, ask);
        } else if (from.isDirectory()) {
            if (ask && to.exists()) {
                int conf = JOptionPane.showConfirmDialog(this, to.toString(), "Overwrite ?", JOptionPane.YES_NO_OPTION);
                if (conf != JOptionPane.YES_OPTION)
                    return false;
            } else
                to.mkdir();

            for (String name : from.list())
                if (!copyRec(new File(from.getAbsolutePath(), name), new File(to.getAbsolutePath(), name), false))
                    return false;

            return true;
        } else {
            JOptionPane.showMessageDialog(this, "Unsupported file type in source.");
            return false;
        }
    }

    private boolean copy0(File from, File to, boolean ask) {
        if (from.equals(to)) {
            JOptionPane.showMessageDialog(this, "Source and destination files are the same.");
            return false;
        }
        if (ask && to.exists()) {
            int conf = JOptionPane.showConfirmDialog(this, to.toString(), "Overwrite ?", JOptionPane.YES_NO_OPTION);
            if (conf != JOptionPane.YES_OPTION)
                return false;
        }
        try {
            copy0(new FileInputStream(from), new FileOutputStream(to));
            return true;
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.OK_OPTION);
            to.delete();
            return false;
        }
    }

    private void copy0(FileInputStream in, FileOutputStream out) throws IOException {
        try {
            byte[] buf = new byte[8 * 1024];
            int c;
            while ((c = in.read(buf)) > -1)
                out.write(buf, 0, c);
            out.flush();
        } finally {
            in.close();
            out.close();
        }
    }

    private synchronized void mkdir() {
        String str = JOptionPane.showInputDialog(this, "Directory name: ", "Create directory",
            JOptionPane.OK_CANCEL_OPTION);
        if (str != null && str.trim().length() > 0) {
            File file = new File(leftPane.getPath(), str);
            file.mkdir();
            leftPane.setPath(leftPane.getPath(), "/" + file.getName());
            leftPane.repaint();
        }
    }

    private synchronized void exit() {
        exitted = true;
        hide();
        Toolkit.getDefaultToolkit().close();
    }

    private static void format(String str, int limit, StringBuilder sb) {
        if (str.length() > limit) {
            int half = limit / 2;
            if (2 * half < limit) {
                str = str.substring(0, half) + '~' + str.substring(str.length() - half);
            } else {
                str = str.substring(0, half) + '~' + str.substring(str.length() - half + 1);
            }
        }
        sb.append(str);
        int ln = str.length();
        while (ln++ < limit) {
            sb.append(' ');
        }
    }

    private static class ListDataEntry implements Comparable {
        final File file;
        final String name;
        private final String text;

        ListDataEntry(File file, String text) {
            this.file = file;
            this.name = text;
            this.text = text;
        }

        ListDataEntry(File file) {
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm");
            char vline = (char) Toolkit.ACS_VLINE;
            this.file = file;
            this.name = (file.isDirectory() ? "/" : "") + file.getName();
            StringBuilder text = new StringBuilder();
            text.append((file.isDirectory() ? '/' : ' '));
            CharvaCommander.format(file.getName(), 15, text);
            text.append(vline);
            format(file.length(), text);
            text.append(vline);
            text.append(df.format(new Date(file.lastModified())));
            this.text = text.toString();
        }

        private static final char[] UNITS = {'B', 'K', 'M', 'G', 'T', 'P', 'E', 'Z', 'Y'};

        private void format(long size, StringBuilder sb) {
            String s = String.valueOf(size);
            int index = 0;
            while (s.length() > 5) {
                size /= 1024;
                s = String.valueOf(size);
                index++;
            }
            int ln = s.length();
            while (ln++ < 5) {
                sb.append(' ');
            }
            sb.append(s);
            sb.append(UNITS[index]);
        }

        public String toString() {
            return text;
        }

        @Override
        public int compareTo(Object o) {
            ListDataEntry other = (ListDataEntry) o;
            boolean thisDirectory = this.file.isDirectory();
            if (!(thisDirectory ^ other.file.isDirectory())) {
                return name.compareTo(other.name);
            } else {
                return thisDirectory ? -1 : 1;
            }
        }
    }

    private class Pane extends JScrollPane implements ListSelectionListener {
        String path;
        MyList list;
        TitledBorder border;
        JLabel label = new JLabel();
        int w;
        int h;

        Pane(int w, int h) {
            this.w = w;
            this.h = h;
            border = new TitledBorder("");
            border.setTitleColor(FOREGROUND_COLOR);
            list = new MyList();
            setViewportView(list);
            list.setVisibleRowCount(h);
            list.setColumns(w);
            list.addListSelectionListener(this);
            this.setViewportBorder(border);
            this.setForeground(FOREGROUND_COLOR);
        }

        private void handleFocus() {
            if (leftPane != this) {
                rightPane = leftPane;
                leftPane = this;
                leftPane.border.setTitleColor(HIGHLIGHT_COLOR);
                rightPane.border.setTitleColor(FOREGROUND_COLOR);
                rightPane.repaint();
            }
        }

        private int setData(String s, String n) {
            File file = new File(s);
            if (file.isDirectory()) {
                setPath0(s);
                File[] files = file.listFiles();
                ListDataEntry[] names = new ListDataEntry[files == null ? 0 : files.length];

                //prefix dirs with '/'
                int i = 0;
                if (files != null) {
                    for (File f : files)
                        names[i++] = new ListDataEntry(f);
                }

                Arrays.sort(names);
                //add parent entry: ".."
                if (!"/".equals(s)) {
                    int ln = names.length;
                    ListDataEntry[] names2 = new ListDataEntry[ln + 1];
                    names2[0] = new ListDataEntry(file, "..");
                    System.arraycopy(names, 0, names2, 1, ln);
                    names = names2;
                }

                list.setListData(names);

                //set list selection
                if (n == null) {
                    list.setSelectedIndex(0);
                    return 0;
                } else {
                    i = 0;
                    for (ListDataEntry nn : names) {
                        if (n.equals(nn.name)) {
                            return i;
                        }
                        i++;
                    }

                    return 0;
                }
            }
            return -1;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            setPath0(path);
            setData(path, null);
        }

        public void setPath(String path, String selection) {
            setPath0(path);
            int index = setData(path, selection);
            list.setCurrentRow(index);
        }

        public String getSelection() {
            String sel = ((ListDataEntry) list.getSelectedValue()).name;
            if (sel == null || sel.equals(".."))
                return null;

            if (sel.charAt(0) == '/')
                return sel.substring(1);

            return sel;
        }

        private void setPath0(String path) {
            this.path = path;
            setBorder(path);
        }

        private void setBorder(String path) {
            int w = this.w - 2;
            if (w > 0 && path.length() > w) {
                path = path.substring(path.length() - w);
            }
            border.setTitle(path);
        }

        @Override
        public void valueChanged(ListSelectionEvent evt) {
            if (label != null) {
                if (list.getModel().getSize() > evt.getFirstIndex()) {
                    String selection = getSelection();
                    if (selection == null) {
                        label.setText("");
                    } else {
                        StringBuilder sb = new StringBuilder();
                        format(selection, 38, sb);
                        label.setText(sb.toString());
                    }
                }
            }
        }

        private class MyList extends JList {
            @Override
            public void processKeyEvent(KeyEvent ke) {
                EventQueue evtqueue = Toolkit.getDefaultToolkit().getSystemEventQueue();
                int keyCode = ke.getKeyCode();
                if (keyCode == KeyEvent.VK_ENTER) {
                    Object sel = ((ListDataEntry) list.getSelectedValue()).name;
                    String s;
                    String n = null;
                    if ("..".equals(sel)) {
                        File file = new File(path);
                        n = file.getName();
                        s = file.getParentFile().getAbsolutePath();
                    } else {
                        String sels = String.valueOf(sel);
                        s = new File(path, sels.substring(1, sels.length())).getAbsolutePath();
                    }
                    int sri = setData(s, "/" + n);
                    int dir = _currentRow < sri ? ScrollEvent.UP : ScrollEvent.DOWN;
                    if (sri > -1)
                        _currentRow = sri;
                    setSelectedIndex(_currentRow);
                    evtqueue.postEvent(new ScrollEvent(this, dir, new Point(0, _currentRow)));
                    ke.consume();
                } else if (keyCode == KeyEvent.VK_HOME) {
                    _currentRow = 0;
                    evtqueue.postEvent(new ScrollEvent(
                        this, ScrollEvent.DOWN, new Point(0, _currentRow)));
                }
                super.processKeyEvent(ke);
                if (keyCode == KeyEvent.VK_UP ||
                    keyCode == KeyEvent.VK_DOWN ||
                    keyCode == KeyEvent.VK_PAGE_UP ||
                    keyCode == KeyEvent.VK_PAGE_DOWN ||
                    keyCode == KeyEvent.VK_END ||
                    keyCode == KeyEvent.VK_HOME) {
                    setSelectedIndex(_currentRow);
                }
            }

            @Override
            public void requestFocus() {
                super.requestFocus();
                handleFocus();
            }

            void setCurrentRow(int index) {
                if (index > -1) {
                    int size = list.getModel().getSize();
                    if (index >= size) {
                        index = size - 1;
                    }
                    int dir = _currentRow < index ? ScrollEvent.UP : ScrollEvent.DOWN;
                    _currentRow = index;
                    setSelectedIndex(_currentRow);
                    Toolkit.getDefaultToolkit().getSystemEventQueue().
                        postEvent(new ScrollEvent(this, dir, new Point(0, _currentRow)));
                }
            }
        }
    }

    private static class MyButton extends JButton {
        public MyButton() {
            this("");
        }

        public MyButton(String text_) {
            super(text_);
        }

        @Override
        public boolean isFocusTraversable() {
            return false;
        }


        @Override
        protected String getLabelString() {
            return getText();
        }

        public int getWidth() {
            return getLabelString().length();
        }

        @Override
        public void draw(Toolkit toolkit) {
            Point origin = getLocationOnScreen();
            toolkit.setCursor(origin);
            int colorpair = getCursesColor();
            toolkit.addString(getLabelString(), Toolkit.A_REVERSE, colorpair);
        }
    }
}
