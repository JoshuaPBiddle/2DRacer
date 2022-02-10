import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalTime;
import java.util.Random;
import java.util.Vector;

public class Racer {
    public Racer() {
        setup();
    }

    public static void setup() {
        appFrame = new JFrame("2DRacer");
        XOFFSET = 0;
        YOFFSET = 40;
        WINWIDTH = 801;
        WINHEIGHT = 500;
        pi = 3.14159265358979;
        twoPi = 2.0 * 3.14159265358979;
        endgame = false;
        p1width = 25;
        p1height = 25;
        p1originalX = (double) XOFFSET + ((double) WINWIDTH / 2.0) - (p1width / 2.0);
        p1originalY = (double) YOFFSET + ((double) WINWIDTH / 2.0) - (p1width / 2.0);



        p1LapCount = 0;

        try {
            background = ImageIO.read(new File("track1.png"));
            player1 = ImageIO.read(new File("player1.png"));
            player2 = ImageIO.read(new File("player2.png"));
            check1 = ImageIO.read(new File("check1.png"));
        } catch (IOException ioe) {

        }
    }

    private static class Animate implements Runnable {
        public void run() {
            while (!endgame) {
                backgroundDraw();
                checkpointsDraw();
                player1Draw();
                player2Draw();

                try {
                    Thread.sleep(32);
                } catch (InterruptedException e) {

                }
            }
        }
    }

    private static class PlayerMover implements Runnable {
        public PlayerMover() {
            velocitystep = 0.01;
            rotatestep = 0.01;
        }

        public void run() {
            while (!endgame) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {

                }

                if (upPressed) {
                    p1velocity = p1velocity + velocitystep;
                }
                else if (downPressed) {
                    p1velocity = p1velocity - velocitystep;
                }
                else {  // if no buttons are pressed, slow down
                    if (p1velocity > 0) {
                        p1velocity = p1velocity - velocitystep;
                    } else {
                        p1velocity = p1velocity + velocitystep;
                    }
                }
                if (leftPressed && p1velocity != 0) {
                    if (p1velocity < 0) {
                        p1.rotate(-rotatestep);
                    } else {
                        p1.rotate(rotatestep);
                    }
                }
                if (rightPressed && p1velocity != 0) {
                    if (p1velocity < 0) {
                        p1.rotate(rotatestep);
                    } else {
                        p1.rotate(-rotatestep);
                    }
                }

                p1.move(-p1velocity * Math.cos(p1.getAngle() - pi / 2.0),
                        p1velocity * Math.sin(p1.getAngle() - pi / 2.0));
                p1.screenWrap(XOFFSET, XOFFSET + WINWIDTH, YOFFSET, YOFFSET + WINHEIGHT);
            }
        }

