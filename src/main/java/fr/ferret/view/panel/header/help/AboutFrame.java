package fr.ferret.view.panel.header.help;

import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import fr.ferret.utils.Resource;
import fr.ferret.view.LinkLabel;

public class AboutFrame extends JFrame {
    public AboutFrame() {
        super(Resource.getTextElement("about.title"));
        JPanel aboutPanel = new JPanel();
        JLabel ferretVersionLabel = new JLabel(Resource.getTextElement("about.version"));
        JLabel ferretDateLabel = new JLabel(Resource.getTextElement("about.date"));
        JTextArea ferretCitation = new JTextArea(Resource.getTextElement("about.citation"), 4, 50);

        LinkLabel ferretWebLabelAbout = new LinkLabel(Resource.getTextElement("about.link"));

        this.getContentPane().add(aboutPanel);
        this.setResizable(true);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        aboutPanel.setLayout(new BoxLayout(aboutPanel, BoxLayout.Y_AXIS));
        aboutPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        aboutPanel.add(ferretVersionLabel);
        aboutPanel.add(ferretDateLabel);
        aboutPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        ferretWebLabelAbout.setBackgroundColor(aboutPanel.getBackground());
        ferretWebLabelAbout.init();
        ferretWebLabelAbout.setAlignmentX(LEFT_ALIGNMENT);
        ferretWebLabelAbout.setMaximumSize(ferretWebLabelAbout.getPreferredSize());
        aboutPanel.add(ferretWebLabelAbout);
        aboutPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        ferretCitation.setAlignmentX(LEFT_ALIGNMENT);
        ferretCitation.setLineWrap(true);
        ferretCitation.setWrapStyleWord(true);
        ferretCitation.setBackground(aboutPanel.getBackground());
        ferretCitation.setMaximumSize(ferretCitation.getPreferredSize());
        aboutPanel.add(ferretCitation);
        this.pack();
    }
}