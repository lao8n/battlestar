package swarm_wars_library.fsm_ui;
import processing.core.PApplet;
import swarm_wars_library.physics.Vector2D;


public class FSMOption1 {

    private PApplet sketch;

    //Buttons
    private FSMBackground FSM1;
    private FSMBackground information;
    private FSMBackground content;
    private boolean showContent = false;


    //SIZING
    private int boxWidth;
    private int boxHeight;
    private int boxX;
    private int boxY;

    //STAR SET UP
    private Star star1;
    private Star star2;
    private Star star3;

    //LABELS FROM STARS
    private Label label1;
    private Label label2;
    private Label label3;

    //ARROW SET UP
    private Arrow arrow1;
    private Arrow arrow2;
    private Arrow arrow3;

    //FSM POSITIONS
    private Vector2D location1;
    private Vector2D location2;
    private Vector2D location3;

    //SWAP VARIABLES
    private String buttonToSwap;
    private boolean buttons[] = {false, false, false};
    private boolean mousePressed = false;

    //=========================================================================//
    // Constructor                                                             //
    //=========================================================================//
    public FSMOption1(PApplet sketch, int boxWidth, int boxHeight, int boxX, 
        int boxY){

        this.sketch = sketch;
        this.boxWidth = boxWidth;
        this.boxHeight = boxHeight;
        this.boxX = boxX;
        this.boxY = boxY;
        this.setupLocations();
        this.setupButtons();
        this.setupArrows();
        this.setupStars();
        this.setupLabels();
    }

    //=========================================================================//
    // Update method                                                           //
    //=========================================================================//
    public void update(){
        this.updateButtons();
        this.updateArrows();
        this.updateStars();
        this.updateLabels();
        this.updateMousePressButton();
    }

    //=========================================================================//
    // Button methods                                                          //
    //=========================================================================//
    private void setupButtons() {

        this.FSM1 = new FSMBackground(this.sketch,
                "",
                new Vector2D(boxX, boxY),
                new Vector2D(boxWidth, boxHeight),
                0, 0, 0);

        this.information = new FSMBackground(this.sketch,
                "Asyclical Finite State Machine",
                new Vector2D(boxX, boxY + boxHeight + 20),
                new Vector2D(boxWidth, 50),
                105, 105, 105);

        this.content = new FSMBackground(this.sketch,
                "One of the most common finite state machine you would have encountered, is\n" +
                        "a traffic light. There are three states: red, orange and green. The lights change\n" +
                        "under certain conditions. This finite state machine is cylical as it goes in\n" +
                        "a circle, always in the same direction.",
                new Vector2D(boxX, boxY + boxHeight + 20),
                new Vector2D(200, 200),
                105, 105, 105);
    }

    private void updateButtons() {
        this.FSM1.update();
        this.information.update();
        if (this.showContent == true) {
            this.content.update();
        }

    }

    //=========================================================================//
    // Arrow methods                                                           //
    //=========================================================================//
    private void setupArrows(){
        this.arrow1 = new Arrow(this.sketch,
                this.location1,
                Vector2D.add(this.location2,
                        new Vector2D(0, -50)));

        this.arrow2 = new Arrow(this.sketch,
                this.location2,
                Vector2D.add(this.location3,
                        new Vector2D(-50, 0)));
        this.arrow3 = new Arrow(this.sketch,
                this.location3,
                Vector2D.add(this.location1,
                        new Vector2D(15, 50)));

    }

    private void updateArrows() {
        this.arrow1.update();
        this.arrow2.update();
        this.arrow3.update();
    }

    //=========================================================================//
    // Star methods                                                            //
    //=========================================================================//
    private void setupStars(){
        Vector2D dimensions = new Vector2D(25, 55);
        int nPoints = 5;
        this.star1 = new Star(this.sketch, this.location1, dimensions, nPoints, 119, 192, 246);
        this.star2 = new Star(this.sketch, this.location2, dimensions, nPoints, 181, 170, 235);
        this.star3 = new Star(this.sketch, this.location3, dimensions, nPoints, 221, 218, 232);
    }


    private void updateStars(){
        this.star1.update();
        this.star2.update();
        this.star3.update();
    }

    //=========================================================================//
    // Locations methods                                                       //
    //=========================================================================//
    private void setupLocations(){
        this.location1 = new Vector2D(boxWidth/2+boxX,
                boxHeight/4+boxY);
        this.location2 = new Vector2D(boxWidth/4+boxX,
                boxHeight/4*3+boxY);
        this.location3 = new Vector2D(boxWidth/4*3+boxX,
                boxHeight/4*3+boxY);
    }

    //=========================================================================//
    // Labels methods                                                          //
    //=========================================================================//
    private void setupLabels(){
        int labelColour = 0;

        //LABELS TO PUT ON STARS
        this.label1 = new Label(this.sketch, labelColour, "Special",
                this.location1);
        this.label2 = new Label(this.sketch, labelColour, "Defend",
                this.location2);
        this.label3 = new Label(this.sketch, labelColour, "Scout",
                this.location3);

    }

