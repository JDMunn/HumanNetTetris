
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
//IMPORT FANN LIBRARY WHEN COMPILING IN UNIX TYPE "javac -cp '.:org.example.jar' .java" or LINUX " OR LINUX "javac -cp .;jwitter.jar MyClass.java" 

public class ANNBot extends TetrisBot {
    
    FileWriter pw;
    int numberRotations;
    int colPosition;
    TetrisPiece currPiece;
    TetrisBoard currBoard;
    boolean waitForPlay = true;
    int rotations;
    Fann fann;
    
    float[] inputs = new float[]{ -1, 1 };
    float[] outputs = fann.run( inputs );
    
    public ANNBot() {
        numberRotations = 0;
        colPosition = 0;
        rotations = 0;
        fann = new Fann( "TetrisNet.net" );
    }
    
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
    public TetrisMove getOutputMove(double[] output, TetrisPiece currPiece){
        int numRotations = -1;
        int leftMostCoordinate = -1;
        double currCoordMax = -100;
        int maxIndex = 0;

        for( int i = 0; i< output.length - 4; i++){
            if (output[i]>currCoordMax){
                currCoordMax = output[i];
                maxIndex = i;
            }
        }
        leftMostCoordinate = maxIndex;

        double currRotateMax = -100;
        for( int i = output.length-4; i< output.length; i++){
            if (output[i]>currRotateMax){
                currRotateMax = output[i];
                numRotations = i-output.length;
            }
        }


        return new TetrisMove(currPiece.rotate(numRotations) , leftMostCoordinate);
    }
    
    public double[] createANNInput(double[] contour, int[] currPieceArray, int[] nextPieceArray){
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
    }
    // return the left-most column of where you want to play
    public TetrisMove chooseMove(TetrisBoard board, TetrisPiece current_piece, TetrisPiece next_piece) {
        
        currPiece = current_piece;
        currBoard = board;

        //set up input vector data by putting all inputs together into the same array
        double[] contour = board.getContour();
        int[] currPieceArray = getPieceList(current_piece);
        int[] nextPieceArray = getPieceList(next_piece); 
        double[] finalInput = createANNInput(contour, currPieceArray , nextPieceArray);



        currBoard.viewIncomingPiece(current_piece, colPosition);

        while (waitForPlay) {   
            try {               
                Thread.sleep(50);
            } catch (InterruptedException e) {
                System.out.println("Execution interrupted!");
            }
        }

	   // I , MICHAEL KAUZMANN, THINK THAT THE DOUBLE[] FINALiNPUT GOES INTO THE FANN.run();
        double[] output = fann.run(finalInput); //Generates the output data  
        
	// The output array then needs to be converted into a TetrisMove that is finally returned and the move is made.
        // Michael's code goes here
        return getOutputMove(output, current_piece);

    }
}
