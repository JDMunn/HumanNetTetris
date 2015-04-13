
import java.util.Arrays;
import java.awt.*;
import javax.swing.*;
import java.util.Random;
import java.awt.event.*;
import java.lang.reflect.*;
import java.io.FileWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class HumanPlayer extends TetrisBot implements KeyListener  {
    
    FileWriter pw;
    int numberRotations;
    int colPosition;
    TetrisPiece currPiece;
    TetrisBoard currBoard;
    boolean waitForPlay = true;
    File outputFile = new File("output.txt");
    int rotations;

    
    
    public HumanPlayer() {
        numberRotations = 0;
        colPosition = 0;
        rotations = 0;
        try {
            pw = new FileWriter(outputFile, true);
        }
        catch (FileNotFoundException e){}
        catch (IOException e){}

    }
    
    public void keyPressed(KeyEvent e){
        char key = e.getKeyChar();
        if (e.getKeyCode() == 38) {
            currPiece = currPiece.rotatePiece(1);
            // System.out.println(currPiece); 
            rotations++;

            if (currBoard.width - currPiece.width < colPosition) {
                colPosition--;
                //                System.out.println(colPosition);
            }
        }
        else if (e.getKeyCode() == 40) {
            //System.out.println("Pressed down: LAUNCH");
        }
        else if (e.getKeyCode() == 37) {
            if (colPosition > 0) {
                colPosition--;
                //                System.out.println(colPosition);
            }
        }
        else if (e.getKeyCode() == 39) {
            if (colPosition + currPiece.width < currBoard.width) {
                colPosition++;
                //                System.out.println(colPosition);
            }
        }
        currBoard.viewIncomingPiece(currPiece, colPosition);
    }
    
    public void keyReleased(KeyEvent e){
        if (e.getKeyCode() == 40) {
            waitForPlay = false;
        }
    }
    public void keyTyped(KeyEvent e){}

    public int[] getPieceList(TetrisPiece piece){
        int[] output = new int[] {0,0,0,0,0,0,0};
        if(TetrisPiece.buildSquarePiece().toString().equals(piece.toString())){
            output[0] = 1;
        }
        else if(TetrisPiece.buildLinePiece().toString().equals(piece.toString())){
            output[1] = 1;
        }
        else if(TetrisPiece.buildSPiece().toString().equals(piece.toString())){
            output[2] = 1;
        }
        else if(TetrisPiece.buildZPiece().toString().equals(piece.toString())){
            output[3] = 1;
        }

        else if(TetrisPiece.buildTPiece().toString().equals(piece.toString())){
            output[4] = 1;
        }
        else if(TetrisPiece.buildLeftLPiece().toString().equals(piece.toString())){
            output[5] = 1;
        }
        else if(TetrisPiece.buildRightLPiece().toString().equals(piece.toString())){
            output[6] = 1;
        }
        else{
            System.out.println("something is fucked dude");
        }
        return output;
    }
    
    
    // return the left-most column of where you want to play
    public TetrisMove chooseMove(TetrisBoard board, TetrisPiece current_piece, TetrisPiece next_piece) {
        
        currPiece = current_piece;
        currBoard = board;

        //set up input vector data
        double[] contour = board.getContour();
        int[] currPieceArray = getPieceList(current_piece);
        int[] nextPieceArray = getPieceList(next_piece); 
        double[] finalInput = new double[contour.length + currPieceArray.length + nextPieceArray.length];
        int index = 0;
        for (int i = 0; i < contour.length; i ++){
            finalInput[index] = contour[i];
            index++;
        }
        for (int i = 0; i < currPieceArray.length; i ++){
            finalInput[index] = currPieceArray[i];
            index++;
        }
        for (int i = 0; i < nextPieceArray.length; i ++){
            finalInput[index] = nextPieceArray[i];
            index++;
        }

        currBoard.viewIncomingPiece(current_piece, colPosition);

        while (waitForPlay) {   
            try {               
                Thread.sleep(50);
            } catch (InterruptedException e) {
                System.out.println("Execution interrupted!");
            }
        }

        //set up output vector data
        int[] output = new int[14];
        rotations %= 4;
        output[colPosition] = 1;
        output[10 + rotations] = 1;
        System.out.println(Arrays.toString(contour));
        //        System.out.println("n:" + Arrays.toString(nextPieceArray));
        //        System.out.println(rotations);

        waitForPlay = true;
        int oldPosition = colPosition;
        colPosition = board.width / 2;
        currBoard.viewIncomingPiece(null, 0);

        //write input and output vectors to output.txt
        try {
            pw.append(Arrays.toString(finalInput)+ "*" + Arrays.toString(output) + "\n");
            pw.flush();
        }
        catch (IOException e){}
        rotations = 0;

        //        System.out.println(current_piece);
        return new TetrisMove(currPiece, oldPosition);
    }
}
