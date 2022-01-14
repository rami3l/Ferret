package fr.ferret.view.panel.header;

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

/**
 * A clickable JTextField containing a link
 */
public class LinkLabel extends JTextField implements MouseListener, FocusListener, ActionListener {
    private URI target;

    public Color standardColor = new Color(0, 0, 255);
    public Color hoverColor = new Color(255, 0, 0);
    public Color activeColor = new Color(128, 0, 128);
    public Color transparent = new Color(0, 0, 0, 0);
    public Color backgroundColor;

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

    public void setBackgroundColor(Color bgColor) {
        this.backgroundColor = bgColor;
    }

    public void init() {
        this.addMouseListener(this);
        this.addFocusListener(this);
        this.addActionListener(this);
        if (target != null)
            setToolTipText(target.toString());
        else
            setToolTipText("Invalid link");

        activeBorder = new MatteBorder(0, 0, 1, 0, activeColor);
        hoverBorder = new MatteBorder(0, 0, 1, 0, hoverColor);
        standardBorder = new MatteBorder(0, 0, 1, 0, transparent);

        setEditable(false);
        setForeground(standardColor);
        setBorder(standardBorder);
        setBackground(backgroundColor);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

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

    @Override
    public void focusGained(FocusEvent e) {
        setForeground(hoverColor);
        setBorder(hoverBorder);
    }

    @Override
    public void focusLost(FocusEvent e) {
        setForeground(standardColor);
        setBorder(standardBorder);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        browse();
    }

    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {
        setForeground(hoverColor);
        setBorder(hoverBorder);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        setForeground(standardColor);
        setBorder(standardBorder);
    }
}
