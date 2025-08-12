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

public class Oval implements Shape {
        private final Point start, end;
        private final Color color;
        private final boolean filled;

        public Oval(Point start, Point end, Color color , boolean filled) {
            this.start = start;
            this.end = end;
            this.color = color;
            this.filled = filled;
        }

        @Override
        public void draw(Graphics2D g) {
            g.setColor(color);
            g.setStroke(new BasicStroke());
            
            if (filled) {
                g.fillOval(Math.min(start.x, end.x), Math.min(start.y, end.y),
                        Math.abs(start.x - end.x), Math.abs(start.y - end.y));
            } else {
                g.drawOval(Math.min(start.x, end.x), Math.min(start.y, end.y),
                        Math.abs(start.x - end.x), Math.abs(start.y - end.y));
            }
        }

        @Override
        public boolean contains(Point point) {
            return new java.awt.geom.Ellipse2D.Double(Math.min(start.x, end.x), Math.min(start.y, end.y),
                    Math.abs(start.x - end.x), Math.abs(start.y - end.y)).contains(point);
        }
    }