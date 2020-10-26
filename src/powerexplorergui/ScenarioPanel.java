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
import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 * PowerME - Power Model Explorer
 * ScenarioPanel - main application Panel with the scenarios (tree structure)
 * @author Ricardo M. Czekster
 */

public class ScenarioPanel extends JPanel {
    private JTextArea outputRef;
    private String modelName;
    private String fileParentPath; // i.e. only saves the path, not the filename
    
    public ScenarioPanel(JTextArea output, String name, String parent) {
        this.modelName = name;
        this.fileParentPath = parent;
        outputRef = output;
        setLayout(new BorderLayout());
        addComponents();
    }

    public String getFileParentPath() {
        return fileParentPath;
    }

    public void setFileParentPath(String parent) {
        this.fileParentPath = parent;
    }
    
    public String getModelName() {
        return modelName;
    }
    
    private void addComponents() {
        // Add the tree structure (top panel)
        MyTree myTree = new MyTree(outputRef, fileParentPath, modelName);
        add(myTree);
    }
    
}
