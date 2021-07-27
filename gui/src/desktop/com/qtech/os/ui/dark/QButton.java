package com.qtech.os.ui.dark;

import com.qtech.os.ui.GraphicsUtils;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;

public class QButton extends JButton {
    private Color accentColor = new Color(0x003fff);
    private Color background = new Color(0x4f4f4f);
    private Color foreground = new Color(0xafafaf);

    public QButton() {
        super();

        setBackground(new Color(0x4f4f4f));
    }

    public QButton(Icon icon) {
        super(icon);
    }

    public QButton(String text) {
        super(text);
    }

    public QButton(Action a) {
        super(a);
    }

    public QButton(String text, Icon icon) {
        super(text, icon);
    }

    @Override
    protected void paintComponent(Graphics g) {
//        super.paintComponent(g);

        Graphics2D gg = (Graphics2D) g;
        gg.setBackground(Color.BLACK);
        gg.clearRect(0, 0, getWidth(), getHeight());

        if (model.isPressed()) {
            gg.setColor(background.darker());
            gg.fillRect(0, 0, getWidth(), getHeight());
        } else if (model.isRollover()) {
            gg.setColor(background.brighter());
            gg.fillRect(0, 0, getWidth(), getHeight());
        } else {
            gg.setColor(background);
            gg.fillRect(0, 0, getWidth(), getHeight());
        }

        if (model.isSelected() && !model.isPressed()) {
            gg.setColor(accentColor);
            gg.drawRect(0, 0, getWidth(), getHeight());
        } else {
            gg.setColor(foreground);
            gg.drawRect(0, 0, getWidth(), getHeight());
        }

        GraphicsUtils.drawCenteredString(g, getText(), new Rectangle2D.Float(0, 0, getWidth(), getHeight()), gg.getFont());
    }

    public Color getAccentColor() {
        return accentColor;
    }

    public void setAccentColor(Color accentColor) {
        this.accentColor = accentColor;
    }

    @Override
    public Color getBackground() {
        return background;
    }

    @Override
    public void setBackground(Color background) {
        this.background = background;
    }
}
