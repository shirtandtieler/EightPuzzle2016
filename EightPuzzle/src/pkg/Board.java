package pkg;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;

public class Board {
	// variables for each instance of Board
	private ArrayList<Integer> bTiles; // the board's tiles as an arraylist of integer values
	private Board parent;
	private ArrayList<Board> children = new ArrayList<Board>();
	
	// final variables for moving any of the tiles
	// works by using an offset value to add the the tile's current index
	static final int MOVE_U = -3;
	static final int MOVE_D = 3;
	static final int MOVE_L = -1;
	static final int MOVE_R = 1;
	
	// used as a reference for which cardinal direction an indexed value is
	public ArrayList<String> dirs = new ArrayList<String>(Arrays.asList(new String[]{"nw","n","ne","w","c","e","sw","s","se"}));
	
	// #### Initializers
	public Board()
	{
	}
	
	/*
	 * @param strRep a one line string representation of tiles, gotten from this.strRep()
	 * @param parent the parent node of the new board (null for root)
	 */
	public Board(String strRep, Board parent) 
	{
		ArrayList<Integer> tmp = new ArrayList<Integer>();
		// iterate over the length of strRep
		for (int i = 0; i < strRep.length(); i++)
		{
			// set 's' to the current number
			String s = strRep.substring(i, i+1);
			int newTile = Integer.parseInt(s);
			tmp.add(newTile);
		}
		this.bTiles = tmp;
		this.parent = parent;
		
	}
	
	/*
	 * @param tiles a list of each of the tiles, listed in same order as you'd read a book in
	 * @param parent the parent node of the new board (null for root)
	 */
	public Board(ArrayList<Integer> tiles, Board parent) 
	{
		this.bTiles = tiles;
		this.parent = parent; 
	}
	
	// #### Gets
	/*
	 * Gets the depth for the current board by counting how many parents until null (indicating root)
	 * @return int the level a node/Board is at (0 = root)
	 */
	public int getDepth()
	{
		int pCounter = 0;
		Board prent = null;
		if (parent != null)
		{
			prent = this.parent;
			while (prent != null)
			{
				prent = prent.parent;
				pCounter++;
			}
		}
		return pCounter;
	}
	
	/*
	 * Basic public function to get the private variable
	 */
	public Board getParent()
	{
		return parent;
	}
	
	/*
	 * Basic public function to get the private variable
	 */
	public ArrayList<Board> getChildren()
	{
		return children;
	}
	
	/*
	 * Basic public function to get the private variable
	 */
	public ArrayList<Integer> getTiles() 
	{
		// Create separate copy so that modifications can be made to the tiles without directly altering 'this.bTiles'
		ArrayList<Integer> tilesCopy = new ArrayList<Integer>();
		for (int itile: bTiles)
			tilesCopy.add(itile);
		return tilesCopy;
	}
	
	/*
	 * @return String a string of the cardinal position of the empty tile
	 */
	public String getEmpty_cPos()
	{
		String pos = "";
		for (int i=0; i<=8; i++)
			if (bTiles.get(i) == 0)
				pos = dirs.get(i);
		return pos;
	}
	
	/*
	 * @return int the integer index of the empty tile
	 */
	public int getEmpty_index()
	{
		int index = -1;
		for (int i=0; i<=8; i++)
			if (this.bTiles.get(i) == 0)
				index = i;
		return index;
	}
	
	/*
	 * @return int an integer representing the amount of tiles not in their correct order
	 */
	public int getAmtMisplaced()
	{
		int mis = 0;
		// works by comparing each tile in bTiles to the value at the same index in goal_oneLiner
		String goal_oneLiner = "123804765";
		for (int t: bTiles)
		{ // compares the value of int t with the number (at the same index as t in bTiles) in goal_oneLiner
			String tileVal = Integer.toString(t);
			String goalVal = String.valueOf(goal_oneLiner.charAt(bTiles.indexOf(t)));
			if (!(tileVal.equals(goalVal)))
					mis++;
		}
		return mis; 
	}
	
	/*
	 * @return ArrayList<Character> a list of characters representing direction ('u' for up, 'd' for down, etc.)
	 */
	public ArrayList<Character> getPossMoveDirs()
	{
		ArrayList<Character> poss = new ArrayList<Character>();
		String empty = getEmpty_cPos();
		if (!empty.contains("n") || empty.equals("c"))
			poss.add('u');
		if (!empty.contains("s") || empty.equals("c"))
			poss.add('d');
		if (!empty.contains("w") || empty.equals("c"))
			poss.add('l');
		if (!empty.contains("e") || empty.equals("c"))
			poss.add('r');
		
		return poss;
	}
	
