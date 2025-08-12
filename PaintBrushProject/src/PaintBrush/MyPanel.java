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
import java.awt.BorderLayout;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Stack;
import javax.swing.JButton;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class MyPanel extends JPanel {
    private boolean drawing;  // True when mouse is pressed and moving
    private boolean filled;     // True if shapes should be filled
    private boolean erasing;    // True if eraser mode is active
    private Color currentColor;
    private DrawingMode currentMode;
    private final Stack<Shape> undoStack;
    private Point startPoint;
    private Point endPoint;
    private final BufferedImage canvasImage;
    private final Graphics2D canvasGraphics;

    

    

    private enum DrawingMode {
        LINE, RECTANGLE, OVAL, PEN, NONE
    }

    public MyPanel() {
        // Get graphics context for drawing directly onto canvas
        canvasImage = new BufferedImage(1600, 900, BufferedImage.TYPE_INT_ARGB);
        
        // Get graphics context for drawing directly onto canvas
        canvasGraphics = canvasImage.createGraphics();
        canvasGraphics.setColor(Color.WHITE);
        canvasGraphics.fillRect(0, 0, 1600, 900); // Fill background with white
        
        // Set defaults
        currentColor = Color.BLACK;
        currentMode = DrawingMode.LINE;
        undoStack = new Stack<>();
        filled = false;
        erasing = false;
        

        // Grid layout for buttons
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(1, 0, 10, 0));
        add(panel, BorderLayout.AFTER_LAST_LINE);
        
        // Custom drawing surface
        DrawingArea canvas = new DrawingArea();
        canvas.setPreferredSize(new Dimension(1600, 900));
        add(canvas, BorderLayout.CENTER);

       //Add buttons and tools
        panel.add(createButton("Pen", new ActionListener() {             
            @Override
            public void actionPerformed(ActionEvent e) {
                setCurrentMode(DrawingMode.PEN);            }
        }));

        panel.add(createButton("Line", new ActionListener() { 
            @Override
            public void actionPerformed(ActionEvent e) {
                setCurrentMode(DrawingMode.LINE);
            }
        }));

        panel.add(createButton("Rectangle", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setCurrentMode(DrawingMode.RECTANGLE);
            }
        }));

        panel.add(createButton("Oval", new ActionListener() { 
            @Override
            public void actionPerformed(ActionEvent e) {
                setCurrentMode(DrawingMode.OVAL);
            }
        }));

        

        // Checkbox for fill option
        JCheckBox filledCheckBox = new JCheckBox("Fill");
        filledCheckBox.setForeground(Color.BLACK);
        
        filledCheckBox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                filled = filledCheckBox.isSelected();
            }
        });
        panel.add(filledCheckBox);

        
        // Color buttons
        JButton redButton = createButton("Red", new ActionListener() {  
            @Override
            public void actionPerformed(ActionEvent e) {
                currentColor = Color.RED;
            }
        });
        redButton.setBackground(Color.RED); 
        panel.add(redButton);
         
        JButton greenButton = createButton("Green", new ActionListener() {  
            @Override
            public void actionPerformed(ActionEvent e) {
                currentColor = Color.GREEN;
            }
        });
        greenButton.setBackground(Color.GREEN); 
        panel.add(greenButton);
        

        
        JButton blueButton = createButton("Blue", new ActionListener() {  
            @Override
            public void actionPerformed(ActionEvent e) {
                currentColor = Color.BLUE;
            }
        });
        blueButton.setBackground(Color.BLUE); 
        panel.add(blueButton);
        
        
        // Utility buttons
        panel.add(createButton("Undo", new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                undo();
            }
        }));
        panel.add(createButton("Clear", new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                clearCanvas();
            }
        })); 
        panel.add(createButton("Erase", new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                toggleErase();
            }
        }));  

        panel.add(createButton("Save", new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                saveCanvas();
            }
        }));
        
        
        panel.add(createButton("Open", new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                openImage();
            }
        }));  
        
    }

    // Inner class responsible for handling drawing operations on the panel
    private class DrawingArea extends JPanel {
        private ArrayList<Point> freeHandPoints = new ArrayList<>();

        public DrawingArea() {
        setBackground(Color.WHITE);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                    drawing = true;
                    startPoint = e.getPoint();
                    freeHandPoints.clear();
                    freeHandPoints.add(startPoint);
                    
                    if (erasing) {
                        // Draw directly to canvas image when erasing
                        canvasGraphics.setColor(Color.WHITE);
                        canvasGraphics.setStroke(new BasicStroke(10));
                        canvasGraphics.fillOval(e.getX() - 5, e.getY() - 5, 10, 10);
                    } 
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                    if (!erasing) {
                        endPoint = e.getPoint();
                        if (currentMode != DrawingMode.PEN) {
                            Shape shape = createShape(startPoint, endPoint);
                            if (shape != null) {
                                // Draw the shape to the canvas image
                                shape.draw(canvasGraphics);
                                undoStack.push(shape);  // Save shape for undo functionality
                            }
                        } else {
                            // If using pen tool, draw freehand
                            FreeHand freeHand = new FreeHand(new ArrayList<>(freeHandPoints), currentColor);
                            freeHand.draw(canvasGraphics);
                            undoStack.push(freeHand);
                        }
                    }
                    drawing = false;
                    repaint();
                }
            
        });

        addMouseMotionListener(new MouseAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    if (erasing) {
                        // Draw directly to canvas image when erasing
                        canvasGraphics.setColor(Color.WHITE);
                        canvasGraphics.setStroke(new BasicStroke(10));
                        Point last = freeHandPoints.get(freeHandPoints.size() - 1);
                        canvasGraphics.drawLine(last.x, last.y, e.getX(), e.getY());
                        freeHandPoints.add(e.getPoint());
                    } else if (currentMode == DrawingMode.PEN) {
                        freeHandPoints.add(e.getPoint());
                    } else {
                        endPoint = e.getPoint();
                    }
                    repaint();
                }
        });
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        g2d.drawImage(canvasImage, 0, 0, null);
        // Draw the current shape preview if drawing
            if (drawing && !erasing) {
                if (currentMode != DrawingMode.PEN) {
                    Shape previewShape = createShape(startPoint, endPoint);
                    if (previewShape != null) {
                        previewShape.draw(g2d);
                    }
                } else {
                    new FreeHand(freeHandPoints, currentColor).draw(g2d);
                }
            }
        
    }
    }

    private Shape createShape(Point start, Point end) {
        switch (currentMode) {
            case LINE:
                return new Line(start, end, currentColor, filled);
            case RECTANGLE:
                return new Rectangle(start, end, currentColor, filled);
            case OVAL:
                return new Oval(start, end, currentColor, filled);
            default:
                return null;
        }
    }

    private JButton createButton(String text, ActionListener listener) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.addActionListener(listener);
        return button;
    }

    

    private void clearCanvas() {
        undoStack.clear();
        // Clear the canvas image by filling it with white
        canvasGraphics.setColor(Color.WHITE);
        canvasGraphics.fillRect(0, 0, canvasImage.getWidth(), canvasImage.getHeight());
        repaint();
    }

    private void undo() {
        if (!undoStack.isEmpty()) {
            undoStack.pop();
            // Redraw everything from scratch
            canvasGraphics.setColor(Color.WHITE);
            canvasGraphics.fillRect(0, 0, canvasImage.getWidth(), canvasImage.getHeight());
            
            // Redraw all shapes in the stack
            for (Shape shape : undoStack) {
                shape.draw(canvasGraphics);
            }
            repaint();
        }
    }

    private void setCurrentMode(DrawingMode mode) {
        currentMode = mode;

        // CHANGED: If the user selects any drawing tool, disable erasing so drawing works immediately.
        if (erasing) {
            erasing = false;
            setCursor(Cursor.getDefaultCursor());
        }
    }

    private void toggleErase() {
        erasing = !erasing;

        if (erasing) {
            // CHANGED: When entering erase mode, cancel any shape preview by switching tool to NONE
            currentMode = DrawingMode.NONE;
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        } else {
            setCursor(Cursor.getDefaultCursor());
            // leave currentMode as NONE (user will click a shape tool to resume)
        }
    }

    private void saveCanvas() {
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save Image");
            fileChooser.setSelectedFile(new File("drawing.png"));
            int result = fileChooser.showSaveDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                // Save only the canvas image
                ImageIO.write(canvasImage, "PNG", file);
                JOptionPane.showMessageDialog(this, "Image saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving image: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Open Image");
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                File file = fileChooser.getSelectedFile();
                BufferedImage loadedImage = ImageIO.read(file);
                
                // Draw the loaded image onto our canvas
                canvasGraphics.drawImage(loadedImage, 0, 0, null);
                repaint();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error loading image: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
      
    
}