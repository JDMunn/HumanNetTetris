import java.io.*
import java.util.*

public class DataToTetris {
  // class to parse our output file for current pieces and subsequent rotations

  String DATAFILE = "outputGA_FIN.txt";
  ArrayList<int[]> ALLMOVES = new ArrayList<int>();

  public static void main(String[] args) throws FileNotFoundException, IOException{

    Scanner scan = new Scanner(new File(DATAFILE));
    scan.useDelimiter(",");

    // loop through all moves in datafile,
    // parse and add the current pieces and rotations from each line
    while (scan.hasNextLine()) {
      String line = (scan.nextLine());

      // remove unnecessary characters
      line = line.replace("[", "");
      line = line.replace("]", "");
      line = line.replace("*", "");
      String[] arr = line.split(",");

      ALLMOVES.add(extractPieceAndRotation(arr));
    }

    scan.close();
  }

  // parses array of string values to identify corresponding currentPiece
  // and piece's rotation
  public int[] extractPieceAndRotation(String[] arr) {
    int[] currPieceAndRotation = new int[8];
    int numRotations = 0;

    // current piece is identified by indices 10-16 from line in output file of moves
    for (int i=10; i<17; i++){
      try {
        currPieceAndRotation[i-10] = Integer.parseInt(arr[i]);
      } catch (NumberFormatException nfe) {
        continue;
      }
    }

    // number of rotations is identified by indices 34-37 from line in output file;
    // a value of 1 at index 34 indicates 0 rotations
    for (int i=35; i<arr.size(); i++) {
      try {
        if(Integer.parseInt[i] == 1)
          numRotations += 1;
        currPieceAndRotation[7] = numRotations;
      } catch (NumberFormatException nfe) {
        continue;
      }
    }

    return currPieceAndRotation;
  }

  // splits piece data from its rotation number
  // (to match input for getOutputPiece)
  public createPieces(int[] currPieceAndRotation) {
    int[] currPiece = Arrays.copyOfRange(currPieceAndRotation, 0, 7);
    int numRotations = currPieceAndRotation[7];
    getOutputPiece(currPiece, numRotations);
  }

  // ** UNCHANGED FROM MICHAEL **
  // creates and rotates tetris piece according to parsed data
  public TetrisPiece getOutputPiece(int[] currPiece, int numRotations){
    if(output.length != 7){
      System.out.println("what the fuck man. . . , currPiece needs to be of length 7!");
      return null;
    }
    else if(currPiece[0] ==1){
      return TetrisPiece.buildSquarePiece().rotatePiece(numRotations);
    }
    else if(currPiece[1] ==1){
      return TetrisPiece.buildLine().rotatePiece(numRotations);
    }
    else if(currPiece[2] ==1){
      return TetrisPiece.buildSPiece().rotatePiece(numRotations);
    }
    else if(currPiece[3] ==1){
      return TetrisPiece.buildZPiece().rotatePiece(numRotations);
    }
    else if(currPiece[4] ==1){
      return TetrisPiece.buildTPiece().rotatePiece(numRotations);
    }
    else if(currPiece[5] ==1){
      return TetrisPiece.buildLeftLPiece().rotatePiece(numRotations);
    }
    else if(currPiece[6] ==1){
      return TetrisPiece.buildRightLPiece().rotatePiece(numRotations);
    }
  }

}
