package pkg;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Stack;

public class Solver {
	private static Board solution;
	private static boolean isSolved = false;

	public static int pops = 0;
	public static int maxSize = 0;
	public final static String easy = "134862705";
	public final static String medm = "281043765";
	public final static String hard = "567408321";
	public static String rand = "000000000";
	public static String cstm = "012345678";
	
	/* [General layout of all algorithms]
	 * 
	 * Set the base variables for running the algorithm (init the stack/queue and previous configuration list, push the root, check if the root is solved)
	 * While the stack/queue is not empty or it isn't solved:
	 * 	Check/Throw if func has been running for > 5 minutes
	 * 	If the stack/queue is empty, return
	 * 	Pop the next item to parse
	 * 	Get an ArrayList of the possible ways the empty tile can move from its given position
	 * 	For each of those possibilities:
	 * 		Make a new board exploring that possibility, setting the parent to the popped item above
	 * 		Check if its solved, returning the function if it is
	 * 		Add the new board to its parent's children
	 * 		Also add it to the previous configuration list (to avoid repeats/infinite pops)
	 * 		Push it onto the stack/queue
	 * 			(When pushing it, funcs other than BFS/DFS will include updated details on the current cost)
	 * 		Increment the number of times it has looped
	 */
	
	/*
	 * Starter function for the UI to provide input to
	 * @param diff The difficulty setting
	 * @param alg The chosen algorithm setting
	 * @return Board The solution board
	 */
	public static Board solve(String diff, String alg)
	{
		Board puzzle;
		if (diff == "Easy")
			puzzle = new Board(easy, null);
		else if (diff == "Medium")
			puzzle = new Board(medm, null);
		else if (diff == "Hard")
			puzzle = new Board(hard, null);
		else if (diff == "Random")
			puzzle = new Board(rand, null);
		else // diff == "Custom"
			puzzle = new Board(cstm, null);
		
		pops = 0;
		maxSize = 0;
		if (alg == "DFS")
			explore_DFS(puzzle);
		else if (alg == "BFS")
			explore_BFS(puzzle);
		else if (alg == "UC")
			explore_UCS(puzzle);
		else if (alg == "BF")
			explore_GBF(puzzle);
		else if (alg == "A1")
			explore_AS1(puzzle);
		else // alg == "A2"
			explore_AS2(puzzle);
		
		return solution;
	}

	/*
	 * Uses a stack (FILO) to explore the possibilities
	 */
	private static void explore_DFS(Board root)
	{
		long initTime = System.currentTimeMillis();
		Stack<Board> stack = new Stack<Board>(); 
		ArrayList<String> prevConfigs = new ArrayList<String>(); 
		stack.push(root); 
		if (root.isSolved())
			{isSolved = true; solution = root; return;}
		if (stack.size() > maxSize) maxSize = stack.size();
		while (!stack.isEmpty() || !isSolved) 
		{
			if (System.currentTimeMillis() - initTime > 300000)
				throw new UnsupportedOperationException("Program is not set to run for more than 5 minutes (ie could not find an answer)");
			if (stack.isEmpty()) return;
			if (stack.size() > maxSize) maxSize = stack.size();
			Board currBoard = stack.pop(); 
			pops += 1;
			ArrayList<Character> possMoves = currBoard.getPossMoveDirs(); 
			
			for (char move: possMoves) // 
			{
				Board child = new Board(currBoard.getTiles(), currBoard); 
				child.move(move); 
				if (prevConfigs.contains(child.strRep())) 
					continue;
				if (child.isSolved()) 
				{
					isSolved = true;
					solution = child;
					return;
				}
				currBoard.addChild(child); 
				prevConfigs.add(child.strRep()); 
				stack.push(child); 
			}
		}
	}
	
	/*
	 * Uses a queue (FIFO) to explore the possibilities
	 */
	private static void explore_BFS(Board root)
	{ 
		long initTime = System.currentTimeMillis();
		Queue<Board> q = new LinkedList<Board>(); //
		ArrayList<String> prevConfigs = new ArrayList<String>();
		q.add(root); //
		if (root.isSolved())
			{isSolved = true; solution = root; return;}
		if (q.size() > maxSize) maxSize = q.size();
		while (!q.isEmpty() || !isSolved)
		{
			if (System.currentTimeMillis() - initTime > 300000)
				throw new UnsupportedOperationException("Program is not set to run for more than 5 minutes (ie could not find an answer)");
			if (q.isEmpty()) return;
			if (q.size() > maxSize) maxSize = q.size();
			Board currBoard = q.poll(); //
			pops++;
			ArrayList<Character> possMoves = currBoard.getPossMoveDirs();
			for (char move: possMoves)
			{
				Board child = new Board(currBoard.getTiles(), currBoard);
				child.move(move);
				if (prevConfigs.contains(child.strRep()))
					continue;
				if (child.isSolved())
				{
					isSolved = true;
					solution = child;
					return;
				}
				currBoard.addChild(child);
				prevConfigs.add(child.strRep());
				q.add(child);
			}
		}
		
	}

