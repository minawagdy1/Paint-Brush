/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PaintBrush;

import javax.swing.JFrame;

/**
 *
 * @author GRAPHICS
 */
public class PaintBrush {
    public static void main(String[] args) {
        
        JFrame f = new JFrame(); 
        f.setTitle("Paint Brush ");  
        f.setContentPane(new MyPanel());  
        f.setSize(900, 650);  
        f.setVisible(true);  
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
        f.setExtendedState(JFrame.MAXIMIZED_BOTH); // Start the app in fullscreen mode.
    }
    
}
