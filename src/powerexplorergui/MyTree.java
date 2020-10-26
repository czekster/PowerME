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

import java.awt.Font;
import java.awt.GridLayout;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * PowerME - Power Model Explorer MyTree - it creates a tree structure (GUI
 * component)
 *
 * @author Ricardo M. Czekster
 */
public class MyTree extends JPanel {
    private Document doc;
    private JTree tree;
    private JScrollPane treeScrollPane;
    private String parentPath;
    private String name;
    private Node root;
    private JTextArea outputRef;
    private DefaultTreeModel dtModel = null;

    public MyTree(JTextArea outputTextArea, String fileParentPath, String name) {
        super(new GridLayout(1, 0));
        this.name = name;
        this.outputRef = outputTextArea;
        this.parentPath = fileParentPath;

        // just a dummy tree
        if (name.equals("Untitled")) {
            DefaultMutableTreeNode root = new DefaultMutableTreeNode("Main");
            tree = new JTree(root);
        } else openXML();

        ImageIcon imageIcon = new ImageIcon(MyTree.class.getResource("images/leaf.jpg"));
        DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
        renderer.setLeafIcon(imageIcon);

        tree.setCellRenderer(renderer);
        tree.setShowsRootHandles(true);
        tree.setRootVisible(true);
        treeScrollPane = new JScrollPane(tree);
        add(treeScrollPane);

        Font bigFont = new Font(tree.getFont().getName(), tree.getFont().getStyle(), tree.getFont().getSize() + 5);
        tree.setFont(bigFont);

        tree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                System.out.println(e.getPath().toString());
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                if (selectedNode != null && selectedNode.isLeaf()) {
                    String str = doc.getElementsByTagName(selectedNode.getUserObject().toString()).item(0).getTextContent();
                    outputTextArea.append("[" + name + "] " + str + '\n');
                }
            }
        });
        this.setVisible(true);
    }

    public JScrollPane getTreeScrollPane() {
        return treeScrollPane;
    }

    public void openXML() {
        String filePath = parentPath + "/" + name;
        try {
            InputStream inputStream= new FileInputStream(filePath);
            Reader reader = new InputStreamReader(inputStream, "UTF-8");
            InputSource is = new InputSource(reader);
            is.setEncoding("UTF-8");

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.parse(is);

            root = (Node) doc.getDocumentElement();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Can't parse file", "Error",
                    JOptionPane.ERROR_MESSAGE);
            System.out.println("Exception: " + ex);
        }
        if (root != null) {
            dtModel = new DefaultTreeModel(builtTreeNode(root));
            tree = new JTree(dtModel);
            tree.setModel(dtModel);
        }
    }

    private DefaultMutableTreeNode builtTreeNode(Node root) {
        DefaultMutableTreeNode dmtNode;

        dmtNode = new DefaultMutableTreeNode(root.getNodeName());
        NodeList nodeList = root.getChildNodes();
        for (int count = 0; count < nodeList.getLength(); count++) {
            Node tempNode = nodeList.item(count);

            if (tempNode.getNodeType() == Node.ELEMENT_NODE) {
                if (tempNode.hasChildNodes()) {
                    dmtNode.add(builtTreeNode(tempNode));
                }
            }
        }
        return dmtNode;
    }

}