	/*
	 * Uses a priority queue (takes the path with the least total cost) to explore the possibilities
	 * (Where total cost = previous costs + cost to move the current tile)
	 */
	private static void explore_UCS(Board root)
	{
		long initTime = System.currentTimeMillis();
		PriorityQueue<Pair_CostBoard> pq = new PriorityQueue<Pair_CostBoard>(); // "Cost" in this case is # tiles in wrong pos
		ArrayList<Board> prevConfigs = new ArrayList<Board>();
		isSolved = root.isSolved();
		if (isSolved) {solution = root; isSolved = true; return;}
		Pair_CostBoard currPair = new Pair_CostBoard(root.getAmtMisplaced(), root);
		pq.add(currPair);
		prevConfigs.add(root);
		if (pq.size() > maxSize) maxSize = pq.size();
		while (!pq.isEmpty() || !isSolved)
		{
			if (System.currentTimeMillis() - initTime > 300000)
				throw new UnsupportedOperationException("Program is not set to run for more than 5 minutes (ie could not find an answer)");
			if (pq.size() > maxSize) maxSize = pq.size();
			currPair = pq.poll();
			pops++;
			int totalCost = currPair.getKey();
			Board currBoard = currPair.getValue();
			ArrayList<Character> possMoves = currBoard.getPossMoveDirs();
			ArrayList<Integer> possTargets = currBoard.getPossMoveTargets();
			for (char poss: possMoves)
			{
				Board child = new Board(currBoard.getTiles(), currBoard);
				child.move(poss);
				int target = possTargets.get(possMoves.indexOf(poss));
				if (prevConfigs.contains(child))
					continue;
				if (child.isSolved())
				{
					solution = child;
					isSolved = true;
					return;
				}
				currBoard.addChild(child);
				prevConfigs.add(child);
				Pair_CostBoard childPair = new Pair_CostBoard(totalCost + target, child); // set score to total cost so far (inc move that was just made)
				pq.add(childPair);
			}
			
		}
	}
	
	/*
	 * Uses a priority queue to first explore the possibilities with the highest number of tiles in the correct place
	 */
	private static void explore_GBF(Board root)
	{
		long initTime = System.currentTimeMillis();
		PriorityQueue<Pair_CostBoard> pq = new PriorityQueue<Pair_CostBoard>(); // "Cost" in this case is # tiles in wrong pos
		ArrayList<Board> prevConfigs = new ArrayList<Board>();
		isSolved = root.isSolved();
		if (isSolved) {solution = root; isSolved = true; return;}
		Pair_CostBoard currPair = new Pair_CostBoard(root.getAmtMisplaced(), root);
		pq.add(currPair);
		prevConfigs.add(root);
		if (pq.size() > maxSize) maxSize = pq.size();
		while (!pq.isEmpty() || !isSolved)
		{
			if (System.currentTimeMillis() - initTime > 300000)
				throw new UnsupportedOperationException("Program is not set to run for more than 5 minutes (ie could not find an answer)");
			currPair = pq.poll();
			pops++;
			Board currBoard = currPair.getValue();
			ArrayList<Character> possMoves = currBoard.getPossMoveDirs();
			for (char poss: possMoves)
			{
				Board child = new Board(currBoard.getTiles(), currBoard);
				child.move(poss);
				
				if (prevConfigs.contains(child))
					continue;
				if (child.isSolved())
				{
					solution = child;
					isSolved = true;
					return;
				}
				currBoard.addChild(child);
				prevConfigs.add(child);
				Pair_CostBoard childPair = new Pair_CostBoard(child.getAmtMisplaced(), child);
				pq.add(childPair);
			}
			
		}
	}
	
