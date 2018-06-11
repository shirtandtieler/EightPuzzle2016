package pkg;

import javax.swing.SwingWorker;

public class RunSolver extends SwingWorker<Board, String>{

	private String curDiff;
	private String curAlg;
	private Board solution;
	private boolean complete;
	
	/*
	 * RunSolver is used to provide multithreading to the GUI.
	 * This allows the elapsed time, loop count, and solving animation to take place while the results are searched for.
	 */
	public RunSolver(String curDiff, String curAlg)
	{
		this.curDiff = curDiff;
		this.curAlg = curAlg;
	}

	/*
	 * (non-Javadoc)
	 * The actions to do in the background
	 * @see javax.swing.SwingWorker#doInBackground()
	 */
	@Override
	protected Board doInBackground() throws Exception {
		solution = Solver.solve(curDiff, curAlg);
		complete = true;
		return solution;
	}
	
	/*
	 * (non-Javadoc)
	 * The actions to take once the calls to "doInBackground" are finished
	 * @see javax.swing.SwingWorker#done()
	 */
	protected void done()
	{
		try
        {
			if (complete)
			{
	            PuzzleUI.foundSolution = true;
	            PuzzleUI.solution = solution;
			} else PuzzleUI.foundSolution = false;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
	}

}