        private double velocitystep;
        private double rotatestep;
    }


    private static class CollisionChecker implements Runnable {

        public void run() {
            Random randomNumbers = new Random(LocalTime.now().getNano());

            while (!endgame) {
                // slow down cars off track

                // count lap
                if (collisionOccurs(p1, c1)) {
                    System.out.println(p1LapCount);
                }
                /*  FIXME   add upon p2 implementation
                if (collisionOccurs(p2, c1)) {
                    p2LapCount += 1;
                    System.out.println(p2LapCount);;
                }
                */
                if (p1LapCount >= p2LapCount) {
                    lapCount = p1LapCount;
                } else {
                    lapCount = p2LapCount;
                }
            }
        }
    }


    private static class WinChecker implements Runnable {
        public void run() {
            while (endgame = false) {
                if (lapCount >= 3) {
                    endgame = true;
                    System.out.println("Game Over. You Win!");
                }
            }
        }
    }


    private static void lockrotateObjAroundObjbottom(ImageObject objOuter, ImageObject objInner, double dist) {
        objOuter.moveto(objInner.getX() + (dist + objInner.getWidth() / 2.0) * Math.cos(-objInner.getAngle() + pi / 2.0) + objOuter.getWidth() / 2.0,
                objInner.getY() + (dist + objInner.getHeight() / 2.0) * Math.sin(-objInner.getAngle() + pi / 2.0)
                        + objOuter.getHeight() / 2.0);
        objOuter.setAngle(objInner.getAngle());
    }

    private static void lockrotateObjAroundObjtop(ImageObject objOuter, ImageObject objInner, double dist) {
        objOuter.moveto(objInner.getX() + objOuter.getWidth() + (objInner.getWidth() / 2.0 +
                        (dist + objInner.getWidth() / 2.0) * Math.cos(objInner.getAngle() + pi / 2.0)) / 2.0,
                objInner.getY() - objOuter.getHeight() + (dist + objInner.getHeight() / 2.0) * Math.sin(objInner.getAngle() / 2.0));

        objOuter.setAngle(objInner.getAngle());
    }

    private static AffineTransformOp rotateImageObject(ImageObject obj) {
        AffineTransform at = AffineTransform.getRotateInstance(-obj.getAngle(), obj.getWidth() / 2.0,
                obj.getHeight() / 2.0);
        AffineTransformOp atop = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
        return atop;
    }

    private static AffineTransformOp spinImageObject(ImageObject obj) {
        AffineTransform at = AffineTransform.getRotateInstance(-obj.getInternalangle(),
                obj.getWidth() / 2.0, obj.getHeight() / 2.0);
        AffineTransformOp atop = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
        return atop;
    }

    // FIXME remove before final version
    // this method draws visual indicators of the checkpoints
    private static void checkpointsDraw() {
        Graphics g = appFrame.getGraphics();
        Graphics2D g2D = (Graphics2D) g;
        g2D.drawImage(rotateImageObject(c1).filter(check1, null), (int)(c1.getX()), (int)(c1.getY()), null);
    }

    private static void backgroundDraw() {
        Graphics g = appFrame.getGraphics();
        Graphics2D g2D = (Graphics2D) g;
        g2D.drawImage(background, XOFFSET, YOFFSET, null);
    }

    private static void player1Draw() {
        Graphics g = appFrame.getGraphics();
        Graphics2D g2D = (Graphics2D) g;
        g2D.drawImage(rotateImageObject(p1).filter(player1, null), (int) (p1.getX() + 0.5), (int) (p1.getY() + 0.5), null);
    }

    private static void player2Draw() {
        Graphics g = appFrame.getGraphics();
        Graphics2D g2D = (Graphics2D) g;
        g2D.drawImage(rotateImageObject(p1).filter(player2, null), (int) (p1.getX() + 0.5), (int) (p1.getY() + 0.5), null);
    }

    private static class KeyPressed extends AbstractAction {
        public KeyPressed() {
            action = "";
        }

        public KeyPressed(String input) {
            action = input;
        }

        public void actionPerformed(ActionEvent e) {
            if (action.equals("UP")) {
                upPressed = true;
            }
            if (action.equals("DOWN")) {
                downPressed = true;
            }
            if (action.equals("LEFT")) {
                leftPressed = true;
            }
            if (action.equals("RIGHT")) {
                rightPressed = true;
            }
        }

        private String action;
    }

    public static class KeyReleased extends AbstractAction {
        public KeyReleased() {
            action = "";
        }

        public KeyReleased(String input) {
            action = input;
        }

        public void actionPerformed(ActionEvent e) {
            if (action.equals("UP")) {
                upPressed = false;
            }
            if (action.equals("DOWN")) {
                downPressed = false;
            }
            if (action.equals("LEFT")) {
                leftPressed = false;
            }
            if (action.equals("RIGHT")) {
                rightPressed = false;
            }
        }

        private String action;
    }

    private static class QuitGame implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            endgame = true;
        }
    }

    private static class StartGame implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            endgame = true;
            upPressed = false;
            downPressed = false;
            leftPressed = false;
            rightPressed = false;
            p1 = new ImageObject(p1originalX, p1originalY, p1width, p1height, 0.0);
            c1 = new ImageObject(397, 403, 25, 49, 0.0);
            p1velocity = 0.0;
            expcount = 1;
            try {
                Thread.sleep(50);
            } catch (InterruptedException ie) {

            }
            endgame = false;
            Thread t1 = new Thread(new Animate());
            Thread t2 = new Thread(new PlayerMover());
            Thread t3 = new Thread(new CollisionChecker());
            //Thread t9 = new Thread(new WinChecker());
            t1.start();
            t2.start();
            t3.start();
            //t9.start();
        }
    }

    /*
    private static class GameLevel implements ActionListener {
        public int decodeLevel(String input) {
            return switch (input) {
                case "One" -> 1;
                case "Two" -> 2;
                case "Four" -> 4;
                case "Five" -> 5;
                case "Six" -> 6;
                case "Seven" -> 7;
                case "Eight" -> 8;
                case "Nine" -> 9;
                case "Ten" -> 10;
                default -> 3;
            };
        }

        public void actionPerformed(ActionEvent e) {
            JComboBox cb = (JComboBox) e.getSource();
            String textLevel = (String) cb.getSelectedItem();
            level = decodeLevel(textLevel);
        }
    }
     */

    private static Boolean isInside(double p1x, double p1y, double p2x1, double p2y1, double p2x2, double p2y2) {
        Boolean ret = false;
        if (p1x > p2x1 && p1x < p2x2) {
            if (p1y > p2y1 && p1y < p2y2) {
                ret = true;
            }
            if (p1y > p2y2 && p1y < p2y1) {
                ret = true;
            }
        }
        if (p1x > p2x2 && p1x < p2x1) {
            if (p1y > p2y1 && p1y < p2y2) {
                ret = true;
            }
            if (p1y > p2y2 && p1y < p2y1) {
                ret = true;
            }
        }
        return ret;
    }


    public static Boolean collisionOccursCoordinates(double p1x1, double p1y1, double p1x2, double p1y2, double p2x1,
                                                     double p2y1, double p2x2, double p2y2) {
        return isInside(p1x1, p1y1, p2x1, p2y1, p2x2, p2y2) || isInside(p1x1, p1y2, p2x1, p2y1, p2x2, p2y2) ||
                isInside(p1x2, p1y1, p2x1, p2y1, p2x2, p2y2) || isInside(p1x2, p1y2, p2x1, p2y1, p2x2, p2y2) ||
                isInside(p2x1, p2y1, p1x1, p1y1, p1x2, p1y2) || isInside(p2x1, p2y2, p1x1, p1y1, p1x2, p1y2) ||
                isInside(p2x2, p2y1, p1x1, p1y1, p1x2, p1y2) || isInside(p2x2, p2y2, p1x1, p1y1, p1x2, p1y2);
    }

    private static Boolean collisionOccurs(ImageObject obj1, ImageObject obj2) {
        return collisionOccursCoordinates(obj1.getX(), obj1.getY(), obj1.getX() + obj1.getWidth(),
                obj1.getY() + obj1.getHeight(), obj2.getX(), obj2.getY(), obj2.getX() + obj2.getWidth(),
                obj2.getY() + obj2.getHeight());
    }

    private static class ImageObject {
        public ImageObject() {

        }

        public ImageObject(double xinput, double yinput, double xwidthinput, double yheightinput, double angleinput) {
            x = xinput;
            y = yinput;
            xwidth = xwidthinput;
            yheight = yheightinput;
            angle = angleinput;
            internalangle = 0.0;
            coords = new Vector<Double>();
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

        public double getWidth() {
            return xwidth;
        }

        public double getHeight() {
            return yheight;
        }

        public double getAngle() {
            return angle;
        }

        public double getInternalangle() {
            return internalangle;
        }

        public void setAngle(double angleinput) {
            angle = angleinput;
        }

        public void setInternalangle(double internalangleinput) {
            internalangle = internalangleinput;
        }

        public Vector<Double> getCoords() {
            return coords;
        }

        public void setCoords(Vector<Double> coordsinput) {
            coords = coordsinput;
            generateTriangles();
            //printTriangles();
        }

        public void generateTriangles() {
            triangles = new Vector<Double>();
            // format: (0, 1), (2, 3), (4, 5) is the x coords of a triangle

            // get center point of all coords
            comX = getComX();
            comY = getComY();

            for (int i = 0; i < coords.size(); i += 2) {
                triangles.addElement(coords.elementAt(i));
                triangles.addElement(coords.elementAt(i + 1));
                triangles.addElement(coords.elementAt((i + 2) % coords.size()));
                triangles.addElement(coords.elementAt((i + 3) % coords.size()));
                triangles.addElement(comX);
                triangles.addElement(comY);
            }
        }

        public void printTriangles() {
            for (int i = 0; i < triangles.size(); i += 6) {
                System.out.print("p0x: " + triangles.elementAt(i) + " p0y: " + triangles.elementAt(i + 1));
                System.out.print("p1x: " + triangles.elementAt(i + 2) + " p1y: " + triangles.elementAt(i + 3));
                System.out.print("p2x: " + triangles.elementAt(i + 4) + " p2y: " + triangles.elementAt(i + 5));
            }
        }

        public double getComX() {
            double ret = 0;
            if (coords.size() > 0) {
                for (int i = 0; i < coords.size(); i += 2) {
                    ret += coords.elementAt(i);
                }
                ret /= (coords.size() / 2.0);
            }
            return ret;
        }

        public double getComY() {
            double ret = 0;
            if (coords.size() > 0) {
                for (int i = 1; i < coords.size(); i += 2) {
                    ret += coords.elementAt(i);
                }
                ret /= (coords.size() / 2.0);
            }
            return ret;
        }

        public void move(double xinput, double yinput) {
            x += xinput;
            y += yinput;
        }

        public void moveto(double xinput, double yinput) {
            x = xinput;
            y = yinput;
        }

        public void screenWrap(double leftEdge, double rightEdge, double topEdge, double bottomEdge) {
            if (x > rightEdge) {
                moveto(leftEdge, getY());
            }
            if (x < leftEdge) {
                moveto(rightEdge, getY());
            }
            if (y > bottomEdge) {
                moveto(getX(), topEdge);
            }
            if (y < topEdge) {
                moveto(getX(), bottomEdge);
            }
        }

        public void rotate(double angleinput) {
            angle += angleinput;
            while (angle > twoPi) {
                angle -= twoPi;
            }

            while (angle < 0) {
                angle += twoPi;
            }
        }

        public void spin(double internalangleinput) {
            internalangle += internalangleinput;
            while (internalangle > twoPi) {
                internalangle -= twoPi;
            }

            while (internalangle < twoPi) {
                internalangle += twoPi;
            }
        }

        private double x;
        private double y;
        private double xwidth;
        private double yheight;
        private double angle;   // in Radians
        private double internalangle;   // in Radians
        private Vector<Double> coords;
        private Vector<Double> triangles;
        private double comX;
        private double comY;
    }

    private static void bindKey(JPanel myPanel, String input) {
        myPanel.getInputMap(IFW).put(KeyStroke.getKeyStroke("pressed " + input), input + " pressed");
        myPanel.getActionMap().put(input + " pressed", new KeyPressed(input));
        myPanel.getInputMap(IFW).put(KeyStroke.getKeyStroke("released " + input), input + " released");
        myPanel.getActionMap().put(input + " released", new KeyReleased(input));
    }


    public static void main(String[] args) {
        setup();
        appFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        appFrame.setSize(801, 500);

        JPanel myPanel = new JPanel();

        String[] levels = {"One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten"};
        JComboBox<String> levelMenu = new JComboBox<>(levels);
        levelMenu.setSelectedIndex(2);
        //levelMenu.addActionListener(new GameLevel());
        myPanel.add(levelMenu);

        JButton newGameButton = new JButton("New Game");
        newGameButton.addActionListener(new StartGame());
        myPanel.add(newGameButton);

        JButton quitButton = new JButton("Quit Game");
        quitButton.addActionListener(new QuitGame());
        myPanel.add(quitButton);

        bindKey(myPanel, "UP");
        bindKey(myPanel, "DOWN");
        bindKey(myPanel, "LEFT");
        bindKey(myPanel, "RIGHT");
        bindKey(myPanel, "F");

        appFrame.getContentPane().add(myPanel, "South");
        appFrame.setVisible(true);
    }

    private static Boolean endgame;
    private static BufferedImage background;
    private static BufferedImage player1;
    private static BufferedImage player2;
    private static BufferedImage check1;
    private static ImageObject c1;

    private static Boolean upPressed;
    private static Boolean downPressed;
    private static Boolean leftPressed;
    private static Boolean rightPressed;

    private static ImageObject p1;
    private static double p1width;
    private static double p1height;
    private static double p1originalX;
    private static double p1originalY;
    private static double p1velocity;
    private static int p1LapCount;

    private static ImageObject p2;
    private static double p2width;
    private static double p2height;
    private static double p2originalX;
    private static double p2originalY;
    private static double p2velocity;
    private static int p2LapCount;

    private static int level;
    private static int lapCount;

    private static int expcount;
    private static int XOFFSET;
    private static int YOFFSET;
    private static int WINWIDTH;
    private static int WINHEIGHT;

    private static double pi;
    private static double twoPi;

    private static JFrame appFrame;

    private static final int IFW = JComponent.WHEN_IN_FOCUSED_WINDOW;
}