    private void updateLabels(){
        this.label1.update();
        this.label2.update();
        this.label3.update();
    }


    //=========================================================================//
    // Mouse methods                                                           //
    //=========================================================================//

    public void updateMousePressButton(){
        // Swap button1
        if(this.checkMousePressStar(this.location1, 45) == true) {
            this.swapButton1();
        }
        // Swap button2
        else if(this.checkMousePressStar(this.location2, 45) == true) {
            this.swapButton2();
        }
        // Swap button3
        else if(this.checkMousePressStar(this.location3, 45) == true) {
            this.swapButton3();
        }
        else if (checkMousePressButton(new Vector2D(boxX, boxY + boxHeight + 20),
                new Vector2D(200, 200)) == true && showContent == false) {
            this.showContent = true;
        }
        else if (checkMousePressButton(new Vector2D(boxX, boxY + boxHeight + 20),
                new Vector2D(200, 200)) == true && showContent == true) {
            this.showContent = false;
        }

    }


    private boolean checkMousePressStar(Vector2D location, int radius) {
        if(this.mousePressed){
            if(this.sketch.mouseX >= location.getX() - radius &&
                    this.sketch.mouseX <= location.getX() + radius &&
                    this.sketch.mouseY >= location.getY() - radius &&
                    this.sketch.mouseY <= location.getY() + radius){
                return true;
            }
        }
        return false;
    }

    private boolean checkMousePressButton(Vector2D location,
                                          Vector2D dimensions){
        if(this.mousePressed){
            if(this.sketch.mouseX >= location.getX() &&
                    this.sketch.mouseX <= location.getX() + dimensions.getX() &&
                    this.sketch.mouseY >= location.getY() &&
                    this.sketch.mouseY <= location.getY() + dimensions.getY()){
                return true;
            }
        }
        return false;
    }


    public void listenMousePressed(){
        this.mousePressed = true;
    }

    public void listenMouseReleased(){
        this.mousePressed = false;
    }


    //=========================================================================//
    // Swap methods                                                            //
    //=========================================================================//
    public void swapButton1() {
        if (this.buttonToSwap == null) {
            this.buttonToSwap = this.label1.getLabelString();
            this.star1.changeColour();
            buttons[0] = true;
        }
        else {
            for (int i=0; i <=2; i++) {
                if (buttons[1] == true) {
                    this.label2.changeLabel(this.label1.getLabelString());
                    this.star2.changeColourSwap(this.label1.getLabelString());
                    this.label1.changeLabel(this.buttonToSwap);
                    this.star1.changeColourSwap(this.buttonToSwap);
                    buttons[1] = false;
                    this.buttonToSwap = null;
                }
                else if (buttons[2] == true) {
                    this.label3.changeLabel(this.label1.getLabelString());
                    this.star3.changeColourSwap(this.label1.getLabelString());
                    this.label1.changeLabel(this.buttonToSwap);
                    this.star1.changeColourSwap(this.buttonToSwap);
                    buttons[2] = false;
                    this.buttonToSwap = null;
                }
            }
        }
    }

    public void swapButton2() {
        if (this.buttonToSwap == null) {
            this.buttonToSwap = this.label2.getLabelString();
            this.star2.changeColour();
            buttons[1] = true;
            System.out.println(this.buttonToSwap);
        }
        else {
            for (int i=0; i <=2; i++) {
                if (buttons[0] == true) {
                    this.label1.changeLabel(this.label2.getLabelString());
                    this.star1.changeColourSwap(this.label2.getLabelString());
                    this.label2.changeLabel(this.buttonToSwap);
                    this.star2.changeColourSwap(this.buttonToSwap);
                    buttons[0] = false;
                    this.buttonToSwap = null;
                }
                else if (buttons[2] == true) {
                    this.label3.changeLabel(this.label2.getLabelString());
                    this.star3.changeColourSwap(this.label2.getLabelString());
                    this.label2.changeLabel(this.buttonToSwap);
                    this.star2.changeColourSwap(this.buttonToSwap);
                    buttons[2] = false;
                    this.buttonToSwap = null;
                }
            }
        }
    }

    public void swapButton3() {
        if (this.buttonToSwap == null) {
            this.buttonToSwap = this.label3.getLabelString();
            this.star3.changeColour();
            buttons[2] = true;
        }
        else {
            for (int i=0; i <=2; i++) {
                if (buttons[0] == true) {
                    this.label1.changeLabel(this.label3.getLabelString());
                    this.star1.changeColourSwap(this.label3.getLabelString());
                    this.label3.changeLabel(this.buttonToSwap);
                    this.star3.changeColourSwap(this.buttonToSwap);
                    buttons[0] = false;
                    this.buttonToSwap = null;
                }
                else if (buttons[1] == true) {
                    this.label2.changeLabel(this.label3.getLabelString());
                    this.star2.changeColourSwap(this.label3.getLabelString());
                    this.label3.changeLabel(this.buttonToSwap);
                    this.star3.changeColourSwap(this.buttonToSwap);
                    buttons[1] = false;
                    this.buttonToSwap = null;
                }
            }
        }
    }
}
