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
import java.util.ArrayList;

public class FreeHand implements Shape {
        private final ArrayList<Point> points;
        private final Color color;

        public FreeHand(ArrayList<Point> points, Color color) {
            this.points = points;
            this.color = color;
        }

        @Override
        public void draw(Graphics2D g) {
            g.setColor(color);
            g.setStroke(new BasicStroke());
            
            for (int i = 0; i < points.size() - 1; i++) {
                Point p1 = points.get(i);
                Point p2 = points.get(i + 1);
                g.drawLine(p1.x, p1.y, p2.x, p2.y);
            }
        }

        @Override
        public boolean contains(Point point) {
            return false;
        }
    }