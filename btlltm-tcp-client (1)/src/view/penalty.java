/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

/**
 *
 * @author Admin
 */
//these are used for the graphics windows and events
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class penalty extends JPanel {

    private Timer timer;        //This updates the graphics.
    static boolean shotTaken;
    boolean goalScored;
    static Ball player;
    static Keeper keeper;
    static penalty newGame;

    int centreX = getWidth()/2; //measures the screen and returns it's width halved, getting the centre of the X axis
    int centreY = getHeight()/2; //same above for height

    public penalty() {//the game constructor which contains the timer, and the action listener, which responds to our inputs.
        setBackground(Color.gray); //choose the window's background colour. There are several default options, though you can choose your own colour as well using Color(r,b,g).
        ActionListener playFrame = new ActionListener() {// this updates the animation every time it is called.
        public void actionPerformed(ActionEvent evt) {
//            player.shotTaken(); //this moves the ball once the shot is taken.
            repaint();// refresh the graphics.
        }
    };
        timer = new Timer( 25, playFrame );  // runs every 30 milliseconds.
   } // end constructor

   public void paintComponent(Graphics graphics) {
        //this draws your graphics to the screen, and is automatically called by a JPanel when the window is visible
        //never call this directly, instead, use repaint(); to refresh your graphics.
        super.paintComponent(graphics); //calling super like this allows you to paint on top of the background

        if (player == null) { // The first time that paintComponent is called, initialise the player and variables.
        player = new Ball();

        keeper = new Keeper();
        
//        keeper.whereToSave = ;
        
        timer.start(); // start the timer, which will make our animations move!
    }
        //lets draw the field and the goal
        Graphics2D drawnObject = (Graphics2D) graphics; //drawnObject is like any object, use any name you want - g is common
        //It is handy to have set variables for position and size, allows you to quickly alter the scale and position of your graphics without much fuss
        centreX = getWidth()/2; //measures the screen and returns it's width halved, getting the centre of the X axis
        centreY = getHeight()/2; //same above for height
        //lets draw the field first. Whatever is drawn first will be rendered first, so the draw order is important to create depth in your graphics
        drawnObject.setColor(new Color(50, 135, 65)); //set the colour for the field. You can use default colours, or this new Colour class to make RGB colours. RGB controls the amount of red-green-blue in a colour, from 0 to 255
        drawnObject.fillRect(0, centreY, centreX*2, centreY); //draw a fill rectangle, a block of colour. Set the width to centreX*2 to get the whole screen width.
        drawnObject.setColor(new Color(100, 90, 70)); //set a new colour. Everything drawn after this will be this colour, a brown-grey. This must be called for a colour change using primitive graphics
        drawnObject.fillRect(0, centreY, centreX*2, centreY/4); //draw another rectangle, for a wall behind the goal. Multiple draw functions can be used by one drawnObject.

        int width = centreX; //the width from x the rectangle will stretch to from its starting coordinate. This ensures that the goal's width will be half the window size.
        int height = centreY; //the height from y the rectangle will stretch down to from its starting coordinate. This ensures that the goal's height will be half the window size.
        int widthEight = (int)(width*0.125f);
        int heightEight = (int)(height*0.125f);
        int x = centreX - (width/2); // x coordinate for rectangle, where it will be drawn on the horizontal X axis.
        int y = centreY - (height/2); // y coordinate for rectangle, where it will be drawn on the vertical Y axis. Positive values move towards the screen's bottom
        // what we've done above is ensure that the rectangle we will draw will always be in the centre position.

        //next we will draw the back of our goal. We will draw the front of the goal AFTER we draw the back, so the posts are rendered IN FRONT of the net.
        Stroke strokeOuterGoal = new BasicStroke(6); // this is used for setting up a style of stroke. Right now, this just alters the width. Any drawnObject using this stroke will have a width of 6.
        Stroke strokeInnerGoal = new BasicStroke(3.5f); // A secondary stroke. Floats can be used for width, allowing for more precision
        Stroke strokeNet = new BasicStroke(1); // Another stroke setting, for the goal's net.

        drawnObject.setColor(Color.lightGray); //set the colour for the next set of drawings. we are using light gray to fake a depth effect, and make our graphics more readable

        drawnObject.setStroke(strokeInnerGoal); //change the stroke. All following drawings will use strokeInnerGoal size now
        drawnObject.drawRect(x+width/8, y+height/8, (width/8)*6, (height/8)*6); //draw another rectangle, this time just the outline, for the back of the net.
        //the settings of the rectangle above mean that, no matter what happens to the x,y,width and height variables, a smaller rectangle 3/4s the size of the original will be drawn.
        //This kind of thinking will help with position changes down the road. Of course, variables can be set up for those coordinates as well, if you prefer neater looking code

        drawnObject.drawLine(x, y, x+widthEight, y+heightEight); // draw a connecting line between the rectangles' top left corner.
        drawnObject.drawLine(x+(widthEight)*7, y+heightEight, x+width, y); // draw a connecting line between the rectangles' top right corner.
        drawnObject.drawLine(x, y+height, x+widthEight, y+(heightEight)*7); // draw a connecting line between the rectangles' bottom left corner.
        drawnObject.drawLine(x+(widthEight)*7, y+(heightEight)*7, x+width, y+height); // draw a connecting line between the rectangles' bottom right corner.
        //we use *7 above here to take into account that we are already drawing the smaller rectangle at 1/8 a distance from left to right.

        //for loops can be used in drawing objects. This would be useful for drawing tiles in a room, or other repetitive tasks, where the coordinates can be worked out incrementally
        //drawing the net lines vertically
        drawnObject.setStroke(strokeNet); //change the stroke. All following drawings will use strokeInnerRectangle now
        for (int netLines = 1; netLines < 7; netLines ++) {
            //here we are creating a new position for each strand of the net at every line
            int newWidth = x+(widthEight/2)+(widthEight)*netLines;
            //this draws the lines at the back of the net
            drawnObject.drawLine(newWidth, y+heightEight, newWidth, y+(height/8)*7);
            //these draw the lines at the top of the net
            if (netLines > 0 && netLines<7) {
                int topWidth = x-(widthEight/2)+(width/6)*netLines;
                drawnObject.drawLine(newWidth, y+heightEight, topWidth, y);
            }
        }
        //drawing the net lines horizontally
        for (int netLines = 1; netLines < 7; netLines ++) {
            //here we are creating a new position for each strand of the net at every line
            int newHeight = y+(heightEight/2)+(heightEight)*netLines;
            //this draws the lines at the back of the net
            drawnObject.drawLine(x+widthEight, newHeight, x+(widthEight)*7, newHeight);
            //these draw the lines at the sides of the net
            if (netLines > 0) {
                int sideHeight = y-(heightEight/2)+(height/6)*netLines;
                drawnObject.drawLine(x+widthEight, newHeight, x, sideHeight);//left
                drawnObject.drawLine(x+(widthEight)*7, newHeight, x+width, sideHeight);//right
            }
        }
        //draw the lines for the centre of the goals sides and the top. These could be drawn in the for loop with conditionals, but for clarity I've put them here
        drawnObject.drawLine(x+(widthEight/2), y+(heightEight/2), x+width-(widthEight/2), y+(heightEight/2));//top
        drawnObject.drawLine(x+(widthEight/2), y+(heightEight/2), x+widthEight/2, y+height-(heightEight/2));//left
        drawnObject.drawLine(x+width-(widthEight/2), y+(heightEight/2), x+width-(widthEight/2), y+height-(heightEight/2));//right

        //finally, draw the goal posts and top bar at the original width and height on top of the net, to preserve depth!
        drawnObject.setColor(Color.white); //set the colour for the next set of drawings
        drawnObject.setStroke(strokeOuterGoal); //set the size of the outer goal posts
        drawnObject.drawLine(x, y, x+width, y);//top
        drawnObject.drawLine(x, y, x, y+height);//left
        drawnObject.drawLine(x+width, y, x+width, y+height);//right

        //while the shot hasn't been taken, update our ball and keeper positions. This allows us to resize the screen
        if (shotTaken == false) {
            player.resetPosition();
            keeper.resetPosition();
        }

        //the code above now guarantees that the goal will be drawn correctly even if you change its position and its size.
        //instead of drawing everything here, we can call the draw functions of objects here. This will help keep this from becoming unwieldy, and help with keeping track of what is being drawn on top.
        //since the keeper should be drawn over the goal, but NOT the ball, we can call our existing Keeper object now

        keeper.draw((Graphics2D)graphics);
        //since the ball should appear to the player as in FRONT of both the keeper and the goal, we call its draw function here

        player.draw(graphics);
        drawnObject.setStroke(new BasicStroke(0f));
    }

//this is the ball class
    class Ball{
        //size and position variables
        int diameter = 60;//initial size of the ball
        int x = centreX; //initial position of x
        int y = (int)(centreY*1.75f);//initial position of y
        int xMove, yMove, xCurvingEffect, yCurvingEffect;

        void shotTaken(String shootingFor){
            if (shootingFor != "") {
                //works only once shootingFor is not empty
                switch (shootingFor) {
                    case "left":
                        xMove = (int)((x - getWidth())*0.015f);
                        yMove = (int)((y - getHeight())*0.1f);
                        break;
                    case "centre":
                        xMove = 0;
                        yMove = (int)((y - getHeight())*0.09f);
                        break;
                    case "right":
                        xMove = (int)((x - getWidth())*-0.015f);
                        yMove = (int)((y - getHeight())*0.1f);
                        break;
                    default:
                        xMove = 0;
                        yMove = 0;
                        xCurvingEffect = 0;
                        yCurvingEffect = 0;
                        break;
                }
                //this moves the ball by the amounts above while the diameter is greater than 40
                x += xMove*2;
                y += yMove*2;

                //this curving effect makes it look like the ball is raising and falling smoothly.
                //there are a few ways to do this, using the halfway point between our start and endpoint for example.
                //Here I am using the shrinking diameter to give me an even rise and fall. Our diameter shrinks by twenty before the game ends, and its total is sixty.
                //So, taking 50 away from it gives us 10 to 1 while it is rising, and -1 to -10 until it stops. The multiplier raises the arc. try adjusting it to see its effects!
                //Using the time measurement like this can help add more natural movement in your games.
                yCurvingEffect += (diameter-50)*3;
                if (xMove>0) {xCurvingEffect += (diameter-50)*3;}
                if (xMove<0) {xCurvingEffect -= (diameter-50)*3;}
                
                if(diameter <= 40){ shootingFor = "";}
                else{
                    diameter -= 2;
                    keeper.jump();
                }
            }
        }

        void resetPosition() {
            x = centreX - diameter/3; //initial position of x
            y = (int)(centreY*1.75f); //initial position of y
            xCurvingEffect = 0;
            yCurvingEffect = 0;
        }
        
        void draw(Graphics g) {  // Draws the ball at its current location.
            g.setColor(new Color(220, 220, 65));
            g.fillOval(x +xCurvingEffect, y-yCurvingEffect, diameter, diameter);//this is our ball
            //lets add a light spot to it, to make it look shiny. Remember about the order of drawing, what ever is called first will be behind everything else
            g.setColor(new Color(250, 250, 180));
            g.fillOval(x +xCurvingEffect+diameter/10, y-yCurvingEffect+diameter/10, diameter/2, diameter/2);//this is our ball
        }
    }
//this is the keeper class
    class Keeper{
        //the variables for our keeper
        int width = (int)(centreX*0.125);//initial size of the ball
        int x = centreX - width/2; //initial position of x
        int y = centreY; //initial position of y

        int footXL = (int)(centreX*1.15) - width/2; //initial position of x
        int footXR = (int)(centreX*0.975) - width/2; //initial position of x
        int footY = (int)(centreY*1.5); //initial position of y

        int handXL = (int)(centreX*1.25) - width/2; //initial position of x
        int handXR = (int)(centreX*0.90) - width/2; //initial position of x
        int handY = (int)(centreY*1.25); //initial position of y

        //where the keeper will jump to
        int whereToSave = 0;
        //while it looks like a lot, these are just the movements done to the hand, foot and body positions to move the keeper towards their final destination
        //I worked this out through testing and adjusting the numbers below until I found ones that worked.
        //A sprite or image would be used and manipulated here rather than lines and reactangles, so the number of variables here would be much less.
        //But for our purposes, of having no imports, this is one way of animating our keeper.
        //mess around with these numbers for strange results
        void jump() {
            switch (whereToSave) {
                case 0://jumping to the left
                    handXL += (int)((x - getWidth())*0.045f); //this moves the left hand side to side
                    handXR += (int)((x - getWidth())*0.02f); //this moves the right hand side to side
                    handY -= (int)((y - getHeight())*0.012f);//this moves both hands up or down

                    footXL += (int)((x - getWidth())*0.015f); //this moves the left foot side to side
                    footXR += (int)((x - getWidth())*0.01f); //this moves the right foot side to side
                    footY += (int)((y - getHeight())*0.006f); //this moves both feet up or down

                    x += (int)((y - getHeight())*0.05f); //this moves the whole body's x axis
                    y += (int)((y - getHeight())*0.004f); //this moves the whole body's y axis
                    break;
                case 1: //jumping to the centre
                    handXL += (int)((x - getWidth())*0.015f);
                    handXR -= (int)((x - getWidth())*0.015f);
                    handY -= (int)((y - getHeight())*0.025f);

                    y -= (int)((y - getHeight())*0.006f);
                    break;
                case 2://jumping to the right
                    handXL -= (int)((x - getWidth())*0.022f);
                    handXR -= (int)((x - getWidth())*0.045f);
                    handY -= (int)((y - getHeight())*0.012f);

                    footXL -= (int)((x - getWidth())*0.01f);
                    footXR -= (int)((x - getWidth())*0.015f);
                    footY += (int)((y - getHeight())*0.006f);

                    x -= (int)((y - getHeight())*0.045f);
                    y += (int)((y - getHeight())*0.004f);
                    break;
                default:
                    break;
            }
        }

        void resetPosition() {
            width = (int)(centreX*0.125);//initial size of the ball
            x = centreX - width/2; //initial position of x
            y = centreY; //initial position of y

            footXL = (int)(centreX*1.15) - width/2; //initial position of x
            footXR = (int)(centreX*0.975) - width/2; //initial position of x
            footY = (int)(centreY*1.5); //initial position of y

            handXL = (int)(centreX*1.25) - width/2; //initial position of x
            handXR = (int)(centreX*0.90) - width/2; //initial position of x
            handY = (int)(centreY*1.25); //initial position of y
        }

        void draw(Graphics2D g) {  // Draws the keeper at its current location.
            //lets get our intial settings set up. We will give out keeper black legs, red top and a pink face.
            g.setColor(new Color(0, 0, 0));
            Stroke strokeLimbs = new BasicStroke(20, BasicStroke.CAP_ROUND, BasicStroke.CAP_ROUND); // Setting a line width for the arms.
            Stroke strokeBrow = new BasicStroke(10, BasicStroke.CAP_ROUND, BasicStroke.CAP_ROUND); // Setting the line width for the keeper's angry eyebrows...

            g.setStroke(strokeLimbs);
            g.drawLine(x+(int)(width*0.85f), (int)(centreY*1.05f), footXL, footY); // draw a line for the left leg
            g.drawLine(x+(int)(width*0.15f), (int)(centreY*1.05f), footXR, footY); // draw a line for the right leg

            //drawing using the 'width' integer, we can keep our keeper in proportion to the goal and the screen.
            g.fillRoundRect(x, y+(int)(width*0.25f), width, (int)(width*0.75f), 30, 30); //draw a rectangle that will act as the keeper's lower torso

            g.setColor(new Color(180, 55, 45)); //set the colour to red
            g.fillRoundRect(x, y-(int)(width*0.25f), width, (int)(width*0.9f), 30, 30);//draw a rectangle that will act as the keeper's upper torso

            g.setColor(new Color(215, 160, 185));//set the colour to pink
            g.fillRoundRect(x+(int)(width*0.1f), y-(int)(width*0.75f), (int)(width*0.8f), (int)(width*0.75f), 30, 30);

            g.setColor(new Color(220, 130, 125));//set the colour to a darker pink for the keeper's nose
            g.fillRoundRect(x+(int)(width*0.4f), y-(int)(width*0.5f), (int)(width*0.2f), (int)(width*0.4f), 50, 10);
            //draw angry eyebrows on the keeper's face
            g.setColor(new Color(0, 0, 0));
            g.setStroke(strokeBrow);
            g.drawLine(x+(int)(width*0.25f), y-(int)(width*0.6f), x+(int)(width*0.5f), y-(int)(width*0.5f));
            g.drawLine(x+(int)(width*0.5f), y-(int)(width*0.5f), x+(int)(width*0.75f), y-(int)(width*0.6f));

            //draw the keeper's arms. We want these to appear in front of the keeper's face, so we draw them last
            g.setStroke(strokeLimbs);
            g.setColor(new Color(180, 55, 45));
            g.drawLine(x+(int)(width*0.9f), (int)centreY+3, handXL, handY); // draw a line for the left arm
            g.drawLine(x+(int)(width*0.1f), (int)centreY+3, handXR, handY); // draw a line for the right arm
        }
    }
}