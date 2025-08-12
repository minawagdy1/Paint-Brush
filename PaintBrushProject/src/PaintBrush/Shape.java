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
import java.awt.Graphics2D;
import java.awt.Point;

public interface Shape {
       void draw(Graphics2D g);
        boolean contains(Point point);
}