	/*
	 * @return int a list of integers that represent tile values that will be swapped if the empty tile moves in their direction
	 */
	public ArrayList<Integer> getPossMoveTargets()
	{
		ArrayList<Integer> possTargets = new ArrayList<Integer>();
		ArrayList<Character> possDirs = getPossMoveDirs();
		int emptyIndex = getEmpty_index();
		int offset;
		for (char dir: possDirs)
		{ // iterates over the results of getPossMoveDirs(), so everything lines up
			if (dir == 'u')
				offset = MOVE_U;
			else if (dir == 'd')
				offset = MOVE_D;
			else if (dir == 'l')
				offset = MOVE_L;
			else if (dir == 'r')
				offset = MOVE_R;
			else
				throw new IllegalArgumentException("Dir must be one of 'u','d','l',or 'r'.");
			int targtIndex = emptyIndex + offset;
			possTargets.add(bTiles.get(targtIndex));
		}
		return possTargets;
	}
	
	
	/* 
	 * @param tile either the index or value of the tile
	 * @param isIndex true if 'tile' is the index, false if 'tile' is the value 
	 * @return int the total cost to move the tile from its current position to its correct position
	 * */
	public int getManhattan(int tile, boolean isIndex)
	{
		int tileIndex, tileValue;
		if (isIndex)
		{
			tileIndex = tile;
			tileValue = bTiles.get(tileIndex);
		} else {
			tileValue = tile;
			tileIndex = bTiles.indexOf(tileValue);
		}
		int x = Math.floorMod(tileIndex, 3); 
		int y = Math.floorDiv(tileIndex, 3); 
		
		int xGoal, yGoal;
		if (tileValue == 0)
		{xGoal = 1; yGoal = 1;}
		else if (tileValue == 1)
		{xGoal = 0; yGoal = 0;}
		else if (tileValue == 2)
		{xGoal = 1; yGoal = 0;}
		else if (tileValue == 3)
		{xGoal = 2; yGoal = 0;}
		else if (tileValue == 4)
		{xGoal = 2; yGoal = 1;}
		else if (tileValue == 5)
		{xGoal = 2; yGoal = 2;}
		else if (tileValue == 6)
		{xGoal = 1; yGoal = 2;}
		else if (tileValue == 7)
		{xGoal = 0; yGoal = 2;}
		else if (tileValue == 8)
		{xGoal = 0; yGoal = 1;}
		else
		{throw new IllegalArgumentException("Val must be between 0-8");}
		
		return tileValue * (Math.abs(x-xGoal) + Math.abs(y-yGoal));
	}
	
	/*
	 * @return int the total Manhattan distance, calculated by adding up each result of getManhattan for each value
	 */
	public int getTotalManhattan()
	{
		int total = 0;
		for (int i = 1; i <= 8; i++)
			total += getManhattan(i, false);
		return total;
	}
	
	/*
	 * @return ArrayList<Board> get the history, starting from root to the current instance
	 */
	public ArrayList<Board> getHistory()
	{
		Stack<Board> history = new Stack<Board>();
		Board board = this;
		history.push(board);
		while (board.getParent() != null)
		{
			board = board.getParent();
			history.push(board);
		}
		ArrayList<Board> order = new ArrayList<Board>();
		while (!history.isEmpty())
			order.add(history.pop());
		return order;
	}
	
	/*
	 * @param boardAfter the board after the move taken in this
	 * @return int cost of move
	 */
	public int getMoveCost(Board boardAfter)
	{
		int cost = 0;
		for (int t=0; t<this.getTiles().size(); t++)
		{
			if (this.getTiles().get(t) != boardAfter.getTiles().get(t))
				cost += this.getTiles().get(t);
		}
		return cost;
	}
	
	// #### Sets
	
	/*
	 * @param parent a public set function to set the current board's parent
	 */
	public void setParent(Board parent)
	{
		this.parent = parent;
	}
	
	/*
	 * @param children a public set function to set the current board's children
	 */
	public void setChildren(ArrayList<Board> children)
	{
		this.children = children;
	}
	
	/*
	 * @param child a public set function to set the current board's children
	 */
	public void addChild(Board child)
	{
		this.children.add(child);
	}
	
	/*
	 * @param newTiles a public set function to set the tiles of the current board
	 */
	public void setTiles(ArrayList<Integer> newTiles)
	{
		this.bTiles = newTiles;
	}
	
	
	// #### Checks
	
	/*
	 * @return boolean true if the order of the current board equals the order of a set "correct" board, otherwise false
	 */
	public boolean isSolved()
	{
		@SuppressWarnings({ "unchecked", "rawtypes" })
		ArrayList<Integer> slvdOrder = new ArrayList(Arrays.asList(new Integer[]{1,2,3,8,0,4,7,6,5}));
		Board solved = new Board(slvdOrder, null);
		if (this.equals(solved))
			return true;
		return false;
	}
	
	
	// #### Actions
	