	/*
	 * Explores the possibilities with the best f(n)...
	 * Where f(n) = h(n) + g(n),
	 * 	h(n) = Number of tiles that aren't in the correct position
	 * 	g(n) = Cost to move the current tile (aka its value)
	 */
	private static void explore_AS1(Board root)
	{
		long initTime = System.currentTimeMillis();
		PriorityQueue<Pair_CostBoard> pq = new PriorityQueue<Pair_CostBoard>(); // "Cost" in this case is f(n)
		ArrayList<Board> prevConfigs = new ArrayList<Board>();
		isSolved = root.isSolved();
		if (isSolved) {solution = root; isSolved = true; return;}
		Pair_CostBoard currPair = new Pair_CostBoard(root.getAmtMisplaced(), root);
		pq.add(currPair);
		prevConfigs.add(root);
		if (pq.size() > maxSize) maxSize = pq.size();
		while (!pq.isEmpty() || !isSolved)
		{
			if (System.currentTimeMillis() - initTime > 300000)
				throw new UnsupportedOperationException("Program is not set to run for more than 5 minutes (ie could not find an answer)");
			if (pq.size() > maxSize) maxSize = pq.size();
			currPair = pq.poll();
			pops++;
			Board currBoard = currPair.getValue();
			ArrayList<Character> possMoves = currBoard.getPossMoveDirs();
			ArrayList<Integer> possTargets = currBoard.getPossMoveTargets();
			for (char poss: possMoves)
			{
				Board child = new Board(currBoard.getTiles(), currBoard);
				child.move(poss);
				int target = possTargets.get(possMoves.indexOf(poss));
				if (prevConfigs.contains(child))
					continue;
				if (child.isSolved())
				{
					solution = child;
					isSolved = true;
					return;
				}
				currBoard.addChild(child);
				prevConfigs.add(child);
				Pair_CostBoard childPair = new Pair_CostBoard(target + child.getAmtMisplaced(), child);
				pq.add(childPair);
			}
		}
	}
	
	/*
	 * Explores the possibilities with the best f(n)...
	 * Where f(n) = h(n) + g(n),
	 * 	h(n) = Sum of Manhattan distances of all the tiles
	 * 	g(n) = Cost to move the current tile (aka its value)
	 */
	private static void explore_AS2(Board root)
	{
		long initTime = System.currentTimeMillis();
		PriorityQueue<Pair_CostBoard> pq = new PriorityQueue<Pair_CostBoard>(); // "Cost" in this case is f(n)
		ArrayList<Board> prevConfigs = new ArrayList<Board>();
		isSolved = root.isSolved();
		if (isSolved) {solution = root; isSolved = true; return;}
		Pair_CostBoard currPair = new Pair_CostBoard(root.getTotalManhattan(), root); 
		pq.add(currPair); //
		prevConfigs.add(root); //
		if (pq.size() > maxSize) maxSize = pq.size();
		while (!pq.isEmpty() || !isSolved)
		{
			if (System.currentTimeMillis() - initTime > 300000)
				throw new UnsupportedOperationException("Program is not set to run for more than 5 minutes (ie could not find an answer)");
			if (pq.size() > maxSize) maxSize = pq.size();
			currPair = pq.poll(); //
			pops++;
			Board currBoard = currPair.getValue(); //
			ArrayList<Character> possMoves = currBoard.getPossMoveDirs();
			ArrayList<Integer> possTargets = currBoard.getPossMoveTargets();
			for (char poss: possMoves)
			{
				Board child = new Board(currBoard.getTiles(), currBoard);
				child.move(poss);
				int target = possTargets.get(possMoves.indexOf(poss));
				if (prevConfigs.contains(child.strRep()))
					continue;
				if (child.isSolved())
				{
					solution = child;
					isSolved = true;
					return;
				}
				currBoard.addChild(child);
				prevConfigs.add(child);
				Pair_CostBoard childPair = new Pair_CostBoard(target + child.getTotalManhattan(), child); //
				pq.add(childPair);
			}
		}
		
	}
	
	/*
	 * Used during initial tests/debugging
	 */
	public static void main(String[] args) {/*
		System.out.println(isSolved);
		System.out.println(pops);
		
		isSolved = solution.isSolved();
		int counter = 0;
		System.out.println("------------");
		while (solution != null)
		{
			System.out.println("[" + Integer.toString(counter) + " / " + Integer.toString(solution.getDepth()) + "]\n" + solution.toString());
			counter = counter + 1;
			solution = solution.getParent();
		}
		System.out.println(isSolved);
		*/
	}

}
