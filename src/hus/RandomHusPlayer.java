package hus;

import hus.HusBoardState;
import hus.HusPlayer;
import hus.HusMove;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Random;

/** A random Hus player. */
public class RandomHusPlayer extends HusPlayer {
    Random rand = new Random();
    private double[] weights;
	private ArrayList<Double>gameMovesPreformed;
	private ArrayList<double[]>movesFeatures;
	private PrintWriter writer;
	
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
    	if(board_state.getTurnNumber()==0){
    		return chooseOptimalMove(board_state);
    	}
    	else{
	    	int  n = rand.nextInt(10) + 1;
	    	if(11>n){
	    		return chooseOptimalMove(board_state);
	    	}
	    	else{
	    		 return chooseRandomMove(board_state);
	    	}
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
  	if(board_state.getTurnNumber()==0){
  		
  		gameMovesPreformed= new ArrayList<Double>();
  		movesFeatures=new ArrayList<double[]>();
  		try {
				FileReader in;
				File f = new File("../learning2.txt");
				in = new FileReader(f);
				BufferedReader br = new BufferedReader(in);
				 String line = br.readLine();
				 String [] myTokens;
				 while(line!=null){
					 myTokens= line.split(" ");
									
					 gameMovesPreformed.add(Double.parseDouble(myTokens[0]));
					 double[] myFeatures= new double[myTokens.length-1];
					 for(int i=0;i<myTokens.length-1;i++){ 
						 myFeatures[i] =Double.parseDouble(myTokens[i+1]); 
						 movesFeatures.add(myFeatures);
					 }
					 line = br.readLine();	
				 }
		    	 in.close();
		    	
		    	 f.delete();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try {
				FileReader in;
				in = new FileReader("../evaluation_function_weights2.txt");
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
//			 learnWeights();
			 gameMovesPreformed.clear();
			 movesFeatures.clear();
			 String strategy=weights[0]+"";
			 for(int i=1;i< weights.length;i++){
				 strategy=strategy+" "+weights[i];
			 }
	    	   PrintWriter writer;
				try {
					writer = new PrintWriter("../evaluation_function_weights2.txt", "UTF-8");
					writer.println(strategy);
		            writer.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
  	}
  	double v= Double.NEGATIVE_INFINITY;
  	HusMove chosenMove=null;
  	ArrayList<HusMove> moves = board_state.getLegalMoves();
  	for (HusMove move : moves){
  		double temp=  minValue (moveResult(board_state, move),5,0,(int)Double.NEGATIVE_INFINITY, (int)Double.POSITIVE_INFINITY);
  		if(v < temp){
  			v= temp;
  			chosenMove= move;
  		}
  	}
  	double[] myFeatures= numberOfPitsScore(board_state);
//  	System.out.println("I chose to go with a move that has a utility of : "+boardUtility(moveResult(board_state, chosenMove)));
  	try {
  		
			writer = new PrintWriter(new FileWriter("../learning2.txt", true));
			String printLine=v+"";
			 for(int i=0;i< weights.length;i++){
				 printLine=printLine+" "+myFeatures[i];
			 }
			writer.println(printLine);
          writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
  	gameMovesPreformed.add(v);
  	movesFeatures.add(myFeatures);
//  	printMovesSoFar();
  	return chosenMove;
  }
  
  public double maxValue (HusBoardState board_state, int depth, int current_depth, double alpha, double beta){
  	current_depth++;
  	if (board_state.gameOver()) {
  		if(board_state.getWinner()==player_id){
  			return 1;
  		}
  		else if (board_state.getWinner()==opponent_id){
  			return -1;
  		}
  		else{
  			return 0;
  		}
  		
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
  		if(board_state.getWinner()==player_id){
  			return 1;
  		}
  		else if (board_state.getWinner()==opponent_id){
  			return -1;
  		}
  		else{
  			return 0;
  		}
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
  public double boardUtility (HusBoardState board_state){
  	double [] scores = numberOfPitsScore(board_state);
  	int evalFunction=0;
  	for(int i=0;i< weights.length;i++){
  		evalFunction+=(weights[i]*scores[i]);
  	}
  	return Math.tanh(0.0005*evalFunction);
//  	int differenceInSeeds= scores[0]- scores[1];
//  	int opponentOneOrZeroPits= scores[3]+scores[5];
//  	int myOneOrZeroPits = scores[2]+scores[4];
//  	return (2*differenceInSeeds)+(1*(opponentOneOrZeroPits-myOneOrZeroPits));
//  	return (2*numberOfPitsScore(board_state))+(op_score-my_score);
  }
  
  
  public double[] numberOfPitsScore (HusBoardState board_state){
  	double [] scores= new double[10]; //first place my pits score, opp score, my empty, opp emty, my 1's, opp 1's
  	int [][] pits = board_state.getPits();
  	int[] my_pits = pits[player_id];
      int[] op_pits = pits[opponent_id];
      for(int pit : my_pits){ 
      	if(pit==0) { //pits that have 0
      		scores[2]=scores[2]+1;
      	}
      	else if (pit ==1){ //pits that have 1
      		scores[4]=scores[4]+pit;
      	}
      	scores[0]=scores[0]+pit; // my seeds
      }
      
      for(int i=16;i< my_pits.length;i++){
    	  if(my_pits[i]>1){
    		 scores[6]=scores[6]+1; //pits in the middle used to attack
    		 scores[7]= scores[7]+my_pits[i]; //seeds in the middle used to attack 
    	  }
      }
      
      for(int pit : op_pits){
      	if(pit==0) {
      		scores[3]=scores[3]+pit;
      	}
      	else if (pit ==1){
      		scores[5]=scores[5]+pit;
      	}
      	scores[1]=scores[1]+pit; //my seeds
      }
      for(int i=16;i< my_pits.length;i++){
    	  if(my_pits[i]>1){
    		 scores[8]=scores[8]+1; //pits in the middle used to attack me
    		 scores[9]= scores[9]+my_pits[i]; //seeds in the middle used to attack me
    	  }
      }
      return scores;
  }
  
  public void learnWeights(){
  	double [] tDValues= new double[this.gameMovesPreformed.size()-1];
  	double alpha = 0.00011;
  	for(int i=0; i<this.gameMovesPreformed.size()-1;i++){
  		tDValues[i]= this.gameMovesPreformed.get(i+1)- this.gameMovesPreformed.get(i);
  		for(int j=0; j<this.weights.length;j++){
    		if(tDValues[i]<0 && weights[j]>0){
    		weights[j]+=(alpha*tDValues[i])*this.movesFeatures.get(i)[j];
    		}
    		else if(tDValues[i]>0 && weights[j]<0){
    			weights[j]+=(alpha*tDValues[i])*this.movesFeatures.get(i)[j];
    		}
    	}
  	}

  	
  }
  public void printMovesSoFar(){
  	for(int i=0; i< this.gameMovesPreformed.size();i++){
  		System.out.println("move number "+i+" had u equals "+gameMovesPreformed.get(i));
  	}
  }
  private static double sigmoid(double x)
  {
      return 1 / (1 + Math.exp(-x));
  }
}
