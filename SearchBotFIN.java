
import java.awt.*;
import javax.swing.*;
import java.util.*;
import java.awt.event.*;
import java.lang.reflect.*;
//data stuff
import java.io.FileWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class SearchBotFIN extends TetrisBot {
    public double a = 0.192;
    public double l = .545;
    public double h = -.9698;
    public double b = -.2302;
    //data stuff
    FileWriter pw;
    File outputFile = new File("outputGA_FIN.txt");
    

    //  double w = 0.0;
    // public SearchBot(double a)
    // {
    //    this.a = a;

    public double helperMove(TetrisBoard board, TetrisPiece current_piece)
    {
        double best_score = -9999.;
        int best_rot = 0;
        int best_col = 0;
        for (int i = 0; i<4; i++) {
            TetrisPiece temp_piece = current_piece.rotatePiece(i);
            for (int col = 0; col <= board.width-temp_piece.width ; col++) {
                //create copy board
                TetrisBoard temp_board = new TetrisBoard(board.width, board.height, false);
                temp_board.board = matrixCopy(board.board);
                //place temp piece at col
                TetrisMove temp_move = new TetrisMove (temp_piece, col);
                temp_board.addPiece(temp_move);
                //check if better
                if (eval(temp_board) > best_score) {
                    best_rot = i;
                    best_col = col;
                    best_score = eval(temp_board);
                }
               
            }
        }
        return best_score;       
    }
    public TetrisMove chooseMove(TetrisBoard board, TetrisPiece current_piece, TetrisPiece next_piece) {
        //data 
        try {
            pw = new FileWriter(outputFile, true);
        }
        catch (FileNotFoundException e){}
        catch (IOException e){}

        //data collection from GA
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

    
        //System.out.println(board.toString()); 
        //System.out.println(board.board[19][0]); 
        
        double best_score = -9999.;
        int best_rot = 0;
        int best_col = 0;
        for (int i = 0; i<4; i++) {
            TetrisPiece temp_piece = current_piece.rotatePiece(i);
            for (int col = 0; col <= board.width-temp_piece.width ; col++) {
                double curr_score = 0;
                //create copy board
                TetrisBoard temp_board = new TetrisBoard(board.width, board.height, false);
                temp_board.board = matrixCopy(board.board);
                //place temp piece at col
                TetrisMove temp_move = new TetrisMove (temp_piece, col);
                temp_board.addPiece(temp_move);
                //check if better
                curr_score = eval(temp_board) + helperMove(temp_board, next_piece);
                if (curr_score > best_score) {
                    best_rot = i;
                    best_col = col;
                    best_score = curr_score;
                }
            }
        }
        //set up output vector data
        int[] output = new int[14];
        int rotations = best_rot;
        output[best_col] = 1;
        output[10 + rotations] = 1;
        //        System.out.println(Arrays.toString(contour));
        
        //write to datafile
        try {
            pw.append(Arrays.toString(finalInput)+ "*" + Arrays.toString(output) + "\n");
            pw.flush();
        }
        catch (IOException e){}             
        return new TetrisMove(current_piece.rotatePiece(best_rot), best_col);
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
    
    public double eval (TetrisBoard board) {
        return (a*agHeight(board)) + (l*lines(board)) + (h*holes(board)) + (b*bumpiness(board));
    }
    
    public int agHeight (TetrisBoard board) {
        int ans = 0;
        for (int col = 0; col<board.width; col++) {
            for (int row = 0; row<board.height; row++) {
                if (board.board[row][col] != 0) {
                    ans += board.height - row;
                    break;
                } 
            }
        }
        return ans;
    }
    
    public int lines (TetrisBoard board) {
        int ans = 0;
        for (int row = 0; row<board.height; row++) {
            boolean line = true;
            for (int col = 0; col<board.width; col++) {
                if (board.board[row][col] == 0) {
                    line = false;
                    break;
                }
            }
            if (line) ans++;
        }
        return ans;
    }
    // public int wells(TetrisBoard board)
    // {
    //   int ans = 0;
    //   boolean edge_well = false;
    //   boolean piece = true;
    //   int depth = 0;
    //   for(int col = 0; col < board.width; col++)
    //   {
    //     for (int row = 0; row<board.height; row++) {
    //         if((board.board[row][0] == 0) &&( board.board[row][col] !=0))edge_well = true;
    //         else 
    //           {
    //             edge_well = false;
    //             ans = 0;
    //           }
    //         if(board.board[row][board.width-1] == 0 &&( board.board[row][col] !=0))edge_well = true;
    //         else 
    //           {
    //             edge_well = false;
    //             ans = 0;
    //           }

    //         if (board.board[row][0] != 0) piece = false;
    //         if (board.board[row][board.width-1] != 0) piece = false;
    //         if ((edge_well&&piece) && ((board.board[row][0] == 0) || (board.board[row][board.width-1] == 0))) ans++;
    //       }
    //   }
    //    return ans;
    // }
            
    public int holes (TetrisBoard board) {
        int ans = 0;
        for (int col = 0; col<board.width; col++) {
            boolean piece = false;
            for (int row = 0; row<board.height; row++) {
                if (board.board[row][col] != 0) piece = true;
                if (piece && board.board[row][col] == 0) ans++;
            }
        }
        return ans;
    }
    
    public int bumpiness (TetrisBoard board) {
        int ans = 0;
        for (int col = 0; col<board.width-1; col++) {
            int left = 0;
            int right = 0;
            boolean Lset = false;
            boolean Rset = false;
            for (int row = 0; row<board.height; row++) {
                if (board.board[row][col] != 0 && !Lset) {
                    left = board.height - row;
                    Lset = true;
                }
                if (board.board[row][col+1] != 0 && !Rset) {
                    right = board.height - row;
                    Rset = true;
                }
            }
            ans += Math.abs(left - right);
        }
        return ans;
    }
    
    public int[][] matrixCopy (int[][] originalArray) {
        int[][] newArray = new int[originalArray.length][originalArray[0].length];
        for (int x = 0; x < originalArray.length; x++)
            {
                for (int y = 0; y < originalArray[0].length; y++)
                    {
                        newArray[x][y]=originalArray[x][y];
                    }
            }
        return newArray;
    }
}

// class Chromosome
// {
//   double a = 0.0;
//   double l = 0.0;
//   double h = 0.0;
//   double b = 0.0;
//   int score = 0;
//   ArrayList<Double> alleles = new ArrayList<>();

//   public Chromosome()
//   {
//     a = (Math.random() * -1)+ (Math.random() * 1);
//     l = (Math.random() * -1)+ (Math.random() * 1);
//     h = (Math.random() * -1)+ (Math.random() * 1);
//     b = (Math.random() * -1)+ (Math.random() * 1);
//   }
//   public void setScore(int _score)
//   {
//     score = _score;
//   }
//   public int getScore()
//   {
//     return score;
//   }
//   public ArrayList<Double> getAlleles()
//   {
//     return alleles;
//   }

// }
// class GA{
  
//   double MUTATION_RATE = 0.05;
//   double CROSSOVER_RATE = 0.6;
//   int POP_SIZE = 10;
//   int EPOCHS = 10;
//   ArrayList<Chromosome> population;
//   ArrayList<Integer> fitnesses;
//   public GA()
//   {
//     population = new ArrayList<>();
//     createPopulation();   
//     setPopulationFitness();
//     evolve();  
//   }

//   public int getFitness(Chromosome chrom)
//   {
//     int sum = 0;
//     for(int i = 0; i < 10; i++)
//     {
//         SearchBot bot = new SearchBot(chrom.a, chrom.l, chrom.h, chrom.b);
//         int fitness = TetrisGame.playGame(10, 20, bot, false, 0);
//         sum += fitness;
//     }
//     return sum / 10;
//   }
//   public void createPopulation()
//   {
//     for(int i = 0; i < POP_SIZE; i++)
//     {
//       population.add(generateRandomChromosome());
//     }
//   }
//   public Chromosome generateRandomChromosome()
//   {
//       Chromosome new_chrom = new Chromosome();
//       return new_chrom;

//   }
//   public Chromosome reproduce( Chromosome p1, Chromosome p2) {
//     int crossOverSite = rand.nextInt(3);
//     GASolution child;
//     if(crossOverSite == 0) {
//       child = new Chromosome(p1.a,p2.h,p2.hW,p2.bW);
//     }
//     else if( crossOverSite == 1){
//       child = new Chromosome(p1.aghW,p1.lW,p2.hW,p2.bW);
//     }
//     else if( crossOverSite == 2){
//       child = new Chromosome(p1.aghW,p1.lW,p1.hW,p2.bW);
//     }
//     return mutate(child);
//   }

//   public void mutate(Chromosome chrom)
//   {//Iterate through ArrayList of doubles stored in alleles
//    //These doubles map to a, l, h, b, in that order
//   }
//   public void populationReproduce()
//   {
//     ArrayList<Chromosome> newPopulation = new ArrayList<>();

//     for(Chromosome chrom : population)
//     {
//       Chromosome mom = new Chromosome();
//       Chromosome dad = new Chromosome();
//       Chromosome child = new Chromosome();

//       // mom = pickFitParent();
//       // dad = pickFitParent();

//       //child = reproduce(mom, dad);

//       newPopulation.add(child);
//     }
//     for(int i = 0; i < population.size(); i++)
//     {
//       population.set(i, newPopulation.get(i));
//     }
//   }
//   public void pickFitParent()
//   {//Find a fit parent through a loop with a tendency 
//    //towards picking parents with larger fitness values

//   }
//   public void setPopulationFitness()
//   {
//     ArrayList<Integer> newFitnesses = new ArrayList<>();
//     for(int i = 0; i < population.size(); i++)
//     {
//       int fitness = getFitness(population.get(i));
//       newFitnesses.add(fitness);
//     }
//     fitnesses = newFitnesses;
//   }
//   public int getPopulationFitnessSum()
//   {
//     int sum = 0;
//     for(int i = 0; i < population.size(); i++)
//     {
//       Chromosome curr_chrom = population.get(i);
//       sum += curr_chrom.getScore();
//     }
//     return sum;
//   }
//   public void newGeneration()
//   {
//     populationReproduce();
//     setPopulationFitness();
//   }
//   public void evolve()
//   {//Start the GA process for a set number of epochs
//     for(int i = 0; i < EPOCHS; i++)
//     {
//       newGeneration();
//     }
//   }
// }