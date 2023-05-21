import java.awt.event.*;
import java.io.*;
import java.util.Random;
import java.util.Scanner;

import javax.sound.sampled.*;

import java.awt.*;
import java.util.ArrayList;
import javax.swing.Timer;

import acm.graphics.GLabel;
import acm.graphics.GOval;
import acm.graphics.GRect;
import acm.program.GraphicsProgram;

public class MainClass extends GraphicsProgram implements ActionListener
{

    public GOval food;

    private ArrayList<GRect> snakeBody;

    double timerSpeed = 250;
    private GRect bgDim;

    public Timer timer = new Timer((int)timerSpeed, this);

    private int score;
    public int dx, dy; //used in grow snake
    boolean instrucOn; boolean showPause;

    private Scoreboard scoreLabel;private Scoreboard scoreLabel2;
    private GLabel instructions; private GLabel instructions2; private GLabel instructions3; private GLabel instructions4;private GLabel instructions5;private GLabel instructions6; private GLabel instructions7;

    public void run()
    {
        addKeyListeners();

        //background: snek movement zone
        GRect canvas = new GRect(50, 50, 650, 400);
        canvas.setFillColor(Color.darkGray);
        canvas.setFilled(true);

        //background: Snek death zone
        GRect theVoid = new GRect(1000, 1000);
        theVoid.setFillColor(Color.black);
        theVoid.setFilled(true);

        add(theVoid);
        add(canvas);

        //boll
        food = new Ball(0,0,20, 20) ;
        food.setFillColor(Color.yellow);
        food.setFilled(true);
        randomFood();
// snake
        snakeBody = new ArrayList<>();
        drawSnake();
        score = 0;
        addInstructions();
        setUpScore(); instrucOn = true;

        music();
    }

    public void randomFood(){

        Random rand = new Random();

        int randX = rand.nextInt(600); int randY = rand.nextInt(350);
        food.setLocation(randX + 70, randY + 70);
        add(food);
    }

