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

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * PowerME - Power Model Explorer
 * AboutPanel - Frame for the Help > About...
 * @author Ricardo M. Czekster
 */

public class AboutPanel extends JFrame {
    private JTextArea textArea;
    private JLabel hyperlink;

    public AboutPanel(JFrame parent) {
        textArea = new JTextArea();
        
        hyperlink = new JLabel("Visit PowerME Project @ GitHub");
        Font f = new Font("Tahoma", Font.PLAIN, 18);
        hyperlink.setFont(f);
        hyperlink.setForeground(Color.BLUE.darker());
        hyperlink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        hyperlink.setHorizontalAlignment(JLabel.CENTER);

        hyperlink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI("https://github.com/czekster/PowerME"));
                    parent.setEnabled(true);
                    dispose();
                } catch (IOException | URISyntaxException e1) {
                    e1.printStackTrace();
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                hyperlink.setText("<html><a href=''>Visit PowerME Project @ GitHub</a></html>");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hyperlink.setText("Visit PowerME Project @ GitHub");
            }
        });
        // capture window closed event
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                parent.setEnabled(true);
            }
        });

        setTitle("About...");
        setLayout(new GridLayout(2, 0));
        setSize(400, 300);
        String str = "";
        str += "PowerME - Power Model Explorer" + '\n';
        str += "The tool is used to explore power simulation models." + '\n';
        str += "This version works only with GridLAB-D XML models." + '\n';
        str += "Run gridlabd (linux) and append on the model:" + '\n';
        str += "#set savefile=\"model.xml\"" + '\n';
        str += "Open \"model.xml\" in PowerME" + '\n';
        textArea.setText(str);
        textArea.setFont(new Font("Tahoma", Font.PLAIN, 16));
        textArea.setBorder(BorderFactory.createLineBorder(Color.black));
        textArea.setEditable(false);
        getContentPane().add(textArea);
        getContentPane().add(hyperlink);
        setLocationRelativeTo(null);
    }

}
