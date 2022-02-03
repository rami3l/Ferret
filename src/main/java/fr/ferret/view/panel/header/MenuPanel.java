package fr.ferret.view.panel.header;

import java.awt.Desktop;
import java.net.URI;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import fr.ferret.utils.Resource;
import fr.ferret.view.FerretFrame;
import fr.ferret.view.panel.header.ferret.SettingsFrame;
import fr.ferret.view.panel.header.ferret.UpdateFrame;
import fr.ferret.view.panel.header.help.AboutFrame;
import fr.ferret.view.panel.header.help.ContactFrame;

/**
 * Menu of the Ferret Frame
 */
public class MenuPanel extends JMenuBar {
    public MenuPanel(FerretFrame ferretFrame) {

        // Ferret menu
        JMenu ferretMenu = new JMenu("Ferret");
        JMenuItem settingsMenuItem = new JMenuItem(Resource.getTextElement("settings.title"));
        JMenuItem updateMenuItem = new JMenuItem(Resource.getTextElement("update.title"));
        JMenuItem exitMenuItem = new JMenuItem(Resource.getTextElement("menu.quit"));
        ferretMenu.add(settingsMenuItem);
        ferretMenu.add(updateMenuItem);
        ferretMenu.add(exitMenuItem);

        // Help menu
        JMenu helpMenu = new JMenu(Resource.getTextElement("menu.help"));
        JMenuItem aboutMenuItem = new JMenuItem(Resource.getTextElement("about.title"));
        JMenuItem faqMenuItem = new JMenuItem(Resource.getTextElement("menu.faq"));
        JMenuItem contactMenuItem = new JMenuItem(Resource.getTextElement("contact.title"));
        helpMenu.add(aboutMenuItem);
        helpMenu.add(faqMenuItem);
        helpMenu.add(contactMenuItem);

        // Adds the two menus defined above
        this.add(ferretMenu);
        this.add(helpMenu);


        // We define menu items action

        // Update window
        updateMenuItem.addActionListener(event -> new UpdateFrame().showFrame(ferretFrame));

        // Settings window
        settingsMenuItem.addActionListener(event -> {
            SettingsFrame settingsFrame = new SettingsFrame(ferretFrame, Resource.CONFIG);
            settingsFrame.setLocationRelativeTo(ferretFrame);
            settingsFrame.setVisible(true);
        });

        // About window
        aboutMenuItem.addActionListener(event -> {
            AboutFrame aboutFrame = new AboutFrame();
            aboutFrame.setLocationRelativeTo(ferretFrame);
            aboutFrame.setVisible(true);
        });

        // Contact window
        contactMenuItem.addActionListener(e -> {
            ContactFrame contactFrame = new ContactFrame();
            contactFrame.setLocationRelativeTo(ferretFrame);
            contactFrame.setVisible(true);
        });

        // FAQ link
        faqMenuItem.addActionListener(event -> {
            try {
                // FAQ link can be different for each language
                Desktop.getDesktop().browse(new URI(Resource.getTextElement("faq.link")));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // Exit Ferret
        exitMenuItem.addActionListener(event -> System.exit(0));
    }
}
