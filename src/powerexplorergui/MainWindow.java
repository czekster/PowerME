/*
 * Copyright (C) 2020 Ricardo M. Czekster
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package powerexplorergui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import javax.swing.JFrame;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Ricardo M. Czekster
 */
public class MainWindow extends JFrame {
    private String applicationTitle;
    private JTextArea output;
    private JSplitPane splitPane;
    private JPanel topPanel;
    private JPanel bottomPanel;
    private JTabbedPane tabbedPane;

    public MainWindow() {
        applicationTitle = "PowerME - Power Model Explorer v0.1";
        output = new JTextArea();
        topPanel = new JPanel(new BorderLayout());
        bottomPanel = new JPanel(new GridLayout(1, 0));
        tabbedPane = new JTabbedPane();
        createAndShowGUI();
    }

    private void createMenu() {
        JMenu menu = new JMenu("File");
        menu.setMnemonic(KeyEvent.VK_F);
        JMenuBar mb = new JMenuBar();
        JMenuItem openMenuItem = new JMenuItem("Open");
        openMenuItem.setPreferredSize(new Dimension(100, openMenuItem.getPreferredSize().height));
        openMenuItem.setMnemonic(KeyEvent.VK_O);
        openMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.ALT_MASK));
        JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.setMnemonic(KeyEvent.VK_X);
        menu.add(openMenuItem);

        JMenu menuModel = new JMenu("Model");
        menuModel.setMnemonic(KeyEvent.VK_S);
        JMenuItem duplicateMenuItem = new JMenuItem("Duplicate");
        duplicateMenuItem.setPreferredSize(new Dimension(100, duplicateMenuItem.getPreferredSize().height));
        menuModel.add(duplicateMenuItem);
        JMenuItem renameMenuItem = new JMenuItem("Rename");
        menuModel.add(renameMenuItem);
        JMenuItem closeMenuItem = new JMenuItem("Close");
        menuModel.add(closeMenuItem);
        menuModel.addSeparator();
        JMenuItem deleteMenuItem = new JMenuItem("Delete");
        deleteMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, ActionEvent.ALT_MASK));
        menuModel.add(deleteMenuItem);

        JMenu menuHelp = new JMenu("Help");
        menuHelp.setMnemonic(KeyEvent.VK_H);
        JMenuItem menuItemAbout = new JMenuItem("About...");
        menuItemAbout.setPreferredSize(new Dimension(100, menuItemAbout.getPreferredSize().height));
        menuHelp.add(menuItemAbout);

        openMenuItem.addActionListener((ActionEvent e) -> {
            JMenuItem menuItem = (JMenuItem) e.getSource();
            JFileChooser fc = new JFileChooser();
            fc.setMultiSelectionEnabled(true);
            fc.setCurrentDirectory(new File("."));
            fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            FileNameExtensionFilter filter = new FileNameExtensionFilter("GridLAB-D xml files", "xml");
            fc.setFileFilter(filter);

            int returnVal = fc.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File[] files = fc.getSelectedFiles();
                for (File file : files) {
                    String tabName = file.getName();
                    File f = new File(tabName);
                    if (!isOpenFile(tabName) && f.exists() && Utils.getFileExtension(tabName).equals("xml")) {
                        addTab(tabName, file.getParent());
                        output.append(Utils.getTime() + "Model '" + tabName + "' open successfully." + '\n');
                        // remove 'untitled' tab
                        removeUntitled();
                    } else {
                        JOptionPane.showMessageDialog(this, "Error while opening '" + tabName + "'. Maybe it is already open, not an XML file or it does not exist.");
                    }
                }
            }
        });
        deleteMenuItem.addActionListener((ActionEvent e) -> {
            int tabIndex = tabbedPane.getSelectedIndex();
            if (tabIndex != -1) {
                String tabName = tabbedPane.getTitleAt(tabIndex);
                ScenarioPanel sp = (ScenarioPanel) tabbedPane.getComponentAt(tabIndex);
                String parentPath = sp.getFileParentPath();
                File file = new File(parentPath + "/" + tabName);
                if ((JOptionPane.showConfirmDialog(null, "You are about to delete '" + tabName + "' from the filesystem.\nDo you want to proceed ?",
                        "Confirmation Dialog", JOptionPane.OK_CANCEL_OPTION)) == 0) {
                    if (file.delete()) {
                        tabbedPane.remove(tabIndex);
                        output.append(Utils.getTime() + "Model '" + tabName + "' deleted successfully." + '\n');
                    } else {
                        JOptionPane.showMessageDialog(this, "Error while deleting '" + tabName + "'.");
                    }
                }
            }
        });
        duplicateMenuItem.addActionListener((ActionEvent e) -> {
            int tabIndex = tabbedPane.getSelectedIndex();
            if (tabIndex != -1) {
                String tabName = tabbedPane.getTitleAt(tabIndex);
                String newName = (String) JOptionPane.showInputDialog(this, "Enter a new name for the model:",
                        "Duplicate model", JOptionPane.QUESTION_MESSAGE, null, null, tabName);
                if (newName != null) {
                    ScenarioPanel sp = (ScenarioPanel) tabbedPane.getComponentAt(tabIndex);
                    String parentPath = sp.getFileParentPath();
                    File source = new File(parentPath + "/" + tabName);
                    File dest = new File(parentPath + "/" + newName);
                    if (!source.exists()) {
                        JOptionPane.showMessageDialog(this, "File '" + source.getName() + "' does not exist.");
                    } else
                        if (source.getAbsolutePath().equals(dest.getAbsolutePath()) == false) {
                            try {
                                Files.copy(source.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
                            } catch (IOException ex) {
                                output.append("Error while duplicating file: " + ex.getMessage());
                            }
                            addTab(newName, dest.getParent());
                            output.append(Utils.getTime() + "Model '" + tabName + "' duplicated successfully." + '\n');
                        } else {
                            JOptionPane.showMessageDialog(this, "Error while duplicating '" + newName + "'. Maybe the file already exists in the filesystem.");
                        }
                }
            }
        });
        menu.addSeparator();
        menu.add(exitMenuItem);
        exitMenuItem.addActionListener((ActionEvent e) -> {
            JMenuItem menuItem = (JMenuItem) e.getSource();
            String text = menuItem.getText();
            if ((JOptionPane.showConfirmDialog(null, "You are about to exit the tool.\nDo you want to proceed ?",
                    "Quit", JOptionPane.OK_CANCEL_OPTION)) == 0) {
                System.exit(0);
            }
        });
        mb.add(menu);
        closeMenuItem.addActionListener((ActionEvent e) -> {
            int tabIndex = tabbedPane.getSelectedIndex();
            if (tabIndex != -1) {
                String tab = tabbedPane.getTitleAt(tabIndex);
                if ((JOptionPane.showConfirmDialog(null, "You are about to close '" + tab + "'\nDo you want to proceed ?",
                        "Confirmation Dialog", JOptionPane.OK_CANCEL_OPTION)) == 0) {
                    tabbedPane.remove(tabIndex);
                    output.append(Utils.getTime() + "Model '" + tab + "' closed successfully." + '\n');
                }
            }
        });
        renameMenuItem.addActionListener((ActionEvent e) -> {
            int tabIndex = tabbedPane.getSelectedIndex();
            if (tabIndex != -1) {
                String tabName = tabbedPane.getTitleAt(tabIndex);
                String newName = tabName;
                File f = new File(newName);
                if (f.exists()) {
                    newName = (String) JOptionPane.showInputDialog(this, "File exists. Overwrite it?",
                        "File exists", JOptionPane.QUESTION_MESSAGE, null, null, tabName);
                    if (newName != null) {
                        File f2 = new File(newName);
                        boolean success = f.renameTo(f2);
                        if (!success)
                            JOptionPane.showMessageDialog(this, "Error while renaming '" + newName + "'. Maybe the file already exists in the filesystem.");
                        else {
                            tabbedPane.setTitleAt(tabIndex, newName);
                            output.append(Utils.getTime() + "Model '" + newName + "' renamed successfully." + '\n');
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "File '" + tabName + "' does not exist.");
                }
            }
        });
        mb.add(menuModel);
        menuItemAbout.addActionListener((ActionEvent e) -> {
            AboutPanel about = new AboutPanel(this);
            about.setVisible(true);
            this.setEnabled(false);
        });
        mb.add(menuHelp);
        UIManager.put("Menu.font", new Font("Tahoma", Font.PLAIN, 14));
        UIManager.put("MenuItem.font", new Font("Tahoma", Font.PLAIN, 14));
        SwingUtilities.updateComponentTreeUI(mb);
        setJMenuBar(mb);
    }

    private void removeUntitled() {
        String tab = tabbedPane.getTitleAt(0);
        if (tab.equals("Untitled")) {
            tabbedPane.remove(0);
        }
    }

    private boolean isOpenFile(String name) {
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            String title = tabbedPane.getTitleAt(i);
            if (title.equals(name)) {
                return true;
            }
        }
        return false;
    }

    private void addTab(String name, String fileParentPath) {
        ScenarioPanel scenarioPanel = new ScenarioPanel(output, name, fileParentPath);
        ImageIcon icon = new ImageIcon(MainWindow.class.getResource("images/copy.png"));
        tabbedPane.addTab(name, icon, scenarioPanel);
        int ttabs = tabbedPane.getTabCount() - 1;
        tabbedPane.setTabComponentAt(ttabs, new ButtonTabComponent(tabbedPane, output));
        // select the last tab
        tabbedPane.setSelectedIndex(ttabs);
    }

    private void addComponentsToPane(Container c) {
        // Add the menu and menuitems
        createMenu();
        addTab("Untitled", "dummy path");

        topPanel.add(tabbedPane);
        c.add(topPanel);

        // Add bottom panel and textarea for output
        Font bigFont = new Font("Courier New", Font.PLAIN, 14);
        bottomPanel.setFont(bigFont);
        Font tabFont = new Font("Tahoma", Font.BOLD, 12);
        tabbedPane.setFont(tabFont);
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

        Font outputFont = new Font("Courier New", Font.PLAIN, 14);
        output.setFont(outputFont);
        bottomPanel.add(output);

        bottomPanel.setSize(new Dimension(400, 200));
        JScrollPane outScrollPane = new JScrollPane(output);
        bottomPanel.add(outScrollPane);
        c.add(bottomPanel, BorderLayout.NORTH);

        // Add the divider between the two panes
        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topPanel, outScrollPane);
        splitPane.setOneTouchExpandable(true);
    }

    private void createAndShowGUI() {
        //Create and set up the window.
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //Set up the content pane.
        addComponentsToPane(getContentPane());
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));

        //Use the content pane's default BorderLayout. No need for setLayout(new BorderLayout());
        //Display the window.
        setTitle(applicationTitle);
        getContentPane().add(splitPane);
        pack();
        setSize(800, 800);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainWindow();
            }
        });
    }

}
