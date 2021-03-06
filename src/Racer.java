import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Vector;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class Racer {
    public Racer() throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        setup();
    }

    public static void setup() throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        playAudio();
        appFrame = new JFrame("2DRacer");
        XOFFSET = 0;
        YOFFSET = 40;
        WINWIDTH = 790;
        WINHEIGHT = 470;
        pi = 3.14159265358979;
        twoPi = 2.0 * 3.14159265358979;
        endgame = false;
        p1width = 25;
        p1height = 25;
        p2width = 25;
        p2height = 25;
        p1originalX = (double) XOFFSET + ((double) WINWIDTH / 2.0) - (p1width / 2.0);
        p1originalY = (double) YOFFSET + ((double) WINWIDTH / 2.0) - (p1width / 2.0);
        p2originalX = (double) XOFFSET + ((double) WINWIDTH / 2.0) - (p1width / 2.0);
        p2originalY = (double) YOFFSET + ((double) WINWIDTH / 2.0) - (p1width / 2.0);
        currentLapP1 = 0;
        currentLapP2 = 0;

        grassX = 190;
        grassY = 300;
        grassWidth = 146;
        grassHeight = 84;
        grassAngle = 0.0;

        c1X = 397;
        c1Y = 403;
        c1width = 25;
        c1height = 49;
        c1angle = 0.0;

        /*
        timeX = 380;
        timeY = 403;
        timeWidth = 25;
        timeHeight = 49;
        timeAngle = 0.0;

         */

        c2X = 486;
        c2Y = 83;
        c2width = 25;
        c2height = 49;
        c2angle = 0.0;

        tX = 0;
        tY = 0;
        twidth = 801;
        theight = 9;
        tangle = 0.0;

        bX = 50;
        bY = 470;
        bwidth = 801;
        bheight = 9;
        bangle = 0.0;

        lX = 0;
        lY = 0;
        lwidth = 9;
        lheight = 500;
        langle = 0.0;

        rX = 790;
        rY = 0;
        rwidth = 9;
        rheight = 500;
        rangle = 0.0;

        fbX = 430;
        fbY = 200;
        fbWidth = 9;
        fbHeight = 200;
        fbAngle = 0.0;

        sbX = 705;
        sbY = 310;
        sbWidth = 9;
        sbHeight = 500;
        sbAngle = 0.0;

        p1c1active = false;
        p2c1active = false;
        p1c2active = true;
        p2c2active = true;

        //timeP1Active = true;
        //timeP2Active = true;

        try {
            firstBarrierImage = ImageIO.read(new File("firstBarrier.png"));
            secondBarrierImage = ImageIO.read(new File("secondBarrier.png"));
            explosion = ImageIO.read(new File("explosion.png"));
            topWall = ImageIO.read(new File("topWall.png"));
            bottomWall = ImageIO.read(new File("bottomWall.png"));
            rightWall = ImageIO.read(new File("rightWall.png"));
            leftWall = ImageIO.read(new File("leftWall.png"));
            p1wins = ImageIO.read(new File("player1wins.png"));
            p2wins = ImageIO.read(new File("player2wins.png"));
            p2winText = ImageIO.read(new File("p2winText.png"));
            p1winText = ImageIO.read(new File("p1wins.png"));
            background = ImageIO.read(new File("track1.png"));
            player1 = ImageIO.read(new File("Player1.png"));
            player2 = ImageIO.read(new File("Player2.png"));
            orangeCar = ImageIO.read(new File("orangeLambo.png"));
            carBG = ImageIO.read(new File("carBG.png"));
            blueCar = ImageIO.read(new File("blueLambo.png"));
            check1 = ImageIO.read(new File("check1.png"));
            slash = ImageIO.read(new File("slash.png"));
            zero = ImageIO.read(new File("number_0.png"));
            one = ImageIO.read(new File("number_1.png"));
            two = ImageIO.read(new File("number_2.png"));
            three = ImageIO.read(new File("number_3.png"));
            four = ImageIO.read(new File("number_4.png"));
            five = ImageIO.read(new File("number_5.png"));
            six = ImageIO.read(new File("number_6.png"));
            seven = ImageIO.read(new File("number_7.png"));
            eight = ImageIO.read(new File("number_8.png"));
            nine = ImageIO.read(new File("number_9.png"));
            ten = ImageIO.read(new File("number_10.png"));
            check1 = ImageIO.read(new File("check1.png"));
            check2 = ImageIO.read(new File("check2.png"));
            //timeCheck = ImageIO.read(new File("timeCheck.png"));
            grassImage = ImageIO.read(new File("grass.png"));
        } catch (IOException ioe) {

        }
    }

    private static class Animate implements Runnable {
        public void run() {
            while (!endgame) {
                backgroundDraw();
                checkpointsDraw();
                //wallsDraw();
                player1Draw();
                player2Draw();
                currentLapsDrawP1();
                currentLapsDrawP2();
                //grassDraw();

                try {
                    Thread.sleep(32);
                } catch (InterruptedException e) {
                    System.out.println("Error: " + e);
                }
            }
        }
    }

    private static class PlayerMover implements Runnable {
        public PlayerMover() {
            velocitystep = 0.01;
            rotatestep = 0.02;
        }

        public void run() {
            while (!endgame) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    System.out.println("Error: " + e);
                }

                if (upPressed) {
                    p1velocity = p1velocity + velocitystep;
                }
                else if (downPressed) {
                    p1velocity = p1velocity - velocitystep * 1.5;
                }
                else {
                    if (p1velocity >= 0) {
                        p1velocity = p1velocity - velocitystep * 0.5;
                    } else {
                        p1velocity = p1velocity + velocitystep * 0.5;
                    }
                }
                if (leftPressed) {
                    if (p1velocity < 0) {
                        p1.rotate(-rotatestep);
                    } else {
                        p1.rotate(rotatestep);
                    }
                }
                if (rightPressed) {
                    if (p1velocity < 0) {
                        p1.rotate(rotatestep);
                    } else {
                        p1.rotate(-rotatestep);
                    }
                }

                // FIXME match control logic
                if (wPressed) {
                    p2velocity = p2velocity + velocitystep;
                }
                else if (sPressed) {
                    p2velocity = p2velocity - velocitystep * 1.5;
                } else {
                    if (p2velocity >= 0) {
                        p2velocity = p2velocity - velocitystep * 0.5;
                    } else {
                        p2velocity = p2velocity + velocitystep * 0.5;
                    }
                }
                if (aPressed) {
                    if (p2velocity < 0) {
                        p2.rotate(-rotatestep);
                    } else {
                        p2.rotate(rotatestep);
                    }
                }
                if (dPressed) {
                    if (p2velocity < 0) {
                        p2.rotate(rotatestep);
                    } else {
                        p2.rotate(-rotatestep);
                    }
                }

                p1.move(-p1velocity * Math.cos(p1.getAngle() - pi / 2.0),
                        p1velocity * Math.sin(p1.getAngle() - pi / 2.0));
                //p1.screenWrap(XOFFSET, XOFFSET + WINWIDTH, YOFFSET, YOFFSET + WINHEIGHT);

                p2.move(-p2velocity * Math.cos(p2.getAngle() - pi / 2.0),
                        p2velocity * Math.sin(p2.getAngle() - pi / 2.0));
                //p2.screenWrap(XOFFSET, XOFFSET + WINWIDTH, YOFFSET, YOFFSET + WINHEIGHT);
            }
        }

        private double velocitystep;
        private double rotatestep;
    }


    private static class CollisionChecker implements Runnable {
        public void run() {
            while (!endgame) {
                DecimalFormat df = new DecimalFormat("#.##");
                startTimeP1 = System.nanoTime();
                startTimeP2 = System.nanoTime();
                System.out.println("P1 Speed: " + df.format(p1velocity));
                System.out.println("\tP2 Speed: " + df.format(p2velocity));
                //p1 grass slow
                if (collisionOccurs(p1, grass)) {
                    p1velocity = p1velocity * 0.9999;
                }

                //p2 grass slow
                if (collisionOccurs(p2, grass)) {
                    p2velocity = p2velocity * 0.9999;
                }

                //p1 lap count
                if (collisionOccurs(p1, c1) && p1c1active) {
                    endTimeP1 = System.nanoTime();
                    double elapsedTime = startTimeP1 - endTimeP1;
                    double seconds = elapsedTime;
                    System.out.println("P1 Lap " + currentLapP1 + " Time: " + df.format(seconds));
                    currentLapP1 += 1;
                    System.out.println("p1 lap up: " + currentLapP1);
                    p1c1active = false;
                    p1c2active = true;

                }
                if (collisionOccurs(p1, c2) && p1c2active) {
                    System.out.println("p1 checkpoint");
                    p1c2active = false;
                    p1c1active = true;
                }

                /*
                if (collisionOccurs(p1, time) && timeP1Active) {
                    startTimeP1 = System.nanoTime();
                }


                if (collisionOccurs(p2, time) && timeP2Active) {
                    startTimeP2 = System.nanoTime();
                }

                 */

                //p2 lap count
                if (collisionOccurs(p2, c1) && p2c1active) {
                    endTimeP2 = System.nanoTime();
                    double elapsedTime = startTimeP2 - endTimeP2;
                    double seconds = elapsedTime / 1_000_000_000.0;
                    System.out.println("P2 Lap " + currentLapP2 + " Time: " + df.format(seconds));
                    currentLapP2 += 1;
                    System.out.println("p2 lap up: " + currentLapP2);
                    p2c1active = false;
                    p2c2active = true;
                }

                if (collisionOccurs(p2, c2) && p2c2active) {
                    System.out.println("p2 checkpoint");
                    p2c2active = false;
                    p2c1active = true;
                }

                //p1TopWallCollision
                if (collisionOccurs(p1, top)) {
                    explosionDraw(p1);
                    p1velocity = 0.0;
                }

                //p2TopWallCollision
                if (collisionOccurs(p2, top)) {
                    explosionDraw(p2);
                    p2velocity = 0.0;
                }

                //p1RightWallCollision
                if (collisionOccurs(p1, right)) {
                    explosionDraw(p1);
                    p1velocity = 0.0;
                }

                //p2RightWallCollision
                if (collisionOccurs(p2, right)) {
                    explosionDraw(p2);
                    p2velocity = 0.0;
                }

                //p1LeftWallCollision
                if (collisionOccurs(p1, left)) {
                    explosionDraw(p1);
                    p1velocity = 0.0;
                }

                //p2LeftWallCollision
                if (collisionOccurs(p2, left)) {
                    explosionDraw(p2);
                    p2velocity = 0.0;
                }

                //p1BottomWallCollision
                if (collisionOccurs(p1, bottom)) {
                    explosionDraw(p1);
                    p1velocity = 0.0;
                }

                //p2BottomWallCollision
                if (collisionOccurs(p2, bottom)) {
                    explosionDraw(p2);
                    p2velocity = 0.0;
                }

                //first barrier collision
                if (collisionOccurs(p1, firstBarrier)) {
                    explosionDraw(p1);
                    p1velocity = 0.0;
                }

                //first barrier collision
                if (collisionOccurs(p2, firstBarrier)) {
                    explosionDraw(p2);
                    p2velocity = 0.0;
                }

                //second barrier collision
                if (collisionOccurs(p1, secondBarrier)) {
                    explosionDraw(p1);
                    p1velocity = 0.0;
                }

                //second barrier collision
                if (collisionOccurs(p2, secondBarrier)) {
                    explosionDraw(p2);
                    p2velocity = 0.0;
                }


                if (currentLapP1 >= currentLapP2) {
                    lapCount = currentLapP1;
                    p1won = true;
                } else {
                    lapCount = currentLapP2;
                    p2won = true;
                }
            }
        }
    }

    private static class WinChecker implements Runnable {
        public void run() {
            while (!endgame) {
                Graphics g = appFrame.getGraphics();
                Graphics2D g2D = (Graphics2D) g;
                //System.out.println("lapCount" + lapCount);
                //System.out.println("maxLap" + maxLapNum);
                if (maxLapNum == 0) {
                    maxLapNum = 3;
                }
                if (lapCount >= maxLapNum && lapCount != 0) {
                    if (p1won) {
                        g2D.drawImage(p1wins, XOFFSET, YOFFSET, null);
                        g2D.drawImage(p1winText, XOFFSET + 220, YOFFSET + 50, null);
                        endgame = true;
                        System.out.println("P1 Wins!");
                    }if (p2won) {
                        g2D.drawImage(p2wins, XOFFSET, YOFFSET, null);
                        g2D.drawImage(p2winText, XOFFSET + 220, YOFFSET + 50, null);
                        endgame = true;
                        System.out.println("P2 Wins!");
                    }
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
        AffineTransform at = AffineTransform.getRotateInstance(-obj.getAngle(), obj.getHeight() / 2.0,
                obj.getWidth() / 2.0);
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
        //g2D.drawImage(rotateImageObject(c1).filter(check1, null), (int) (c1.getX()), (int) (c1.getY()), null);
        //g2D.drawImage(rotateImageObject(c2).filter(check1, null), (int) (c2.getX()), (int) (c2.getY()), null);
    }


    /*
    private static void wallsDraw() {
        Graphics g = appFrame.getGraphics();
        Graphics2D g2D = (Graphics2D) g;

        g2D.drawImage(rotateImageObject(top).filter(topWall, null), (int) (top.getX()), (int) (top.getY()), null);
        g2D.drawImage(rotateImageObject(bottom).filter(bottomWall, null), (int) (bottom.getX()), (int) (bottom.getY()),null);
        g2D.drawImage(rotateImageObject(right).filter(rightWall, null), (int) (right.getX()), (int) (right.getY()), null);
        g2D.drawImage(rotateImageObject(left).filter(leftWall, null), (int) (left.getX()), (int) (left.getY()), null);
        g2D.drawImage(rotateImageObject(firstBarrier).filter(firstBarrierImage, null), (int) (firstBarrier.getX()), (int) (firstBarrier.getY()), null);
        g2D.drawImage(rotateImageObject(secondBarrier).filter(secondBarrierImage, null), (int) (secondBarrier.getX()), (int) (secondBarrier.getY()), null);
    }

     */

    private static void grassDraw() {
        Graphics g = appFrame.getGraphics();
        Graphics2D g2D = (Graphics2D) g;
        g2D.drawImage(rotateImageObject(grass).filter(grassImage, null), (int) (grass.getX()), (int) (grass.getY()), null);
    }

    private static void backgroundDraw() {
        Graphics g = appFrame.getGraphics();
        Graphics2D g2D = (Graphics2D) g;
        g2D.drawImage(background, XOFFSET, YOFFSET, null);
        g2D.drawImage(carBG, XOFFSET, YOFFSET + 430, null);
        g2D.drawImage(orangeCar, XOFFSET, YOFFSET + 430, null);
        g2D.drawImage(blueCar, XOFFSET + 560, YOFFSET + 422, null);
    }

    private static void explosionDraw(ImageObject player) {
        Graphics g = appFrame.getGraphics();
        Graphics2D g2D = (Graphics2D) g;
        g2D.drawImage(explosion, (int) (player.getX()), (int) (player.getY() + 11), null);
    }

    //orange car
    private static void currentLapsDrawP1() {
        Graphics g = appFrame.getGraphics();
        Graphics2D g2D = (Graphics2D) g;

        switch (currentLapP1) {
            case 1 -> g2D.drawImage(one, XOFFSET + 205, YOFFSET + 465, null);
            case 2 -> g2D.drawImage(two, XOFFSET + 205, YOFFSET + 465, null);
            case 3 -> g2D.drawImage(three, XOFFSET + 205, YOFFSET + 465, null);
            case 4 -> g2D.drawImage(four, XOFFSET + 205, YOFFSET + 465, null);
            case 5 -> g2D.drawImage(five, XOFFSET + 205, YOFFSET + 465, null);
            case 6 -> g2D.drawImage(six, XOFFSET + 205, YOFFSET + 465, null);
            case 7 -> g2D.drawImage(seven, XOFFSET + 205, YOFFSET + 465, null);
            case 8 -> g2D.drawImage(eight, XOFFSET + 205, YOFFSET + 465, null);
            case 9 -> g2D.drawImage(nine, XOFFSET + 205, YOFFSET + 465, null);
            case 10 -> g2D.drawImage(ten, XOFFSET + 205, YOFFSET + 465, null);
            default -> g2D.drawImage(zero, XOFFSET + 205, YOFFSET + 465, null);
        };

        g2D.drawImage(slash, XOFFSET + 225, YOFFSET + 455, null);

        switch (maxLapNum) {
            case 1 -> g2D.drawImage(one, XOFFSET + 245, YOFFSET + 465, null);
            case 2 -> g2D.drawImage(two, XOFFSET + 245, YOFFSET + 465, null);
            case 4 -> g2D.drawImage(four, XOFFSET + 245, YOFFSET + 465, null);
            case 5 -> g2D.drawImage(five, XOFFSET + 245, YOFFSET + 465, null);
            case 6 -> g2D.drawImage(six, XOFFSET + 245, YOFFSET + 465, null);
            case 7 -> g2D.drawImage(seven, XOFFSET + 245, YOFFSET + 465, null);
            case 8 -> g2D.drawImage(eight, XOFFSET + 245, YOFFSET + 465, null);
            case 9 -> g2D.drawImage(nine, XOFFSET + 245, YOFFSET + 465, null);
            case 10 -> g2D.drawImage(ten, XOFFSET + 245, YOFFSET + 465, null);
            default -> g2D.drawImage(three, XOFFSET + 245, YOFFSET + 465, null);
        };
    }

    //blue car
    private static void currentLapsDrawP2() {
        Graphics g = appFrame.getGraphics();
        Graphics2D g2D = (Graphics2D) g;

        switch (currentLapP2) {
            case 1 -> g2D.drawImage(one, XOFFSET + 480, YOFFSET + 465, null);
            case 2 -> g2D.drawImage(two, XOFFSET + 480, YOFFSET + 465, null);
            case 3 -> g2D.drawImage(three, XOFFSET + 480, YOFFSET + 465, null);
            case 4 -> g2D.drawImage(four, XOFFSET + 480, YOFFSET + 465, null);
            case 5 -> g2D.drawImage(five, XOFFSET + 480, YOFFSET + 465, null);
            case 6 -> g2D.drawImage(six, XOFFSET + 480, YOFFSET + 465, null);
            case 7 -> g2D.drawImage(seven, XOFFSET + 480, YOFFSET + 465, null);
            case 8 -> g2D.drawImage(eight, XOFFSET + 480, YOFFSET + 465, null);
            case 9 -> g2D.drawImage(nine, XOFFSET + 480, YOFFSET + 465, null);
            case 10 -> g2D.drawImage(ten, XOFFSET + 480, YOFFSET + 465, null);
            default -> g2D.drawImage(zero, XOFFSET + 480, YOFFSET + 465, null);
        };

        g2D.drawImage(slash, XOFFSET + 500, YOFFSET + 455, null);

        switch (maxLapNum) {
            case 1 -> g2D.drawImage(one, XOFFSET + 520, YOFFSET + 465, null);
            case 2 -> g2D.drawImage(two, XOFFSET + 520, YOFFSET + 465, null);
            case 4 -> g2D.drawImage(four, XOFFSET + 520, YOFFSET + 465, null);
            case 5 -> g2D.drawImage(five, XOFFSET + 520, YOFFSET + 465, null);
            case 6 -> g2D.drawImage(six, XOFFSET + 520, YOFFSET + 465, null);
            case 7 -> g2D.drawImage(seven, XOFFSET + 520, YOFFSET + 465, null);
            case 8 -> g2D.drawImage(eight, XOFFSET + 520, YOFFSET + 465, null);
            case 9 -> g2D.drawImage(nine, XOFFSET + 520, YOFFSET + 465, null);
            case 10 -> g2D.drawImage(ten, XOFFSET + 520, YOFFSET + 465, null);
            default -> g2D.drawImage(three, XOFFSET + 520, YOFFSET + 465, null);
        };

    }

    private static void player1Draw() {
        Graphics g = appFrame.getGraphics();
        Graphics2D g2D = (Graphics2D) g;
        g2D.drawImage(rotateImageObject(p1).filter(player1, null), (int) (p1.getX()), (int) (p1.getY() + 11), null);
    }

    private static void player2Draw() {
        Graphics g = appFrame.getGraphics();
        Graphics2D g2D = (Graphics2D) g;
        g2D.drawImage(rotateImageObject(p2).filter(player2, null), (int) (p2.getX()), (int) (p2.getY() - 13), null);
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
            if (action.equals("W")) {
                wPressed = true;
            }
            if (action.equals("S")) {
                sPressed = true;
            }
            if (action.equals("A")) {
                aPressed = true;
            }
            if (action.equals("D")) {
                dPressed = true;
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
            if (action.equals("W")) {
                wPressed = false;
            }
            if (action.equals("S")) {
                sPressed = false;
            }
            if (action.equals("A")) {
                aPressed = false;
            }
            if (action.equals("D")) {
                dPressed = false;
            }
        }

        private String action;
    }

    private static class QuitGame implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            audioClip.close();
            try {
                audioStream.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
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
            wPressed = false;
            aPressed = false;
            sPressed = false;
            dPressed = false;

            p1 = new ImageObject(p1originalX, p1originalY, p1width, p1height, -1.6);
            p2 = new ImageObject(p2originalX, p2originalY, p2width, p2height, -1.6);
            //c1 = new ImageObject(397, 403, 25, 49, 0.0);
            //c2 = new ImageObject(407, 403, 25, 49, 0.0);
            grass = new ImageObject(grassX, grassY, grassWidth, grassHeight, grassAngle);
            c1 = new ImageObject(c1X, c1Y, c1width, c1height, c1angle);
            c2 = new ImageObject(c2X, c2Y, c2width, c2height, c2angle);
            //time = new ImageObject(timeX, timeY, timeWidth, timeHeight, timeAngle);
            top = new ImageObject(tX, tY, twidth, theight, tangle);
            bottom = new ImageObject(bX, bY, bwidth, bheight, bangle);
            left = new ImageObject(lX, lY, lwidth, lheight, langle);
            right = new ImageObject(rX, rY, rwidth, rheight, rangle);
            firstBarrier = new ImageObject(fbX, fbY, fbWidth, fbHeight, fbAngle);
            secondBarrier = new ImageObject(sbX, sbY, sbWidth, sbHeight, sbAngle);
            p1velocity = 0.0;
            p2velocity = 0.0;
            //expcount = 1;
            try {
                Thread.sleep(50);
            } catch (InterruptedException ie) {

            }
            endgame = false;

            Thread t1 = new Thread(new Animate());
            Thread t2 = new Thread(new PlayerMover());
            Thread t3 = new Thread(new CollisionChecker());
            Thread t4 = new Thread(new WinChecker());
            t1.start();
            t2.start();
            t3.start();
            t4.start();
        }
    }


    private static class MaxLaps implements ActionListener {
        public int decodeLevel(String input) {
            return switch (input) {
                case "One" -> 1;
                case "Two" -> 2;
                //case "Three" -> 3;
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
            maxLapNum = decodeLevel(textLevel);
            System.out.println("MaxLap: " + maxLapNum);
        }
    }


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

    private static void playAudio() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        File audioFile = new File("videoplayback.wav");
        audioStream = AudioSystem.getAudioInputStream(audioFile);
        AudioFormat format = audioStream.getFormat();
        DataLine.Info info = new DataLine.Info(Clip.class, format);
        audioClip = (Clip) AudioSystem.getLine(info);
        audioClip.open(audioStream);
        audioClip.start();
    }


    public static void main(String[] args) throws IOException, UnsupportedAudioFileException, LineUnavailableException {
        setup();

        appFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        appFrame.setSize(790, 600);

        JPanel myPanel = new JPanel();

        appFrame.getContentPane().add(myPanel, "South");
        appFrame.setVisible(true);

        String[] levels = {"One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten"};
        JLabel label1 = new JLabel("Max Laps:");
        myPanel.add(label1);
        JComboBox<String> levelMenu = new JComboBox<>(levels);
        levelMenu.setSelectedIndex(2);
        levelMenu.addActionListener(new MaxLaps());
        myPanel.add(levelMenu);

        JButton newGameButton = new JButton("New Game");
        newGameButton.addActionListener(new StartGame());
        myPanel.add(newGameButton);

        JButton quitButton = new JButton("Quit Game");
        quitButton.addActionListener(new QuitGame());
        myPanel.add(quitButton);


        Intro = ImageIO.read(new File("Intro.png"));
        title = ImageIO.read(new File("title.png"));
        Graphics g = appFrame.getGraphics();
        Graphics2D g2D = (Graphics2D) g;
        g2D.drawImage(Intro, XOFFSET, YOFFSET, null);
        g2D.drawImage(title, XOFFSET + 20, YOFFSET, null);

        bindKey(myPanel, "UP");
        bindKey(myPanel, "DOWN");
        bindKey(myPanel, "LEFT");
        bindKey(myPanel, "RIGHT");
        bindKey(myPanel, "W");
        bindKey(myPanel, "S");
        bindKey(myPanel, "A");
        bindKey(myPanel, "D");
        bindKey(myPanel, "F");
        appFrame.getContentPane().add(myPanel, "South");
        appFrame.setVisible(true);
    }

    private static Boolean endgame;
    private static BufferedImage Intro;
    private static BufferedImage title;
    private static BufferedImage background;
    private static BufferedImage orangeCar;
    private static BufferedImage blueCar;
    private static BufferedImage carBG;
    private static BufferedImage player1;
    private static BufferedImage player2;
    private static BufferedImage zero;
    private static BufferedImage one;
    private static BufferedImage two;
    private static BufferedImage three;
    private static BufferedImage four;
    private static BufferedImage five;
    private static BufferedImage six;
    private static BufferedImage seven;
    private static BufferedImage eight;
    private static BufferedImage nine;
    private static BufferedImage ten;
    private static BufferedImage slash;
    private static BufferedImage p1wins;
    private static BufferedImage p2wins;
    private static BufferedImage p1winText;
    private static BufferedImage p2winText;
    private static BufferedImage explosion;

    private static BufferedImage rightWall;
    private static ImageObject right;
    private static double rX;
    private static double rY;
    private static double rwidth;
    private static double rheight;
    private static double rangle;

    private static BufferedImage leftWall;
    private static ImageObject left;
    private static double lX;
    private static double lY;
    private static double lwidth;
    private static double lheight;
    private static double langle;

    private static BufferedImage topWall;
    private static ImageObject top;
    private static double tX;
    private static double tY;
    private static double twidth;
    private static double theight;
    private static double tangle;

    private static BufferedImage bottomWall;
    private static ImageObject bottom;
    private static double bX;
    private static double bY;
    private static double bwidth;
    private static double bheight;
    private static double bangle;

    private static BufferedImage firstBarrierImage;
    private static ImageObject firstBarrier;
    private static double fbX;
    private static double fbY;
    private static double fbWidth;
    private static double fbHeight;
    private static double fbAngle;

    private static BufferedImage secondBarrierImage;
    private static ImageObject secondBarrier;
    private static double sbX;
    private static double sbY;
    private static double sbWidth;
    private static double sbHeight;
    private static double sbAngle;


    private static Boolean upPressed;
    private static Boolean downPressed;
    private static Boolean leftPressed;
    private static Boolean rightPressed;

    private static Boolean wPressed;
    private static Boolean aPressed;
    private static Boolean sPressed;
    private static Boolean dPressed;

    private static ImageObject p1;
    private static double p1width;
    private static double p1height;
    private static double p1originalX;
    private static double p1originalY;
    private static double p1velocity;

    private static BufferedImage grassImage;
    private static ImageObject grass;
    private static double grassX;
    private static double grassY;
    private static double grassWidth;
    private static double grassHeight;
    private static double grassAngle;

    private static BufferedImage check1;
    private static ImageObject c1;
    private static double c1X;
    private static double c1Y;
    private static double c1width;
    private static double c1height;
    private static double c1angle;
    private static boolean p1c1active;
    private static boolean p2c1active;

    /*
    private static BufferedImage timeCheck;
    private static ImageObject time;
    private static double timeX;
    private static double timeY;
    private static double timeWidth;
    private static double timeHeight;
    private static double timeAngle;
    private static boolean timeP1Active;
    private static boolean timeP2Active;

     */

    private static BufferedImage check2;
    private static ImageObject c2;
    private static double c2X;
    private static double c2Y;
    private static double c2width;
    private static double c2height;
    private static double c2angle;
    private static boolean p1c2active;
    private static boolean p2c2active;

    private static ImageObject p2;
    private static double p2width;
    private static double p2height;
    private static double p2originalX;
    private static double p2originalY;
    private static double p2velocity;

    private static int maxLapNum;
    private static int currentLapP1;
    private static int currentLapP2;
    private static int lapCount;

    private static boolean p1won;
    private static boolean p2won;

    private static long startTimeP1;
    private static long startTimeP2;

    private static double endTimeP1;
    private static double endTimeP2;


    private static int expcount;
    private static int XOFFSET;
    private static int YOFFSET;
    private static int WINWIDTH;
    private static int WINHEIGHT;

    private static double pi;
    private static double twoPi;

    private static JFrame appFrame;

    private static Clip audioClip;
    private static AudioInputStream audioStream;

    private static final int IFW = JComponent.WHEN_IN_FOCUSED_WINDOW;
}

