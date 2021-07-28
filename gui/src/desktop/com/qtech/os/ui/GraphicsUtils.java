package com.qtech.os.ui;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class GraphicsUtils {
    /**
     * Draw a String centered in the middle of a Rectangle.
     *
     * @param g    The Graphics instance.
     * @param text The String to draw.
     * @param rect The Rectangle to center the text within.
     * @param font The Font for the text.
     * @author <b>Danier Kvist</b> <a href="https://stackoverflow.com/a/27740330/11124294">from this answer</a>.
     */
    public static void drawCenteredString(Graphics g, String text, Rectangle2D rect, Font font) {
        // Get the FontMetrics
        FontMetrics metrics = g.getFontMetrics(font);

        // Determine the X coordinate for the text
        int x = (int) (rect.getX() + (rect.getWidth() - metrics.stringWidth(text)) / 2);

        // Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top of the screen)
        int y = (int) (rect.getY() + ((rect.getHeight() - metrics.getHeight()) / 2) + metrics.getAscent());

        // Set the font
        g.setFont(font);

        // Draw the String
        g.drawString(text, x, y);
    }

    /**
     * Draw a String centered in the middle of a Rectangle.
     *
     * @param g      The Graphics instance.
     * @param text   The String to draw.
     * @param point  The Point to center the text within.
     * @param height The height to center the text within.
     * @param font   The Font for the text.
     * @author <b>Danier Kvist</b> <a href="https://stackoverflow.com/a/27740330/11124294">from this answer</a>.
     */
    public static void drawRightAnchoredString(Graphics g, String text, Point2D point, double height, Font font) {
        // Get the FontMetrics
        FontMetrics metrics = g.getFontMetrics(font);

        // Determine the X coordinate for the text
        int x = (int) (point.getX() - (metrics.stringWidth(text)));

        // Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top of the screen)
        int y = (int) (point.getY() + ((height - metrics.getHeight()) / 2) + metrics.getAscent());

        // Set the font
        g.setFont(font);

        // Draw the String
        g.drawString(text, x, y);
    }

    /**
     * Draw a String centered in the middle of a Rectangle.
     *
     * @param g      The Graphics instance.
     * @param text   The String to draw.
     * @param point  The Point to center the text within.
     * @param height The height to center the text within.
     * @param font   The Font for the text.
     * @author <b>Danier Kvist</b> <a href="https://stackoverflow.com/a/27740330/11124294">from this answer</a>.
     */
    public static void drawLeftAnchoredString(Graphics g, String text, Point2D point, double height, Font font) {
        // Get the FontMetrics
        FontMetrics metrics = g.getFontMetrics(font);

        // Determine the X coordinate for the text
        int x = (int) (point.getX());

        // Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top of the screen)
        int y = (int) (point.getY() + ((height - metrics.getHeight()) / 2) + metrics.getAscent());

        // Set the font
        g.setFont(font);

        // Draw the String
        g.drawString(text, x, y);
    }
}
