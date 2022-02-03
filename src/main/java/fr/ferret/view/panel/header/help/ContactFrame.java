package fr.ferret.view.panel.header.help;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import fr.ferret.utils.Resource;

public class ContactFrame extends JFrame {

    public ContactFrame() {

        super(Resource.getTextElement("contact.title"));

        // panel settings
        JPanel contactPanel = new JPanel();
        contactPanel.setLayout(new BoxLayout(contactPanel, BoxLayout.Y_AXIS));
        contactPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Contact names
        JLabel contactPeopleLabel = new JLabel(Resource.getTextElement("contact.text"));
        contactPeopleLabel.setAlignmentX(CENTER_ALIGNMENT);
        contactPanel.add(contactPeopleLabel);

        // Contact emails
        JTextArea contactEmailLabel = new JTextArea(Resource.getTextElement("contact.mail"));
        contactEmailLabel.setAlignmentX(CENTER_ALIGNMENT);
        contactEmailLabel.setBackground(contactPanel.getBackground());
        contactPanel.add(contactEmailLabel);

        // frame settings
        this.getContentPane().add(contactPanel);
        this.setResizable(true);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.pack();
    }
}