	/*
	 * For the current board, move the empty tile in a specified direction
	 * 
	 * @param dir a character of either 'u','d','l', or 'r' (up, down, left, or right) to move the empty tile
	 * @return int the value of the moved tile
	 */
	public int move(char dir)
	{
		int emptyIndex = bTiles.indexOf(0);
		int offset;
		if (dir == 'u')
			offset = MOVE_U;
		else if (dir == 'd')
			offset = MOVE_D;
		else if (dir == 'l')
			offset = MOVE_L;
		else if (dir == 'r')
			offset = MOVE_R;
		else
			throw new IllegalArgumentException("Dir must be one of 'u','d','l',or 'r'.");
		int targtIndex = emptyIndex + offset;
		if (targtIndex < 0 || targtIndex > 8)
			throw new IndexOutOfBoundsException("The tile cannot be moved in the direction of: " + String.valueOf(dir) + ", since it causes the index to be out of bounds :/");
		int targtValue = bTiles.get(targtIndex);
		bTiles.set(emptyIndex, targtValue);
		bTiles.set(targtIndex, 0);
		return targtValue;
	}
	
	/*
	 * Used for initial testing purposes
	 * 
	 * @param dir a character of either 'u','d','l', or 'r' (up, down, left, or right) to move the empty tile
	 * @return ArrayList<Integer> what the board will look like if it moves in the direction 'dir'
	 */
	public ArrayList<Integer> pseudomove(char dir)
	{
		ArrayList<Integer> tmp_copy_old = new ArrayList<Integer>();
		for (int itile: bTiles)
			tmp_copy_old.add(itile);
		int emptyIndex = bTiles.indexOf(0);
		int offset;
		if (dir == 'u')
			offset = MOVE_U;
		else if (dir == 'd')
			offset = MOVE_D;
		else if (dir == 'l')
			offset = MOVE_L;
		else if (dir == 'r')
			offset = MOVE_R;
		else
			throw new IllegalArgumentException("Dir must be one of 'u','d','l',or 'r'.");
		int targtIndex = emptyIndex + offset;
		int targtValue = tmp_copy_old.get(targtIndex);
		ArrayList<Integer> tmp_copy_new = new ArrayList<Integer>();
		for (int i = 0; i < tmp_copy_old.size(); i++)
			if (i == emptyIndex)
				tmp_copy_new.add(targtValue);
			else if (i == targtIndex)
				tmp_copy_new.add(0);
			else
				tmp_copy_new.add(tmp_copy_old.get(i));
		return tmp_copy_new;
	}
	
	
	// #### Representations
	
	/*
	 * Used for checking if a board is in the ArrayList of previous configurations
	 * 
	 * @return String a one line representation of the current board
	 */
	public String strRep()
	{
		String rep = "";
		for (int t: bTiles)
			rep += Integer.toString(t);
		return rep;
	}
	
	// #### Overrides
	
	/*
	 * (non-Javadoc)
	 * @return String the current board in a 3x3 pattern
	 * @see java.lang.Object#toString()
	 * 
	 */
	@Override
	public String toString()
	{
		String bOutput = ""; //"+-------------+\n";
		for (int i = 0; i < bTiles.size(); i++)
		{
			String curTile = String.valueOf(bTiles.get(i));
			int j = i+1;
			if (j % 3 != 0)
				bOutput += curTile + " ";
			else if (j != 8)
				bOutput += curTile + "\n";
			else
				bOutput += curTile;
		}
		//bOutput += "+-------------+";
		return bOutput;
		
	}
	
	/*
	 * (non-Javadoc)
	 * @param o the board object to compare 'this' to
	 * @return boolean true if both objects equal each other, otherwise false
	 * @see java.lang.Object#equals(java.lang.Object)
	 * 
	 */
	@Override
	public boolean equals(Object o)
	{
		// true if comparing to self
		if (o == this)
			return true;
		
		// fail if object is not of type Board
		if (!(o instanceof Board))
			return false;
		
		// simplify readability by assigning 'this' to its own variable
		Board b1 = this;
		// typecast o, in order to properly compare 
		Board b2 = (Board) o;
	
		ArrayList<Integer> b1Tiles = b1.getTiles();
		ArrayList<Integer> b2Tiles = b2.getTiles();
		
		for (int i=0; i<=8; i++)
		{
			// as soon as the value at index i in b1Tiles does not equal the same value at the same index in b2Tiles, they are not equal (return false)
			if (!(b1Tiles.get(i).equals(b2Tiles.get(i))))
				return false;
		}
		return true;
	}
}
