package student_player;

import hus.HusBoardState;
import hus.HusPlayer;
import hus.HusMove;

import java.util.ArrayList;
import java.util.Hashtable;

import student_player.mytools.MyTools;

/** A Hus player submitted by a student. */
public class StudentPlayer extends HusPlayer {

	private Hashtable<HusBoardState,Integer> transpositionTable; //not sure if we are allowed to use hash tables
	private boolean saveStateScores;
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
    	if(board_state.getTurnNumber()==1 || board_state.getTurnNumber()==0){
    		transpositionTable= new Hashtable<HusBoardState,Integer>();
    		saveStateScores= true;
    	}
    	else{
    		saveStateScores= false;
    	}
    	int v= (int)Double.NEGATIVE_INFINITY;
    	HusMove chosenMove=null;
    	ArrayList<HusMove> moves = board_state.getLegalMoves();
    	for (HusMove move : moves){
    		int temp=  minValue (moveResult(board_state, move),5,0,(int)Double.NEGATIVE_INFINITY, (int)Double.POSITIVE_INFINITY);
    		if(v < temp){
    			v= temp;
    			chosenMove= move;
    		}
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
    
    public int maxValue (HusBoardState board_state, int depth, int current_depth, int alpha, int beta){
    	current_depth++;
    	if (board_state.gameOver() || depth==current_depth) {
    		return boardUtility(board_state);
    	}
    	int v= (int)Double.NEGATIVE_INFINITY;
    	ArrayList<HusMove> moves = board_state.getLegalMoves();
    	for (HusMove move : moves){
    		int temp=  minValue (moveResult(board_state, move),depth,current_depth,alpha,beta);
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
    
    public int minValue (HusBoardState board_state, int depth, int current_depth,int alpha, int beta){
    	current_depth++;
    	if (board_state.gameOver() || depth==current_depth) {
    		return boardUtility(board_state);
    	}
    	int v= (int)Double.POSITIVE_INFINITY;
    	ArrayList<HusMove> moves = board_state.getLegalMoves();
    	for (HusMove move : moves){
    		int temp=  maxValue (moveResult(board_state, move),depth,current_depth,alpha,beta);
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
