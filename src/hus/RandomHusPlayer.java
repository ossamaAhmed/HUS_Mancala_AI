package hus;

import hus.HusBoardState;
import hus.HusPlayer;
import hus.HusMove;

import java.util.ArrayList;
import java.util.Random;

/** A random Hus player. */
public class RandomHusPlayer extends HusPlayer {
    Random rand = new Random();

    public RandomHusPlayer() { super("RandomHusPlayer"); }

    /** Choose moves randomly. */
//    public HusMove chooseMove(HusBoardState board_state)
//    {
//        // Pick a random move from the set of legal moves.
//        ArrayList<HusMove> moves = board_state.getLegalMoves();
//        HusMove move = moves.get(rand.nextInt(moves.size()));
//        return move;
//    }
    /*
     * ADDED STUFF HERE <<,, TO BE REMOVED
     */
    
    public HusMove chooseMove(HusBoardState board_state)
    {
    	Random rand = new Random();

    	int  n = rand.nextInt(10) + 1;
    	if(5>n){
    		return chooseOptimalMove(board_state);
    	}
    	else{
    		 return chooseRandomMove(board_state);
    	}
    }
    
   
    
  public HusMove chooseRandomMove(HusBoardState board_state)
  {
      // Pick a random move from the set of legal moves.
      ArrayList<HusMove> moves = board_state.getLegalMoves();
      HusMove move = moves.get(rand.nextInt(moves.size()));
      return move;
  }
    
    public HusMove chooseOptimalMove(HusBoardState board_state)
    {
    	int v= (int)Double.NEGATIVE_INFINITY;
    	HusMove chosenMove=null;
    	ArrayList<HusMove> moves = board_state.getLegalMoves();
    	for (HusMove move : moves){
    		int temp=  minValue (moveResult(board_state, move),5,0);
    		if(v < temp){
    			v= temp;
    			chosenMove= move;
    		}
    	}
    	return chosenMove;
    }
    
    public int maxValue (HusBoardState board_state, int depth, int current_depth){
    	current_depth++;
    	if (board_state.gameOver() || depth==current_depth) {
    		return boardUtility(board_state);
    	}
    	int v= (int)Double.NEGATIVE_INFINITY;
    	ArrayList<HusMove> moves = board_state.getLegalMoves();
    	for (HusMove move : moves){
    		int temp=  minValue (moveResult(board_state, move),depth,current_depth);
    		if(v < temp){
    			v= temp;
    		}
    	}
    	return v;
    } 
    
    public int minValue (HusBoardState board_state, int depth, int current_depth){
    	current_depth++;
    	if (board_state.gameOver() || depth==current_depth) {
    		return boardUtility(board_state);
    	}
    	int v= (int)Double.POSITIVE_INFINITY;
    	ArrayList<HusMove> moves = board_state.getLegalMoves();
    	for (HusMove move : moves){
    		int temp=  maxValue (moveResult(board_state, move),depth,current_depth);
    		if(v > temp){
    			v= temp;
    		}
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
    	int [] scores = numberOfPitsScore(board_state);
    	int differenceInSeeds= scores[0]- scores[1];
    	int opponentOneOrZeroPits= scores[3]+scores[5];
    	int myOneOrZeroPits = scores[2]+scores[4];
    	return (2*differenceInSeeds)+(1*(opponentOneOrZeroPits-myOneOrZeroPits));
//    	return (2*numberOfPitsScore(board_state))+(op_score-my_score);
    }
    
    
    public int[] numberOfPitsScore (HusBoardState board_state){
    	int [] scores= new int[6]; //first place my pits score, opp score, my empty, opp emty, my 1's, opp 1's
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
}
