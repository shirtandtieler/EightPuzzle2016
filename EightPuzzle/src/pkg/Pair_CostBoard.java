package pkg;

public class Pair_CostBoard implements Comparable<Pair_CostBoard> {
	private int key; // the cost 
	private Board value; // the board
	
	public Pair_CostBoard(int key, Board board) {
		this.key = key;
		this.value = board;
	}
	
	/*
	 * @return int a public get function for private variable 'key'
	 */
	public int getKey()
	{
		return this.key;
	}
	
	/*
	 * @return int a public get function for private variable 'value'
	 */
	public Board getValue()
	{
		return this.value;
	}
	
	/*
	 * (non-Javadoc)
	 * To compare one pair with another - used for the priority queue
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Pair_CostBoard otherPair) {
		if (this.key > otherPair.key)
			return 1;
		else if (this.key < otherPair.key) 
			return -1;
		return 0;
	}
	
	/*
	 * (non-Javadoc)
	 * To print the object in a human-readable way which shows the key (cost) and it's assigned board - used for debugging
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "[" + String.valueOf(key) + "]\n" + value.toString() + "\n";
	}

}
