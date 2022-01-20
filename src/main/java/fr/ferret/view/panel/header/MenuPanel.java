package fr.ferret.view.panel.header;

import java.awt.Desktop;
import java.net.URI;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import fr.ferret.controller.settings.FerretConfig;
import fr.ferret.utils.Resource;
import fr.ferret.view.FerretFrame;

/**
 * Menu of the Ferret Frame
 */
public class MenuPanel extends JMenuBar {
    public MenuPanel(FerretFrame ferretFrame) {
        JMenu ferretMenu = new JMenu("Ferret");
        JMenu helpMenu = new JMenu(Resource.getTextElement("menu.help"));

        JMenuItem settingsMenuItem = new JMenuItem(Resource.getTextElement("settings.title"));
        JMenuItem updateMenuItem = new JMenuItem(Resource.getTextElement("update.title"));
        JMenuItem exitMenuItem = new JMenuItem(Resource.getTextElement("menu.quit"));
        JMenuItem aboutMenuItem = new JMenuItem(Resource.getTextElement("about.title"));
        JMenuItem faqMenuItem = new JMenuItem(Resource.getTextElement("menu.faq"));
        JMenuItem contactMenuItem = new JMenuItem(Resource.getTextElement("contact.title"));

        ferretMenu.add(settingsMenuItem);
        ferretMenu.add(updateMenuItem);
        ferretMenu.add(exitMenuItem);
        helpMenu.add(aboutMenuItem);
        helpMenu.add(faqMenuItem);
        helpMenu.add(contactMenuItem);
        this.add(ferretMenu);
        this.add(helpMenu);

        // update window
        updateMenuItem.addActionListener(arg0 -> new UpdateFrame().showFrame(ferretFrame));

        // Settings pane:
        FerretConfig config = Resource.CONFIG;
        settingsMenuItem.addActionListener(arg0 -> {
            SettingsFrame settingsFrame = new SettingsFrame(ferretFrame, config);
            settingsFrame.setLocationRelativeTo(ferretFrame);
            settingsFrame.setVisible(true);
        });

        // About window
        aboutMenuItem.addActionListener(arg0 -> {
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

        // Other action listeners
        exitMenuItem.addActionListener(arg0 -> System.exit(0));
        faqMenuItem.addActionListener(arg0 -> {
            try {
                // On met le lien dans la traduction : possible de faire des faq dans d'autres
                // langues
                Desktop.getDesktop().browse(new URI(Resource.getTextElement("faq.link")));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
