import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import java.util.ArrayList;

public class SimpleAutoCAD extends JFrame {
    private CanvasPanel canvas;
    private String currentShape = "Line";
    private boolean showGrid = false;

    public SimpleAutoCAD() {
        setTitle("Simple AutoCAD in Java");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Canvas
        canvas = new CanvasPanel();
        add(canvas, BorderLayout.CENTER);
        
        // Buttons
        JPanel controls = new JPanel();
        JButton lineButton = new JButton("Line");
        JButton rectButton = new JButton("Rectangle");
        JButton clearButton = new JButton("Clear");
        JButton gridButton = new JButton("Toggle Grid");
        
        lineButton.addActionListener(e -> currentShape = "Line");
        rectButton.addActionListener(e -> currentShape = "Rectangle");
        clearButton.addActionListener(e -> canvas.clearCanvas());
        gridButton.addActionListener(e -> {
            showGrid = !showGrid;
            canvas.repaint();
        });
        
        controls.add(lineButton);
        controls.add(rectButton);
        controls.add(clearButton);
        controls.add(gridButton);
        
        add(controls, BorderLayout.NORTH);
        
        setVisible(true);
    }

    private class CanvasPanel extends JPanel {
        private ArrayList<Shape> shapes = new ArrayList<>();
        private Point startPoint, currentPoint;
        private Shape previewShape;

        public CanvasPanel() {
            setBackground(Color.WHITE);
            addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    startPoint = e.getPoint();
                    currentPoint = startPoint;
                }
                public void mouseReleased(MouseEvent e) {
                    Point endPoint = e.getPoint();
                    if (currentShape.equals("Line")) {
                        shapes.add(new Line2D.Double(startPoint, endPoint));
                    } else if (currentShape.equals("Rectangle")) {
                        int x = Math.min(startPoint.x, endPoint.x);
                        int y = Math.min(startPoint.y, endPoint.y);
                        int width = Math.abs(startPoint.x - endPoint.x);
                        int height = Math.abs(startPoint.y - endPoint.y);
                        shapes.add(new Rectangle(x, y, width, height));
                    }
                    previewShape = null;
                    repaint();
                }
            });
            addMouseMotionListener(new MouseMotionAdapter() {
                public void mouseDragged(MouseEvent e) {
                    currentPoint = e.getPoint();
                    if (currentShape.equals("Line")) {
                        previewShape = new Line2D.Double(startPoint, currentPoint);
                    } else if (currentShape.equals("Rectangle")) {
                        int x = Math.min(startPoint.x, currentPoint.x);
                        int y = Math.min(startPoint.y, currentPoint.y);
                        int width = Math.abs(startPoint.x - currentPoint.x);
                        int height = Math.abs(startPoint.y - currentPoint.y);
                        previewShape = new Rectangle(x, y, width, height);
                    }
                    repaint();
                }
            });
        }

        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            
            if (showGrid) {
                g2.setColor(Color.LIGHT_GRAY);
                for (int i = 0; i < getWidth(); i += 20) {
                    g2.drawLine(i, 0, i, getHeight());
                }
                for (int i = 0; i < getHeight(); i += 20) {
                    g2.drawLine(0, i, getWidth(), i);
                }
            }
            
            g2.setColor(Color.BLACK);
            for (Shape shape : shapes) {
                g2.draw(shape);
            }
            
            if (previewShape != null) {
                g2.setColor(Color.RED);
                g2.draw(previewShape);
            }
        }

        public void clearCanvas() {
            shapes.clear();
            repaint();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SimpleAutoCAD::new);
    }
}