    public void setUpScore() {
        //get high score
        File file = new File("snekHighScore.txt");
        Scanner scanner;
        try {
            scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        int hs = scanner.nextInt();
        scanner.close();

        scoreLabel = new Scoreboard("Score: " + score, 100, 30);
        scoreLabel2 = new Scoreboard("High Score: " + hs, 400, 30);

        scoreLabel.setColor(Color.white);
        scoreLabel2.setColor(Color.white);
        scoreLabel.setFont(new Font("Serif", Font.BOLD, 16)); //change displayed word font
        scoreLabel2.setFont(new Font("Serif", Font.BOLD, 16));
    }
    public void addInstructions(){
        //instructions mass
        instructions= new GLabel("Welcome to Snek Lite!", 250, 100);
        instructions2= new GLabel("You control the snek. The Snek cannot stop moving, but can turn around.", 100, 150);
        instructions3= new GLabel("Press WASD to rotate the snake", 100, 190);
        instructions4= new GLabel("Try eating as much yellow balls as you can. ", 100, 230);
        instructions5= new GLabel("Its game over once the snek runs into itself or the edges", 100, 270);
        instructions6= new GLabel("Good luck!", 100, 310);
        instructions7= new GLabel("Press Space to START", 250, 400);
        //font
        instructions.setFont(new Font("Serif", Font.BOLD, 24));
        instructions2.setFont(new Font("Serif", Font.BOLD, 17));
        instructions3.setFont(new Font("Serif", Font.BOLD, 17));
        instructions4.setFont(new Font("Serif", Font.BOLD, 17));
        instructions5.setFont(new Font("Serif", Font.BOLD, 17));
        instructions6.setFont(new Font("Serif", Font.BOLD, 17));
        instructions7.setFont(new Font("Serif", Font.BOLD, 24));
        //color
        instructions.setColor(Color.white);
        instructions2.setColor(Color.white);
        instructions3.setColor(Color.white);
        instructions4.setColor(Color.white);
        instructions5.setColor(Color.white);
        instructions6.setColor(Color.white);
        instructions7.setColor(Color.white);
        //add
        backgroundDim();
        add(instructions);
        add(instructions2);
        add(instructions3);
        add(instructions4);
        add(instructions5);
        add(instructions6);
        add(instructions7);
    }
    public void updateScoreLabel(){
        //scoreLabel updates to show the current score count.
        remove(scoreLabel);
        setUpScore();
        add(scoreLabel);
    }

    public void timerSpeedUp(){
        timer.stop();
        timerSpeed = timerSpeed / 1.003;
        timer.setInitialDelay((int) timerSpeed);
        timer.start();
        //timer is restarted so the new timer is applied.
    }
    public void pauseScreen(){
        if(showPause){
            System.out.println("removing pause");
            removeInstructions();
            timer.start();
            showPause=false;
            try {
                timer.wait(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }else {
            System.out.println("adding pause");
            instructions = new GLabel("PAUSED", 300, 200);
            instructions7 = new GLabel("Press Space to Unpause", 250, 400);
            instructions.setFont(new Font("Serif", Font.BOLD, 40));
            instructions7.setFont(new Font("Serif", Font.BOLD, 24));
            instructions.setColor(Color.white);
            instructions7.setColor(Color.white);
            add(instructions);
            add(instructions7);
            showPause = true;
            timer.stop();
        }
    }
    public void backgroundDim(){
        bgDim = new GRect(0, 0, 1000, 1000);
        Color color = new Color(0, 0, 0, 128); // black at 50% transparency
        bgDim.setFillColor(color);
        bgDim.setFilled(true);
        add(bgDim);
    }

    public void removeInstructions(){
        remove(instructions); remove(instructions2);
        remove(instructions3); remove(instructions4);
        remove(instructions5); remove(instructions6); remove(instructions7);
        remove(bgDim);
        add(scoreLabel);
        add(scoreLabel2);
        timer.start();
    }


    public void drawSnake()
    {
        Random rand = new Random();
        int randX = rand.nextInt(500); int randY = rand.nextInt(350);
        for(int i = 0; i <60; i= i + 20){
            SnakePart part = new SnakePart(randX + 120 - i, randY + 80, 20, 20);
            part.setFillColor(Color.lightGray); part.setFilled(true);
            add(part);
            snakeBody.add(part);}
    }


    boolean goingUp = false;
    boolean goingLeft = false;
    boolean goingRight = true;
    boolean goingDown = false;
    public void keyPressed(KeyEvent keyPressed)
    {
        switch (keyPressed.getKeyCode())
        {
            case KeyEvent.VK_W:
                goingLeft = false; goingRight = false; goingDown = false;
                goingUp = true;
                break;

            case KeyEvent.VK_A:
                goingUp = false; goingRight = false; goingDown = false;
                goingLeft = true;
                break;

            case KeyEvent.VK_S:
                goingUp = false; goingRight = false; goingLeft = false;
                goingDown = true;
                break;

            case KeyEvent.VK_D:
                goingUp = false; goingDown = false; goingLeft = false;
                goingRight = true;
                break;

            case KeyEvent.VK_SPACE:
                if(instrucOn){
                    instrucOn = false;
                    removeInstructions();
                    timer.start();
                    break;
               }//else{
                   // pauseScreen();
                    //break;
                //}
        }
                }


    public void touchFood(){
        if(food.getX()+20 > snakeBody.get(0).getX() &&
                food.getX()-20 < snakeBody.get(0).getX() &&
                food.getY()+20 > snakeBody.get(0).getY() &&
                food.getY()-20 < snakeBody.get(0).getY())
        {
            score++;
            updateScoreLabel();
            growSnake();

            remove(food);
            randomFood();
            }
    }
    public void touchSelf(){
        for(int i = 1; i < snakeBody.size(); i++){
            if(snakeBody.get(i).getX()+10 > snakeBody.get(0).getX() &&
                    snakeBody.get(i).getX()-10 < snakeBody.get(0).getX() &&
                    snakeBody.get(i).getY()+10 > snakeBody.get(0).getY() &&
                    snakeBody.get(i).getY()-10 < snakeBody.get(0).getY())
            {
                gameOver();
            }
        }
    }
    public void touchVoid(){
        if(snakeBody.get(0).getX() < 40 | snakeBody.get(0).getX() > 680 |
                snakeBody.get(0).getY() < 40 | snakeBody.get(0).getY() > 430){
            gameOver();
    }
    }

    private void redrawSnake()
    {
        for (int i = snakeBody.size() - 1; i > 0; i--){
            GRect previousSnek = snakeBody.get(i-1);
            // set new snake location to previous body's position
            snakeBody.get(i).setLocation(previousSnek.getX(), previousSnek.getY());
        }
    }

    private void growSnake()
    {
        // get the last body part of the snake
        SnakePart lastBodyPart = (SnakePart) snakeBody.get(snakeBody.size() - 1);

        // create new body part
        SnakePart newBodyPart = new SnakePart(lastBodyPart.getX() - dx, lastBodyPart.getY() - dy,20,20);
        newBodyPart.setFillColor(Color.lightGray); newBodyPart.setFilled(true);
        add(newBodyPart);

        // add the new body part to the end of snake
        snakeBody.add(newBodyPart);
    }

    public int highScoreCalculation() throws IOException {
        String filename = "snekHighScore.txt";
        File file = new File(filename);
        int previousHighScore =0;
        //is there already a high score?
        // is file empty?
        if (file.length() == 0) {
            //if no, create file
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write("0");
            writer.close();
        }else {
            //read file's highscore into int
            Scanner scanner = new Scanner(file);
            previousHighScore = scanner.nextInt();
            scanner.close();
        }

        //is current score higher than high score?
        if (previousHighScore < score) {
            //if yes, write current score to high score file
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(""+ score);
            writer.close();
        }
            //return score from high score file
        Scanner scanner = new Scanner(file);
        int highScore = scanner.nextInt();
        scanner.close();
        return (highScore);
    }

    private void moveUp()
    {
        snakeBody.get(0).move(0, -20);
        dx = 0; dy = -20;
        //the dx and dy will be used when growing snake,
        // to ensure that the new part will be positioned behind the last snake part
    }

    private void moveDown()
    {
        snakeBody.get(0).move(0, 20);
        dx = 0; dy = 20;
    }

    private void moveLeft()
    {
        snakeBody.get(0).move(-20, 0);
        dx = -20; dy = 0;
    }

    private void moveRight()
    {
        snakeBody.get(0).move(20, 0);
        dx = 20; dy = 0;
    }


    @Override
    public void actionPerformed(ActionEvent arg0) {
        redrawSnake();

        if(goingUp){
            moveUp();
        }
        else if(goingLeft){
            moveLeft();
        }
        else if(goingDown){
            moveDown();
        }
        else if(goingRight){
            moveRight();
        }
        touchFood();
        timerSpeedUp();
        touchVoid();
        touchSelf();
        }

    public void gameOver(){
        timer.stop();
        int highScore;
        try {
            highScore = highScoreCalculation();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //displays
        instructions= new GLabel("Game Over", 300, 100);
        instructions2= new GLabel("Score:  " + score, 150, 400);
        instructions3= new GLabel("High Score:  " + highScore, 450, 400);
        //font
        instructions.setFont(new Font("Serif", Font.BOLD, 30));
        instructions2.setFont(new Font("Serif", Font.BOLD, 20));
        instructions3.setFont(new Font("Serif", Font.BOLD, 20));
        //color
        instructions2.setColor(Color.white);
        instructions.setColor(Color.white);
        instructions3.setColor(Color.white);
        //add
        backgroundDim();
        add(instructions);
        add(instructions3);
        add(instructions2);

    }

    //my music taste is indeed very
    public void music() {
        Random rand = new Random();
        int randNum = rand.nextInt(4);

        //randoming
        String fileName = "XDee.mp3";
        //enable for more music diversity!1!!111
        /*
        if(randNum == 1){
            fileName = "holy snakes.mp3";
        } if (randNum == 2) {
            fileName = "vibing snakes.mp3";
        } if(randNum == 3){
            fileName = "retro snakes.mp3";
        }
        */

        try {
            File soundFile = new File(fileName);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);

            // specify the audio format
            AudioFormat format = new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    44100,
                    16,
                    2,
                    4,
                    44100,
                    false);

            // get a modified audio input stream with the specified format
            AudioInputStream modifiedIn = AudioSystem.getAudioInputStream(format, audioIn);

            Clip clip = AudioSystem.getClip();
            clip.open(modifiedIn);
            clip.start(); // start playing the audio

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.out.println("No more rick: " + e.getMessage());
        }
    }

    public static void main(String[] args)
    {
        new MainClass().start();
    }
}
