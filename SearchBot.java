
import java.io.FileWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.awt.*;
import javax.swing.*;
import java.util.*;
import java.awt.event.*;
import java.lang.reflect.*;

class GASolution{

    public double aghW;
    public double lW;
    public double hW;
    public double bW;


    public GASolution(double _aghW, double _lW, double _hW, double _bW ) {

        aghW = _aghW;
        lW = _lW;
        hW = _hW;
        bW = _bW;
    }
    public void setWeights(double _aghW, double _lW, double _hW, double _bW){
        aghW = _aghW;
        lW = _lW;
        hW = _hW;
        bW = _bW;
    }
}

class GATetris{

    ArrayList<GASolution> population;
    ArrayList<Integer> fitnesses;
    int totalPopulationFitness;
    Random rand = new Random();

    double MUTATION_RATE = 0.05;
    double MUTATION_CHANGE = 0.1;
    int POPULATION_SIZE = 20;
    int NUMBER_GENERATIONS = 1000;
    int NUMBER_GAMES = 1;

    public GATetris() 
    {
        population = new ArrayList<GASolution>();
        createPopulation();
        System.out.println("Population Created");
        evolve();
        System.out.println("Evolve called in constructor");
    }

    public void createPopulation() 
    {
        for( int i = 0; i < POPULATION_SIZE; i++) 
            {
                double aghW = -Math.random();
                double lW = Math.random();
                double hW = -Math.random();
                double bW = -Math.random();

                GASolution solution = new GASolution(aghW, lW, hW, bW);
                population.add(solution);
            }
    }

    public void populationReproduce() 
    {
        ArrayList<GASolution> new_population = new ArrayList<GASolution>();
        for( int i = 0; i < population.size(); i++) 
            {
                GASolution p1 = pickFitParent();
                GASolution p2 = pickFitParent();
                GASolution child = reproduce(p1,p2);
                new_population.add(child);
            }
        population = new_population;
    }

    public GASolution reproduce( GASolution p1, GASolution p2) {
        int crossOverSite = rand.nextInt(3);
        GASolution child = new GASolution(0, 0, 0, 0);
        if(crossOverSite == 0) {
            child.setWeights(p1.aghW,p2.lW,p2.hW,p2.bW);
        }
        else if( crossOverSite == 1){
            child.setWeights(p1.aghW,p1.lW,p2.hW,p2.bW);
        }
        else if( crossOverSite == 2){
            child.setWeights(p1.aghW,p1.lW,p1.hW,p2.bW);
        }
        return mutate(child);
    }

    public GASolution mutate(GASolution child){
        GASolution mutated_child;
        for(int i = 0; i < 4; i++)
            {
                if(rand.nextFloat() < MUTATION_RATE){
                    double new_aghW;
                    double new_lW;
                    double new_hW;
                    double new_bW;
                    int mutator = rand.nextInt(4);
                    if(mutator == 0) { 
                        new_aghW = child.aghW;
                        double mutation = rand.nextDouble();
                        double rando = Math.random();
                        if(mutation < .5 && new_aghW <= 1.0 - MUTATION_CHANGE *rando)
                            {
                                new_aghW += MUTATION_CHANGE * Math.random();
                            }
                        else if(new_aghW >= -1.0 + MUTATION_CHANGE * rando){
                            new_aghW -= MUTATION_CHANGE * Math.random();
                        }
                    }
                    else{
                        new_aghW = child.aghW;
                    }
                    if(mutator == 1) {
                        double rando = Math.random();
                        new_lW = child.lW;
                        double mutation = rand.nextDouble();
                        if(mutation < .5 && new_lW <= 1.0 - MUTATION_CHANGE * rando)
                            {
                                new_lW += MUTATION_CHANGE * Math.random();
                            }
                        else if(new_lW >= -1.0 + MUTATION_CHANGE * rando){
                            new_lW -= MUTATION_CHANGE * rando;
                        }
                    }
                    else{
                        new_lW = child.lW;
                    }

                    if(mutator == 2) {
                        double rando = Math.random();
                        new_hW = child.hW;
                        double mutation = rand.nextDouble();
                        if(mutation < .5 && new_hW <= 1.0 - MUTATION_CHANGE* rando)
                            {
                                new_hW += MUTATION_CHANGE * rando;
                            }
                        else if(new_hW > -1.0 + MUTATION_CHANGE*rando){
                            new_hW -= MUTATION_CHANGE * rando;
                        }
                    }
                    else{
                        new_hW = child.hW;
                    }

                    if(mutator == 3) {
                        double rando = Math.random();
                        new_bW = child.bW;
                        double mutation = rand.nextDouble();
                        if(mutation < .5 && new_bW <= 1.0 - MUTATION_CHANGE*rando)
                            {
                                new_bW += MUTATION_CHANGE * rando;
                            }
                        else if(new_bW > -1.0 + MUTATION_CHANGE*rando){
                            new_bW -= MUTATION_CHANGE * rando;
                        }
                    }
                    else{
                        new_bW = child.bW;
                    }
                    return mutated_child = new GASolution(new_aghW, new_lW, new_hW, new_bW);
                }
            }
        return child;
    }

    public void setPopulationFitness(){
        fitnesses = new ArrayList<Integer>();
        for( GASolution solution: population) {
            fitnesses.add(getFitness(solution));
        }
        totalPopulationFitness = 0;
        for( int i = 0; i < fitnesses.size(); i++){
            totalPopulationFitness += fitnesses.get(i);
        }
    }

