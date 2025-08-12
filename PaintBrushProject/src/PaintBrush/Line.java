/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PaintBrush;

/**
 *
 * @author GRAPHICS
 */
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

public class Line implements Shape{
        private final Point start, end;
        private final Color color;
        private final boolean filled;

        public Line(Point start, Point end, Color color , boolean filled) {
            this.start = start;
            this.end = end;
            this.color = color;
            this.filled = filled;
        }

        @Override
        public void draw(Graphics2D g) {
            g.setColor(color);
            g.setStroke(new BasicStroke());
            g.drawLine(start.x, start.y, end.x, end.y);
        }

        @Override
        public boolean contains(Point point) {
            return false;
        }
    
}
