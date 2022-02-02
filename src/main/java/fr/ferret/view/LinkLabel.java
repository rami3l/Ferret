package fr.ferret.view;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URI;
import java.net.URISyntaxException;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.MatteBorder;
import fr.ferret.utils.Resource;
import lombok.Setter;

/**
 * A clickable JTextField containing a link
 */
public class LinkLabel extends JTextField implements MouseListener, FocusListener, ActionListener {

    private URI target;

    // Colors need to be seen in dark and white mode
    private Color standardColor = Resource.LINK_STANDARD_COLOR;
    private Color hoverColor = Resource.LINK_HOVER_COLOR;
    private Color activeColor = Resource.LINK_ACTIVE_COLOR;
    private Color transparent = new Color(0, 0, 0, 0);
    @Setter
    private Color backgroundColor;

    private Border activeBorder;
    private Border hoverBorder;
    private Border standardBorder;

    public LinkLabel(String url) {
        this(url, url);
    }

    public LinkLabel(String url, String text) {
        super(text);
        try {
            this.target = new URI(url);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void init() {

        // Adds listeners
        addMouseListener(this);
        addFocusListener(this);
        addActionListener(this);

        // Tooltip (shown when link hovered)
        setToolTipText(target == null ? "Invalid link" : target.toString());

        activeBorder = new MatteBorder(0, 0, 1, 0, activeColor);
        hoverBorder = new MatteBorder(0, 0, 1, 0, hoverColor);
        standardBorder = new MatteBorder(0, 0, 1, 0, transparent);

        // Text settings
        setEditable(false);
        setForeground(standardColor);
        setBorder(standardBorder);
        setBackground(backgroundColor);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    /**
     * Tries to open link in the browser
     */
    public void browse() {
        setForeground(activeColor);
        setBorder(activeBorder);
        try {
            Desktop.getDesktop().browse(target);
        } catch (Exception e) {
            e.printStackTrace();
        }
        setForeground(standardColor);
        setBorder(standardBorder);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        browse();
    }

    /**
     * Browse the link when mouse clicked
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        browse();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // We do nothing when mouse pressed
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // We do nothing when mouse released
    }

    /**
     * Changes visual properties on focus
     */
    @Override
    public void focusGained(FocusEvent e) {
        setForeground(hoverColor);
        setBorder(hoverBorder);
    }

    /**
     * Undo visual changes when focus lost
     */
    @Override
    public void focusLost(FocusEvent e) {
        setForeground(standardColor);
        setBorder(standardBorder);
    }

    /**
     * Changes visual properties on mouse hover
     */
    @Override
    public void mouseEntered(MouseEvent e) {
        setForeground(hoverColor);
        setBorder(hoverBorder);
    }

    /**
     * Undo visual changes when mouse exit
     */
    @Override
    public void mouseExited(MouseEvent e) {
        setForeground(standardColor);
        setBorder(standardBorder);
    }
}