    public GASolution pickFitParent() 
    {
        double r = Math.random()*totalPopulationFitness;
        double running_sum = fitnesses.get(0);
        int current_parent = 0;
        while( running_sum <= r) {
            running_sum += fitnesses.get(current_parent);
            current_parent++;
        }
        if( current_parent == 0) {
            return population.get(current_parent);
        }
        return population.get(current_parent-1);
    }

    public int getFitness(GASolution chromosome) {
        int playerFitness = 0;
        for( int i = 0; i < NUMBER_GAMES; i++) {
            SearchBot player = new SearchBot(chromosome.aghW, chromosome.lW, chromosome.hW, chromosome.bW);
            int fitness = TetrisGame.playGame(10, 20, player, false, 0);
            playerFitness += fitness;
        }
        return playerFitness/NUMBER_GAMES;
    }

    public void createNewGeneration() {
        populationReproduce();
        setPopulationFitness();
    }

    public GASolution getBestSolution(int gen) {

        double best_fit = 0;
        int best_fit_index = 0;
        for( int i = 0; i < fitnesses.size(); i++){
            if(fitnesses.get(i) > best_fit)
                {
                    best_fit_index = i;
                    best_fit = fitnesses.get(i);
                }
        }
        GASolution curr_best = population.get(best_fit_index);
        System.out.println(gen +  ", " + curr_best.aghW + ", " + curr_best.lW + ", " +  curr_best.hW + ", " + curr_best.bW);
        return population.get(best_fit_index);
    }


    public void evolve() {
        System.out.println("Evolve Started");
        GASolution allTimeBest = new GASolution(0, 0, 0, 0);
        setPopulationFitness();
        System.out.println("Fitness Set");
        for(int i = 0; i < NUMBER_GENERATIONS; i++) {
            System.out.println("Entered Evolve loop");
            createNewGeneration();
            System.out.println("Generation Created");
            GASolution best = getBestSolution(i);
            System.out.println("GENERATION: " + i +" BEST FITNESS: " + getFitness(best));
            if(i == NUMBER_GENERATIONS - 1)
                {
                    allTimeBest = best;
                }
        }
        System.out.println("aghW: " + allTimeBest.aghW + "| lW: " + allTimeBest.lW + "| hW: " + allTimeBest.hW + "| bW: " + allTimeBest.bW);
    }
}


public class SearchBot extends TetrisBot {

    double aggrW;
    double lineW;
    double holesW;
    double bumpW;
    //data stuff
    FileWriter pw;
    File outputFile = new File("test.txt");

    public SearchBot(double _aghW, double _lW, double _hW, double _bW){
        aggrW = _aghW;
        lineW = _lW;
        holesW = _hW;
        bumpW = _bW;
        try {
            pw = new FileWriter(outputFile, true);
        }
        catch (FileNotFoundException e){}
        catch (IOException e){}
    }

    public TetrisMove chooseMove(TetrisBoard board, TetrisPiece current_piece, TetrisPiece next_piece) 
    {

        //data collection from GA
        double[] temp = board.getContour();
        float[] contour = new float[temp.length];
        for(int i=0; i<temp.length;i++){
            contour[i] = (float)temp[i];
            //System.out.println(contour[i]);
        }        int[] currPieceArray = getPieceList(current_piece);
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
        //System.out.println("HOLES: "+ holes(board));
        //System.out.println("WELLS: "+ wells(board)); 
        double best_score = -9999.0;
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

                        double curr_score = eval(temp_board) + lookAhead(temp_board, next_piece);
                        if (curr_score > best_score) {
                                best_rot = i;
                                best_col = col;
                                best_score = curr_score;
                            }
                    }
        }
        //set up output vector data
        int[] output = new int[board.width + 4];
        for(int i = 0; i< output.length;i++){
            output[i] = -1;
        }
        int rotations = best_rot;
        output[best_col] = 1;
        output[board.width + rotations] = 1;
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
        int[] output = new int[] {-1,-1,-1,-1,-1,-1,-1};
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

    public double lookAhead(TetrisBoard board, TetrisPiece next_piece) 
    {        
        double best_score = -9999.0;
        int best_rot = 0;
        int best_col = 0;
        for (int i = 0; i < 4; i++) 
            {
                TetrisPiece temp_piece = next_piece.rotatePiece(i);
                for (int col = 0; col <= board.width-next_piece.width ; col++) 
                    {
                        //create copy board
                        TetrisBoard temp_board = new TetrisBoard(board.width, board.height, false);
                        temp_board.board = matrixCopy(board.board);

                        //place temp piece at col
                        TetrisMove temp_move = new TetrisMove (next_piece, col);
                        temp_board.addPiece(temp_move);

                        //check if better
                        if (eval(temp_board) > best_score) 
                            {
                                best_rot = i;
                                best_col = col;
                                best_score = eval(temp_board);
                            }
                    }
            }             
        return best_score;
    }

    public double eval (TetrisBoard board) {
        return (aggrW*agHeight(board)) + (lineW*lines(board)) + (holesW*holes(board)) + (bumpW*bumpiness(board));
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
    public static void main(String[] args) {
        GATetris geneticAlgorithm = new GATetris();
    }
}


