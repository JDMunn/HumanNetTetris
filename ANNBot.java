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
import com.googlecode.fannj.*;
//IMPORT FANN LIBRARY WHEN COMPILING IN UNIX TYPE "javac -cp '.:org.example.jar' .java" or LINUX " OR LINUX "javac -cp .;jwitter.jar MyClass.java"

public class ANNBot extends TetrisBot {

    FileWriter pw;
    int numberRotations;
    int colPosition;
    TetrisPiece currPiece;
    TetrisBoard currBoard;
    int rotations;
    Fann fann;

    // float[] inputs = new float[]{ -1, 1 };
    // float[] outputs = fann.run( inputs );

    public ANNBot() {
        numberRotations = 0;
        colPosition = 0;
        rotations = 0;
        fann = new Fann("TetrisNet.net");
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
    public TetrisMove getOutputMove(float[] output, TetrisPiece currPiece){
        int numRotations = -1;
        int leftMostCoordinate = -1;

        for( int i = 0; i< output.length - 4; i++){
            if (output[i]==1){
                leftMostCoordinate = i;
            }
        }

        int index = 0;
        for( int i = output.length-4; i< output.length; i++){
            if (output[i]==1){
               numRotations = index;
            }
        }

        // create the new move based on the network
        TetrisMove newMove = new TetrisMove(currPiece.rotatePiece(numRotations) , leftMostCoordinate);

        // return it if it's valid, otherwise just do some random shit
        if(isLegal(newMove))
            return newMove;
        return TetrisPiece.buildRandomPiece()
    }

    // checks that piece is not trying to be placed beyond boundaries
    public boolean isLegal(TetrisMove m) {
        int leftCol = m.boardCol;
        TetrisPiece piece = m.piece;

        if (leftCol < 0 || leftCol + piece.width > width)
            return false;
        return true;
    }

    public float[] createANNInput(float[] contour, int[] currPieceArray, int[] nextPieceArray){
        float[] finalInput = new float[contour.length + currPieceArray.length + nextPieceArray.length];
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
        return finalInput;
    }
    // return the left-most column of where you want to play
    public TetrisMove chooseMove(TetrisBoard board, TetrisPiece current_piece, TetrisPiece next_piece) {

        currPiece = current_piece;
        currBoard = board;

        //set up input vector data by putting all inputs together into the same array
        double[] temp = board.getContour();
        float[] contour = new float[temp.length];
        for(int i=0; i<temp.length;i++){
            contour[i] = (float)temp[i];
        }
        int[] currPieceArray = getPieceList(current_piece);
        int[] nextPieceArray = getPieceList(next_piece);
        float[] finalInput = createANNInput(contour, currPieceArray , nextPieceArray);



        currBoard.viewIncomingPiece(current_piece, colPosition);

	   // I , MICHAEL KAUZMANN, THINK THAT THE float[] FINALiNPUT GOES INTO THE FANN.run();
        System.out.println(finalInput);
        float[] output = fann.run(finalInput); //Generates the output data
        System.out.println(output);

	// The output array then needs to be converted into a TetrisMove that is finally returned and the move is made.
        // Michael's code goes here
        return getOutputMove(output, current_piece);

    }
}
