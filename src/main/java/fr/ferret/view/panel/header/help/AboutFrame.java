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
        aboutPanel.setLayout(new BoxLayout(aboutPanel, BoxLayout.Y_AXIS));
        aboutPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Version label
        JLabel ferretVersionLabel = new JLabel(Resource.getTextElement("about.version"));
        aboutPanel.add(ferretVersionLabel);

        // Date label
        JLabel ferretDateLabel = new JLabel(Resource.getTextElement("about.date"));
        aboutPanel.add(ferretDateLabel);

        aboutPanel.add(Box.createRigidArea(new Dimension(0, 12)));

        // About link
        LinkLabel ferretAboutLink = new LinkLabel(Resource.getTextElement("about.link"));
        ferretAboutLink.setBackgroundColor(aboutPanel.getBackground());
        ferretAboutLink.init();
        ferretAboutLink.setAlignmentX(LEFT_ALIGNMENT);
        ferretAboutLink.setMaximumSize(ferretAboutLink.getPreferredSize());
        aboutPanel.add(ferretAboutLink);

        aboutPanel.add(Box.createRigidArea(new Dimension(0, 12)));

        // Citation
        JTextArea ferretCitation = new JTextArea(Resource.getTextElement("about.citation"), 4, 50);
        ferretCitation.setAlignmentX(LEFT_ALIGNMENT);
        ferretCitation.setLineWrap(true);
        ferretCitation.setWrapStyleWord(true);
        ferretCitation.setBackground(aboutPanel.getBackground());
        ferretCitation.setMaximumSize(ferretCitation.getPreferredSize());
        aboutPanel.add(ferretCitation);

        // frame settings
        this.getContentPane().add(aboutPanel);
        this.setResizable(true);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.pack();
    }
}
