package student_player;

import hus.HusBoardState;
import hus.HusPlayer;
import hus.HusMove;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Hashtable;

import student_player.mytools.MyTools;

/** A Hus player submitted by a student. */
public class StudentPlayer extends HusPlayer {

	private Hashtable<HusBoardState,Integer> transpositionTable; //not sure if we are allowed to use hash tables
	private boolean saveStateScores;
	private double[] weights;
	private double finalBoardScore;
	private double[] previousWeights;
    /** You must modify this constructor to return your student number.
     * This is important, because this is what the code that runs the
     * competition uses to associate you with your agent.
     * The constructor should do nothing else. */
    public StudentPlayer() { super("260549558"); }

    /** This is the primary method that you need to implement.
     * The ``board_state`` object contains the current state of the game,
     * which your agent can use to make decisions. See the class hus.RandomHusPlayer
     * for another example agent. */
    public HusMove chooseMove(HusBoardState board_state)
    {
    	
    	if(board_state.getTurnNumber()==0){
    		transpositionTable= new Hashtable<HusBoardState,Integer>();
    		saveStateScores= true;
			try {
				FileReader in;
				in = new FileReader("../evaluation_function_weights.txt");
				BufferedReader br = new BufferedReader(in);
				 String line = br.readLine();
		    	 System.out.println(line);
		    	 String [] myWeights= line.split(" ");
		    	 weights= new double[myWeights.length];
		    	 for(int i=0;i<myWeights.length;i++){
		    		 weights[i]= Double.parseDouble(myWeights[i]);
		    	 }
		    	 in.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	else{
    		String strategy=weights[0]+" "+weights[1]+" "+weights[2]+" "+weights[3]+" "+weights[4]+" "+weights[5];
    		PrintWriter writer;
			try {
				writer = new PrintWriter("../evaluation_function_weights.txt", "UTF-8");
				writer.println(strategy);
	            writer.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		saveStateScores= false;
    	}
    	double v= Double.NEGATIVE_INFINITY;
    	HusMove chosenMove=null;
    	double temp= Double.NEGATIVE_INFINITY;
    	ArrayList<HusMove> moves = board_state.getLegalMoves();
    	for (HusMove move : moves){
    		temp=  minValue (moveResult(board_state, move),5,0,(int)Double.NEGATIVE_INFINITY, (int)Double.POSITIVE_INFINITY);
    		if(v < temp){
    			v= temp;
    			chosenMove= move;
    		}
    	}
    	System.out.println("I chose to go with a move that has a utility of : "+boardUtility(moveResult(board_state, chosenMove)));
    	if(board_state.getTurnNumber()==0){
    		finalBoardScore= temp;
    		previousWeights = numberOfPitsScore (board_state);
    	}
    	else{
    		learnWeights(moveResult(board_state, chosenMove),board_state, temp);
    	}
    	return chosenMove;
//        // Get the contents of the pits so we can use it to make decisions.
//        int[][] pits = board_state.getPits();
//
//        // Use ``player_id`` and ``opponent_id`` to get my pits and opponent pits.
//        int[] my_pits = pits[player_id];
//        int[] op_pits = pits[opponent_id];
//
//        // Use code stored in ``mytools`` package.
//        MyTools.getSomething();
//
//        // Get the legal moves for the current board state.
//        ArrayList<HusMove> moves = board_state.getLegalMoves();
//        HusMove move = moves.get(0);
//
//        // We can see the effects of a move like this...
//        HusBoardState cloned_board_state = (HusBoardState) board_state.clone();
//        cloned_board_state.move(move);
//
//        // But since this is a placeholder algorithm, we won't act on that information.
//        return move;
    }
    
    public double maxValue (HusBoardState board_state, int depth, int current_depth, double alpha, double beta){
    	current_depth++;
    	if (board_state.gameOver()) {
    		return 2*boardUtility(board_state);
    	}
    	else if( depth==current_depth){
    		return boardUtility(board_state);
    	}
    	double v= Double.NEGATIVE_INFINITY;
    	ArrayList<HusMove> moves = board_state.getLegalMoves();
    	for (HusMove move : moves){
    		double temp=  minValue (moveResult(board_state, move),depth,current_depth,alpha,beta);
    		if(v < temp){
    			v= temp;
    		}
    		if (v>=beta){
    			return v;
    		}
    		alpha=Math.max(alpha, v);
    	}
    	return v;
    } 
    
    public double minValue (HusBoardState board_state, int depth, int current_depth,double alpha, double beta){
    	current_depth++;
    	if (board_state.gameOver()) {
    		return 2*boardUtility(board_state);
    	}
    	else if( depth==current_depth){
    		return boardUtility(board_state);
    	}
    	double v= Double.POSITIVE_INFINITY;
    	ArrayList<HusMove> moves = board_state.getLegalMoves();
    	for (HusMove move : moves){
    		double temp=  maxValue (moveResult(board_state, move),depth,current_depth,alpha,beta);
    		if(v > temp){
    			v= temp;
    		}
    		if (v<=alpha){
    			return v;
    		}
    		beta=Math.min(beta, v);
    	}
    	
    	return v;
    } 
    
    public HusBoardState moveResult(HusBoardState board_state,HusMove move ){
    	//System.out.println(move.toPrettyString());
    	HusBoardState cloned_board_state = (HusBoardState) board_state.clone();
        cloned_board_state.move(move);
        return cloned_board_state;
    }
    public int boardUtility (HusBoardState board_state){
    	double [] scores = numberOfPitsScore(board_state);
    	int evalFunction=0;
    	for(int i=0;i< weights.length;i++){
    		evalFunction+=(weights[i]*scores[i]);
    	}
    	return evalFunction;
//    	int differenceInSeeds= scores[0]- scores[1];
//    	int opponentOneOrZeroPits= scores[3]+scores[5];
//    	int myOneOrZeroPits = scores[2]+scores[4];
//    	return (2*differenceInSeeds)+(1*(opponentOneOrZeroPits-myOneOrZeroPits));
//    	return (2*numberOfPitsScore(board_state))+(op_score-my_score);
    }
    
    
    public double[] numberOfPitsScore (HusBoardState board_state){
    	double [] scores= new double[6]; //first place my pits score, opp score, my empty, opp emty, my 1's, opp 1's
    	int [][] pits = board_state.getPits();
    	int[] my_pits = pits[player_id];
        int[] op_pits = pits[opponent_id];
        for(int pit : my_pits){
        	if(pit==0) {
        		scores[2]=scores[2]+pit;
        	}
        	else if (pit ==1){
        		scores[4]=scores[4]+pit;
        	}
        	scores[0]=scores[0]+pit;
        }
        
        for(int pit : op_pits){
        	if(pit==0) {
        		scores[3]=scores[3]+pit;
        	}
        	else if (pit ==1){
        		scores[5]=scores[5]+pit;
        	}
        	scores[1]=scores[1]+pit;
        }
        return scores;
//    	return (my_score-op_score);
    }
    
    public void learnWeights(HusBoardState board_state, HusBoardState pboard_state, double cuurentEval){
//    	double ytp1= boardUtility (board_state);
    	double ytp1= cuurentEval;
//    	double yt =boardUtility (pboard_state) ;
    	double yt= finalBoardScore;
//    	double yt =172;
    	double [] currentFeatures= numberOfPitsScore (pboard_state);
    	double alpha = 0.0000011;
    	double coeff= alpha*(ytp1-yt);
    	for(int i=0;i< weights.length;i++){
    		if(currentFeatures[i]!=0){
    			weights[i]+=coeff*(currentFeatures[i]);
    		}
    	}
    	finalBoardScore= cuurentEval;
//    	yt= ytp1;
//    	previousWeights= numberOfPitsScore (board_state);
    }
}
