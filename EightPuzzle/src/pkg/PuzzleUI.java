package pkg;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

public class PuzzleUI {

	// As per original assignment instructions, create a fixed seed
	private final int RANDOM_SEED = 123454321;
	private Random random = new Random(RANDOM_SEED);

	public static Board solution;
	public static boolean foundSolution;
	public String solve = "Solving";
	public int dots = 0;
	
	// set the UI elements as class fields for simpler referencing across functions
	private static String curDiff;
	private static String curAlg;
	private JTable table_currConfig;
	private JTable table_solution;
	private DefaultTableModel solutionModel;
	private JTextField results_elapsed;
	private JTextField results_loops;
	private long startTime;
	private int historyIndex = 0;
	private String customPuzzle;
	private String historyLen_padded;
	private ArrayList<Board> history;
	private Timer buttonCheck = null;
	
	private JFrame frame;
	private AbstractButton button;
	private Board solutionStep;
	private DefaultTableModel board_model;
	private Dimension userScreenSize;
	private JButton btnN1000;
	private JButton btnN100;
	private JButton btnN10;
	private JButton btnNext;
	private JButton btnP1000;
	private JButton btnP100;
	private JButton btnP10;
	private JButton btnPrev;
	private JButton btnSolve;
	private JLabel results_lbl_elapsed;
	private JLabel results_lbl_loops;
	private JLabel results_stepCount;
	private JLabel results_cost;
	private JLabel lblMax;
	private JLabel status_lbl_alg;
	private JLabel status_lbl_configTitle;
	private JLabel status_lbl_currently;
	private JLabel status_lbl_diff;
	private JLabel status_lbl_optionsTitle;
	private JMenu menuAlgorithm;
	private JMenu menuDifficulty;
	private JMenuBar menuBar;
	private JPanel panel_results;
	private JPanel panel_status;
	private final ButtonGroup btnGroup_alg = new ButtonGroup();
	private JRadioButtonMenuItem menuAlg_A1;
	private JRadioButtonMenuItem menuAlg_A2;
	private JRadioButtonMenuItem menuAlg_BF;
	private JRadioButtonMenuItem menuAlg_BFS;
	private JRadioButtonMenuItem menuAlg_DFS;
	private JRadioButtonMenuItem menuAlg_UC;
	private final ButtonGroup btnGroup_diff = new ButtonGroup();
	private JRadioButtonMenuItem menuDiff_Easy;
	private JRadioButtonMenuItem menuDiff_Medium;
	private JRadioButtonMenuItem menuDiff_Hard;
	private JRadioButtonMenuItem menuDiff_Random;
	private JRadioButtonMenuItem menuDiff_Custom;
	private JTextField status_alg;
	private JTextField status_currently;
	private JTextField status_diff;
	private Object val;
	private RunSolver runner;
	private String errorMsg;
	private String historyIndex_padded;
	private Timer delay;
	private int indx;
	private int minutes;
	private int seconds;
	private int tIndex;
	private int uss_hyt;
	private int uss_wid;
	
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		// set look and feel to system style
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		} catch (InstantiationException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		} catch (UnsupportedLookAndFeelException e1) {
			e1.printStackTrace();
		}
		
		// create window and run
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					PuzzleUI window = new PuzzleUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create/Initialize the various parts of the application.
	 */
	public PuzzleUI() {
		initialize();
		init_menubar();
		init_status();
		init_results();
	}
	
	
	/**
	 * Initialize the contents of the frame (aka the GUI window).
	 */
	private void initialize() {
		frame = new JFrame();
		userScreenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		uss_wid = userScreenSize.width;
		uss_hyt = userScreenSize.height;
		frame.setBounds((uss_wid/3)-(595/2),(uss_hyt/3)-(320/2),625,332);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.setTitle("Eight Puzzle Solver");
		
		// a timer which runs every 100ms, updating the skip buttons for when they should/n't be enabled or disabled
		buttonCheck = new Timer(100, new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				
				// conditionals to turn the previous buttons on
				if (historyIndex < history.size()-1)
				{
					btnPrev.setEnabled(true);
					btnP10.setEnabled(true);
					btnP100.setEnabled(true);
					btnP1000.setEnabled(true);
				}
					
				// conditionals to turn the previous buttons off
				if (historyIndex == 0) 
					btnPrev.setEnabled(false);
				if (historyIndex < 10)
					btnP10.setEnabled(false);
				if (historyIndex < 100)
					btnP100.setEnabled(false);
				if (historyIndex < 1000)
					btnP1000.setEnabled(false);
				
				//conditionals to turn the next buttons on
				if (historyIndex >= 0) 
				{
					btnNext.setEnabled(true);
					btnN10.setEnabled(true);
					btnN100.setEnabled(true);
					btnN1000.setEnabled(true);
				}
				
				// conditionals to turn the next buttons off
				if (historyIndex == history.size()-1) 
					btnNext.setEnabled(false);
				if (historyIndex >= history.size()-10)
					btnN10.setEnabled(false);
				if (historyIndex >= history.size()-100)
					btnN100.setEnabled(false);
				if (historyIndex >= history.size()-1000)
					btnN1000.setEnabled(false);
			}
		});

		frame.setResizable(false);
	}
	
	/**
	 * Setup the information for the menubar
	 */
	private void init_menubar()
	{
		menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		// initialize the table model for the board
		board_model = new DefaultTableModel(
				new Object[][] {
					{new Integer(1), new Integer(2), new Integer(3)},
					{new Integer(8), new Integer(0), new Integer(4)},
					{new Integer(7), new Integer(6), new Integer(5)},
				},
				new String[] {
					"1", "2", "3"
				}
			) {
				private static final long serialVersionUID = 1932528177059281771L;
				@SuppressWarnings("rawtypes")
				Class[] columnTypes = new Class[] {
					Integer.class, Integer.class, Integer.class
				};
				@SuppressWarnings({ "unchecked", "rawtypes" })
				public Class getColumnClass(int columnIndex) {
					return columnTypes[columnIndex];
				}
				boolean[] columnEditables = new boolean[] {
					false, false, false
				};
				public boolean isCellEditable(int row, int column) {
					return columnEditables[column];
				}
		};
		
		
		// create a new menu list for the difficulty settings
		menuDifficulty = new JMenu("Difficulty");
		menuBar.add(menuDifficulty);
		
		menuDiff_Easy = new JRadioButtonMenuItem("Easy");
		menuDiff_Easy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, InputEvent.CTRL_MASK));
		btnGroup_diff.add(menuDiff_Easy);
		menuDifficulty.add(menuDiff_Easy);
		// listener for 'easy' menu option
		// update the current board in the status pane with the default 'easy' board
		menuDiff_Easy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonCheck.stop();
				curDiff = "Easy";
				status_diff.setText("Easy");
				for (int x=0; x<3; x++)
					for (int y=0; y<3; y++)
					{
						indx = 3*y+x; // formula from (x,y) to the index
						val = Integer.valueOf(Solver.easy.substring(indx, indx+1));
						if (val.equals(0))
							val = " ";
						board_model.setValueAt(val, y, x);
					}
				table_currConfig.setModel(board_model);
				clearSolution(new JButton[]{btnNext, btnPrev, btnN10, btnN100, btnN1000, btnP10, btnP100, btnP1000});
			}
		});
		
		
		menuDiff_Medium = new JRadioButtonMenuItem("Medium");
		menuDiff_Medium.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2, InputEvent.CTRL_MASK));
		btnGroup_diff.add(menuDiff_Medium);
		menuDifficulty.add(menuDiff_Medium);
		// listener for 'medium' menu option
		// update the current board in the status pane with the default 'medium' board
		menuDiff_Medium.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonCheck.stop();
				curDiff = "Medium";
				status_diff.setText("Medium");
				for (int x=0; x<3; x++)
					for (int y=0; y<3; y++)
					{
						indx = 3*y+x;
						val = Integer.valueOf(Solver.medm.substring(indx, indx+1));
						if (val.equals(0))
							val = " ";
						board_model.setValueAt(val, y, x);
					}
				table_currConfig.setModel(board_model);
				clearSolution(new JButton[]{btnNext, btnPrev, btnN10, btnN100, btnN1000, btnP10, btnP100, btnP1000});
			}
		});
		
		menuDiff_Hard = new JRadioButtonMenuItem("Hard");
		menuDiff_Hard.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_3, InputEvent.CTRL_MASK));
		btnGroup_diff.add(menuDiff_Hard);
		menuDifficulty.add(menuDiff_Hard);
		// listener for 'hard' menu option
		// update the current board in the status pane with the default 'hard' board
		menuDiff_Hard.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonCheck.stop();
				curDiff = "Hard";
				status_diff.setText("Hard");
				for (int x=0; x<3; x++)
					for (int y=0; y<3; y++)
					{
						indx = 3*y+x;
						val = Integer.valueOf(Solver.hard.substring(indx, indx+1));
						if (val.equals(0))
							val = " ";
						board_model.setValueAt(val, y, x);
					}
				table_currConfig.setModel(board_model);
				clearSolution(new JButton[]{btnNext, btnPrev, btnN10, btnN100, btnN1000, btnP10, btnP100, btnP1000});
			}
		});
		

		menuDiff_Random = new JRadioButtonMenuItem("Random");
		menuDiff_Random.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_4, InputEvent.CTRL_MASK));
		// listener for the 'random' menu option
		// update the current board in the status pane with a random board
		menuDiff_Random.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				buttonCheck.stop();
				curDiff = "Random";
				status_diff.setText("Random");
				// Randomly place all 9 numbers
				List<String> nums_left = new ArrayList<>(Arrays.asList("0", "1", "2", "3", "4", "5", "6", "7", "8"));
				String rand_order = "";
				int curr_rand_output = -1;
				while (nums_left.size() > 1) {
					curr_rand_output = random.nextInt(nums_left.size()-1);
					rand_order += nums_left.get(curr_rand_output);
					nums_left.remove(curr_rand_output);
				}
				rand_order += nums_left.get(0);
				Solver.rand = rand_order;
				for (int x=0; x<3; x++)
					for (int y=0; y<3; y++)
					{
						indx = 3*y+x;
						val = Integer.valueOf(rand_order.substring(indx, indx+1));
						if (val.equals(0))
							val = " ";
						board_model.setValueAt(val, y, x);
					}
				clearSolution(new JButton[]{btnNext, btnPrev, btnN10, btnN100, btnN1000, btnP10, btnP100, btnP1000});
				table_currConfig.setModel(board_model);
				
			}

		});
		btnGroup_diff.add(menuDiff_Random);
		menuDifficulty.add(menuDiff_Random);


		menuDiff_Custom = new JRadioButtonMenuItem("Custom...");
		menuDiff_Custom.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_5, InputEvent.CTRL_MASK));
		// listener for the 'custom' menu option
		// update the current board with the user-inputted board
		menuDiff_Custom.addActionListener(new ActionListener() {
			
			/*
			 * checks if the user input is a valid puzzle, returning true if it is, otherwise false
			 */
			public boolean isValidPuzzle(String puzzle)
			{
				ArrayList<String> target = new ArrayList<String>(Arrays.asList(new String[]{"0","1","2","3","4","5","6","7","8"}));
				if (puzzle == null || puzzle.length() == 0)
					return true;
				else if (puzzle.length() != 9)
					return false;
				for (String num: target)
				{
					if (!puzzle.contains(num))
						return false;
				}
				return true;
			}
			
			/*
			 * (non-Javadoc)
			 * Show an input box which the user can input their own problem
			 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
			 */
			public void actionPerformed(ActionEvent e) {
				buttonCheck.stop();
				curDiff = "Custom";
				status_diff.setText("Custom");
				customPuzzle = JOptionPane.showInputDialog("Please enter your custom puzzle below (one line, no spaces)");
				// while the user-inputed puzzle is not valid, ask again (or quit asking if the user pushes cancel)
				while (!isValidPuzzle(customPuzzle))
					customPuzzle = JOptionPane.showInputDialog("That is not a valid puzzle configuration. Please try again (or press cancel to not use a custom puzzle).");
				if (customPuzzle == null)
					customPuzzle = "123804765";
				Solver.cstm = customPuzzle;
				for (int x=0; x<3; x++)
					for (int y=0; y<3; y++)
					{
						indx = 3*y+x;
						val = Integer.valueOf(Solver.cstm.substring(indx, indx+1));
						if (val.equals(0))
							val = " ";
						board_model.setValueAt(val, y, x);
					}
				clearSolution(new JButton[]{btnNext, btnPrev, btnN10, btnN100, btnN1000, btnP10, btnP100, btnP1000});
				table_currConfig.setModel(board_model);
			}
		});
		btnGroup_diff.add(menuDiff_Custom);
		menuDifficulty.add(menuDiff_Custom);
		
		
		// creating a new menu list for the algorithms
		menuAlgorithm = new JMenu("Algorithm");
		menuBar.add(menuAlgorithm);
		
		menuAlg_BFS = new JRadioButtonMenuItem("Breadth First");
		menuAlg_BFS.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, InputEvent.CTRL_MASK | InputEvent.ALT_MASK));
		// listener for the 'BFS' menu button
		menuAlg_BFS.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				buttonCheck.stop();
				curAlg = "BFS";
				status_alg.setText("Breadth First");
				clearSolution(new JButton[]{btnNext, btnPrev, btnN10, btnN100, btnN1000, btnP10, btnP100, btnP1000});
			}
		});
		btnGroup_alg.add(menuAlg_BFS);
		menuAlgorithm.add(menuAlg_BFS);
		
		menuAlg_DFS = new JRadioButtonMenuItem("Depth First");
		menuAlg_DFS.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2, InputEvent.CTRL_MASK | InputEvent.ALT_MASK));
		// listener for the 'DFS' menu button
		menuAlg_DFS.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				buttonCheck.stop();
				curAlg = "DFS";
				status_alg.setText("Depth First");
				clearSolution(new JButton[]{btnNext, btnPrev, btnN10, btnN100, btnN1000, btnP10, btnP100, btnP1000});
			}
		});
		btnGroup_alg.add(menuAlg_DFS);
		menuAlgorithm.add(menuAlg_DFS);
		
		menuAlg_BF = new JRadioButtonMenuItem("Best First");
		menuAlg_BF.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_3, InputEvent.CTRL_MASK | InputEvent.ALT_MASK));
		// listener for the Best-First menu option
		menuAlg_BF.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				buttonCheck.stop();
				curAlg = "BF";
				status_alg.setText("Best First");
				clearSolution(new JButton[]{btnNext, btnPrev, btnN10, btnN100, btnN1000, btnP10, btnP100, btnP1000});
			}
		});
		btnGroup_alg.add(menuAlg_BF);
		menuAlgorithm.add(menuAlg_BF);
		
		menuAlg_UC = new JRadioButtonMenuItem("Uniform Cost");
		menuAlg_UC.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_4, InputEvent.CTRL_MASK | InputEvent.ALT_MASK));
		// listener for the uniform cost option
		menuAlg_UC.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				buttonCheck.stop();
				curAlg = "UC";
				status_alg.setText("Uniform Cost");
				clearSolution(new JButton[]{btnNext, btnPrev, btnN10, btnN100, btnN1000, btnP10, btnP100, btnP1000});
			}
		});
		btnGroup_alg.add(menuAlg_UC);
		menuAlgorithm.add(menuAlg_UC);
		
		menuAlg_A1 = new JRadioButtonMenuItem("A* 1 (Tiles Misplaced)");
		menuAlg_A1.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_5, InputEvent.CTRL_MASK | InputEvent.ALT_MASK));
		// listener for the A* option, w/ heuristic targetting the number of tiles misplaced
		menuAlg_A1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				buttonCheck.stop();
				curAlg = "A1";
				status_alg.setText("A* 1");
				clearSolution(new JButton[]{btnNext, btnPrev, btnN10, btnN100, btnN1000, btnP10, btnP100, btnP1000});
			}
		});
		btnGroup_alg.add(menuAlg_A1);
		menuAlgorithm.add(menuAlg_A1);
		
		menuAlg_A2 = new JRadioButtonMenuItem("A* 2 (Manhattan)");
		menuAlg_A2.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_6, InputEvent.CTRL_MASK | InputEvent.ALT_MASK));
		// listener for the A* option, w/ heuristic targetting the manhattan distance to the destination
		menuAlg_A2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonCheck.stop();
				curAlg = "A2";
				status_alg.setText("A* 2");
				clearSolution(new JButton[]{btnNext, btnPrev, btnN10, btnN100, btnN1000, btnP10, btnP100, btnP1000});
			}
		});
		btnGroup_alg.add(menuAlg_A2);
		menuAlgorithm.add(menuAlg_A2);
	}
	
	/*
	 * initialize all the objects within the status pane
	 */
	private void init_status()
	{
		panel_status = new JPanel();
		panel_status.setBounds(10, 12, 195, 259);
		panel_status.setBorder(new TitledBorder(null, "Status", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		frame.getContentPane().add(panel_status);
		panel_status.setLayout(null);

		
		status_lbl_currently = new JLabel("Currently:");
		status_lbl_currently.setBounds(10, 24, 59, 14);
		panel_status.add(status_lbl_currently);
		
		status_currently = new JTextField("On Standby");
		status_currently.setBounds(79, 21, 100, 20);
		status_currently.setEditable(false);
		panel_status.add(status_currently);
		
		status_lbl_optionsTitle = new JLabel("Chosen Options");
		status_lbl_optionsTitle.setBounds(52, 55, 84, 14);
		status_lbl_optionsTitle.setFont(new Font("Tahoma", Font.ITALIC, 11));
		panel_status.add(status_lbl_optionsTitle);
		
		status_lbl_diff = new JLabel("Difficulty:");
		status_lbl_diff.setBounds(10, 80, 59, 14);
		panel_status.add(status_lbl_diff);
		
		status_diff = new JTextField("None", 10);
		status_diff.setBounds(79, 77, 100, 20);
		status_diff.setEditable(false);
		panel_status.add(status_diff);
		
		status_lbl_alg = new JLabel("Algorithm:");
		status_lbl_alg.setBounds(10, 105, 59, 14);
		panel_status.add(status_lbl_alg);
		
		status_alg = new JTextField("None");
		status_alg.setBounds(79, 102, 100, 20);
		status_alg.setEditable(false);
		panel_status.add(status_alg);
		
		status_lbl_configTitle = new JLabel("Current Configuration");
		status_lbl_configTitle.setBounds(41, 133, 109, 14);
		status_lbl_configTitle.setFont(new Font("Tahoma", Font.ITALIC, 11));
		panel_status.add(status_lbl_configTitle);
		
		table_currConfig = new JTable();
		table_currConfig.setBounds(49, 158, 90, 90);
		table_currConfig.setEnabled(false);
		table_currConfig.setRowSelectionAllowed(false);
		table_currConfig.setFont(new Font("Tahoma", Font.PLAIN, 22));
		table_currConfig.setModel(new DefaultTableModel(
			new Object[][] {
				{null, null, null},
				{null, null, null},
				{null, null, null},
			},
			new String[] {
				"1", "2", "3"
			}
		) {
			/**
			 * 
			 */
			private static final long serialVersionUID = -4359580772027728320L;
			@SuppressWarnings("rawtypes")
			Class[] columnTypes = new Class[] {
				String.class, String.class, String.class
			};
			@SuppressWarnings({ "unchecked", "rawtypes" })
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
		});
		// set column and row widths
		for (int c = 0; c < 3; c++) {
			table_currConfig.getColumnModel().getColumn(c).setPreferredWidth(30);
			table_currConfig.getColumnModel().getColumn(c).setMinWidth(30);
		}
		table_currConfig.setRowHeight(30);
		panel_status.add(table_currConfig);
	}
	
	/*
	 * initialize all the content within the result pane
	 */
	private void init_results()
	{
		panel_results = new JPanel();
		panel_results.setBounds(234, 12, 375, 259);
		panel_results.setBorder(new TitledBorder(null, "Results", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		frame.getContentPane().add(panel_results);
		panel_results.setLayout(null);
		
		btnSolve = new JButton("Solve");
		btnSolve.setBounds(111, 11, 150, 42);
		panel_results.add(btnSolve);
		
		table_solution = new JTable();
		table_solution.setBackground(SystemColor.menu);
		table_solution.setBounds(111, 78, 150, 150);
		table_solution.setFont(new Font("Tahoma", Font.PLAIN, 26));
		solutionModel = new DefaultTableModel(
			new Object[][] {
				{null, null, null},
				{null, null, null},
				{null, null, null},
			},
			new String[] {
				"1", "2", "3"
			}
		) {
			/**
			 * 
			 */
			private static final long serialVersionUID = -7558906694314479394L;
			@SuppressWarnings("rawtypes")
			Class[] columnTypes = new Class[] {
				Integer.class, Integer.class, Integer.class
			};
			@SuppressWarnings({ "unchecked", "rawtypes" })
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
		};
		table_solution.setModel(solutionModel);
		
		results_stepCount = new JLabel("Step: 00 of 00");
		results_stepCount.setHorizontalAlignment(JLabel.CENTER);
		results_stepCount.setEnabled(false);
		results_stepCount.setBounds(111, 234, 150, 14);
		panel_results.add(results_stepCount);
		
		results_cost = new JLabel("Cost: 0");
		results_cost.setHorizontalAlignment(SwingConstants.CENTER);
		results_stepCount.setHorizontalAlignment(JLabel.CENTER);
		results_cost.setEnabled(false);
		results_cost.setBounds(111, 58, 76, 14);
		panel_results.add(results_cost);
		
		lblMax = new JLabel("Max: 0");
		lblMax.setEnabled(false);
		lblMax.setHorizontalAlignment(SwingConstants.CENTER);
		lblMax.setBounds(191, 58, 70, 14);
		panel_results.add(lblMax);
		
		btnPrev = new JButton("Prev");
		btnPrev.setEnabled(false);
		btnPrev.setBounds(10, 157, 91, 42);
		panel_results.add(btnPrev);
		table_solution.setRowHeight(50);
		panel_results.add(table_solution);
		
		btnNext = new JButton("Next");
		
		btnNext.setEnabled(false);
		btnNext.setBounds(274, 157, 91, 42);
		panel_results.add(btnNext);
		
		
		results_lbl_elapsed = new JLabel("<HTML><U>Elapsed time:</U></HTML>");
		results_lbl_elapsed.setEnabled(false);
		results_lbl_elapsed.setBounds(10, 73, 91, 29);
		panel_results.add(results_lbl_elapsed);
		
		results_elapsed = new JTextField();
		results_elapsed.setEditable(false);
		results_elapsed.setEnabled(false);
		results_elapsed.setBounds(10, 98, 91, 20);
		results_elapsed.setHorizontalAlignment(JLabel.CENTER);
		panel_results.add(results_elapsed);
		results_elapsed.setColumns(10);
		
		results_loops = new JTextField();
		results_loops.setEnabled(false);
		results_loops.setEditable(false);
		results_loops.setBounds(274, 98, 91, 20);
		results_loops.setHorizontalAlignment(JLabel.CENTER);
		panel_results.add(results_loops);
		results_loops.setColumns(10);
		
		results_lbl_loops = new JLabel("<HTML><U>Queries:</U></HTML>");
		results_lbl_loops.setEnabled(false);
		results_lbl_loops.setBounds(271, 80, 46, 14);
		panel_results.add(results_lbl_loops);
		
		/*
		 * adds a listener to accomplish multiple tasks including:
		 * 1. make sure user has selected a difficulty and algorithm setting
		 * 2. create a timer which keeps checking if the solution is found, while also updating the elapsed time and iteration fields, and the solving animation.
		 */
		btnSolve.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				errorMsg = "";
				if (curDiff == null && curAlg == null)
					errorMsg += "Before continuing, please select your desired difficulty and algorithm method.";
				else if (curDiff == null)
					errorMsg += "Before continuing, please select your desired difficulty setting.";
				else if (curAlg == null)
					errorMsg += "Before continuing, please select your desired algorithm method.";
				if (errorMsg != "")
				{
					JOptionPane.showMessageDialog(frame, errorMsg, "Settings issue", JOptionPane.WARNING_MESSAGE);
					return;
				}
				Solver.pops = 0;
				status_currently.setText(solve);
				btnSolve.setEnabled(false);
				results_lbl_elapsed.setEnabled(true);
				results_elapsed.setEnabled(true);
				results_lbl_loops.setEnabled(true);
				results_loops.setEnabled(true);
				delay = new Timer(200, new ActionListener() {
					public void actionPerformed(ActionEvent ae) {
						if (foundSolution)
						{
							((Timer)ae.getSource()).stop();
							buttonCheck.start();
							table_solution.setBackground(Color.white);
							for (int x=0; x<3; x++)
								for (int y=0; y<3; y++)
									solutionModel.setValueAt(table_currConfig.getValueAt(y, x), y, x);
							table_solution.setModel(solutionModel);
							history = solution.getHistory();
							status_currently.setText("Solved!");
							btnNext.setEnabled(true);
							btnSolve.setEnabled(true);
							btnSolve.setText("Solve");
							results_stepCount.setEnabled(true);
							results_cost.setEnabled(true);
							lblMax.setEnabled(true);
							historyLen_padded = String.valueOf(history.size());
							if (historyLen_padded.length() == 1) historyLen_padded = "0" + historyLen_padded;
							results_stepCount.setText("Step 01 of " + historyLen_padded);
							int totalCost = 0;
							for (int b=0; b<history.size()-1; b++)
							{
								Board before = history.get(b);
								Board after  = history.get(b+1);
								totalCost += before.getMoveCost(after);
							}
							results_cost.setText("Cost: " + String.valueOf(totalCost));
							lblMax.setText("Max: " + Solver.maxSize);
							for (Enumeration<AbstractButton> buttons = btnGroup_alg.getElements(); buttons.hasMoreElements();) {
								button = buttons.nextElement();
								button.setEnabled(true);
							}
							for (Enumeration<AbstractButton> buttons = btnGroup_diff.getElements(); buttons.hasMoreElements();) {
								button = buttons.nextElement();
								button.setEnabled(true);
							}
						} else {
							if (dots > 3) {
								dots = 0;
								solve = "Solving";
							} else {
							solve += ".";
							}
							dots++;
							status_currently.setText(solve);
						}
						results_loops.setText(String.valueOf(Solver.pops));
						seconds = (int) ((System.currentTimeMillis() - startTime)/1000);
						minutes = Math.floorDiv(seconds, 60);
						seconds = seconds-(minutes*60);
						if (seconds == 0 && minutes == 0)
							results_elapsed.setText(String.valueOf(System.currentTimeMillis()-startTime) + "ms");
						else
							results_elapsed.setText(String.valueOf(minutes) + "m " + String.valueOf(seconds) + "s");
					}
				});
				runner = new RunSolver(curDiff, curAlg);
				delay.setRepeats(true);
				delay.start();
				startTime = System.currentTimeMillis();
				runner.execute();
				for (Enumeration<AbstractButton> buttons = btnGroup_alg.getElements(); buttons.hasMoreElements();) {
					button = buttons.nextElement();
					button.setEnabled(false);
				}
				for (Enumeration<AbstractButton> buttons = btnGroup_diff.getElements(); buttons.hasMoreElements();) {
					button = buttons.nextElement();
					button.setEnabled(false);
				}
			}
		});
		
		/*
		 * btnNext and btnPrev allow the user to step through how the solution was found
		 */
		btnNext.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				historyIndex++;
				historyIndex_padded = String.valueOf(historyIndex+1);
				if (historyIndex_padded.length() == 1) historyIndex_padded = "0" + historyIndex_padded;
				results_stepCount.setText("Step " + historyIndex_padded + " of " + historyLen_padded);
				solutionStep = history.get(historyIndex);
				for (Object tile: solutionStep.getTiles())
				{
					tIndex = solutionStep.getTiles().indexOf(tile);
					if (tile.equals(0))
						tile = " ";
					solutionModel.setValueAt(tile, Math.floorDiv(tIndex, 3), Math.floorMod(tIndex, 3));
				}
			}
		});
		
		btnPrev.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				historyIndex--;
				historyIndex_padded = String.valueOf(historyIndex+1);
				if (historyIndex_padded.length() == 1) historyIndex_padded = "0" + historyIndex_padded;
				results_stepCount.setText("Step " + historyIndex_padded + " of " + historyLen_padded);
				solutionStep = history.get(historyIndex);
				for (Object tile: solutionStep.getTiles())
				{
					tIndex = solutionStep.getTiles().indexOf(tile);
					if (tile.equals(0))
						tile = " ";
					solutionModel.setValueAt(tile, Math.floorDiv(tIndex, 3), Math.floorMod(tIndex, 3));
				}
			}
		});
		
		/*
		 * The 6 buttons below (which follow the format "btn[N/P][10/100/1000]" - N for Next, P for previous) control how many steps forward and backwards the user can skip ahead
		 */
		btnN10 = new JButton("+10");
		btnN10.setEnabled(false);
		btnN10.setFont(new Font("Tahoma", Font.PLAIN, 10));
		btnN10.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				historyIndex += 10;
				historyIndex_padded = String.valueOf(historyIndex+1);
				if (historyIndex_padded.length() == 1) historyIndex_padded = "0" + historyIndex_padded;
				results_stepCount.setText("Step " + historyIndex_padded + " of " + historyLen_padded);
				solutionStep = history.get(historyIndex);
				for (Object tile: solutionStep.getTiles())
				{
					tIndex = solutionStep.getTiles().indexOf(tile);
					if (tile.equals(0))
						tile = " ";
					solutionModel.setValueAt(tile, Math.floorDiv(tIndex, 3), Math.floorMod(tIndex, 3));
				}
				
			}
		});
		btnN10.setMargin(new Insets(0,0,0,0));
		btnN10.setBounds(274, 200, 29, 29);
		panel_results.add(btnN10);
		
		btnN100 = new JButton("+100");
		btnN100.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				historyIndex += 100;
				historyIndex_padded = String.valueOf(historyIndex+1);
				if (historyIndex_padded.length() == 1) historyIndex_padded = "0" + historyIndex_padded;
				results_stepCount.setText("Step " + historyIndex_padded + " of " + historyLen_padded);
				solutionStep = history.get(historyIndex);
				for (Object tile: solutionStep.getTiles())
				{
					tIndex = solutionStep.getTiles().indexOf(tile);
					if (tile.equals(0))
						tile = " ";
					solutionModel.setValueAt(tile, Math.floorDiv(tIndex, 3), Math.floorMod(tIndex, 3));
				}
			}
		});
		btnN100.setEnabled(false);
		btnN100.setMargin(new Insets(0, 0, 0, 0));
		btnN100.setFont(new Font("Tahoma", Font.PLAIN, 9));
		btnN100.setBounds(305, 200, 29, 29);
		panel_results.add(btnN100);
		
		btnN1000 = new JButton("+1000");
		btnN1000.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				historyIndex += 1000;
				historyIndex_padded = String.valueOf(historyIndex+1);
				if (historyIndex_padded.length() == 1) historyIndex_padded = "0" + historyIndex_padded;
				results_stepCount.setText("Step " + historyIndex_padded + " of " + historyLen_padded);
				solutionStep = history.get(historyIndex);
				for (Object tile: solutionStep.getTiles())
				{
					tIndex = solutionStep.getTiles().indexOf(tile);
					if (tile.equals(0))
						tile = " ";
					solutionModel.setValueAt(tile, Math.floorDiv(tIndex, 3), Math.floorMod(tIndex, 3));
				}
			}
		});
		btnN1000.setEnabled(false);
		btnN1000.setMargin(new Insets(0, 0, 0, 0));
		btnN1000.setFont(new Font("Tahoma", Font.PLAIN, 8));
		btnN1000.setBounds(336, 200, 29, 29);
		panel_results.add(btnN1000);
		
		btnP1000 = new JButton("-1000");
		btnP1000.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				historyIndex -= 1000;
				historyIndex_padded = String.valueOf(historyIndex+1);
				if (historyIndex_padded.length() == 1) historyIndex_padded = "0" + historyIndex_padded;
				results_stepCount.setText("Step " + historyIndex_padded + " of " + historyLen_padded);
				solutionStep = history.get(historyIndex);
				for (Object tile: solutionStep.getTiles())
				{
					tIndex = solutionStep.getTiles().indexOf(tile);
					if (tile.equals(0))
						tile = " ";
					solutionModel.setValueAt(tile, Math.floorDiv(tIndex, 3), Math.floorMod(tIndex, 3));
				}
			}
		});
		btnP1000.setEnabled(false);
		btnP1000.setMargin(new Insets(0, 0, 0, 0));
		btnP1000.setFont(new Font("Tahoma", Font.PLAIN, 8));
		btnP1000.setBounds(72, 200, 29, 29);
		panel_results.add(btnP1000);
		
		btnP100 = new JButton("-100");
		btnP100.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				historyIndex -= 100;
				historyIndex_padded = String.valueOf(historyIndex+1);
				if (historyIndex_padded.length() == 1) historyIndex_padded = "0" + historyIndex_padded;
				results_stepCount.setText("Step " + historyIndex_padded + " of " + historyLen_padded);
				solutionStep = history.get(historyIndex);
				for (Object tile: solutionStep.getTiles())
				{
					tIndex = solutionStep.getTiles().indexOf(tile);
					if (tile.equals(0))
						tile = " ";
					solutionModel.setValueAt(tile, Math.floorDiv(tIndex, 3), Math.floorMod(tIndex, 3));
				}
			}
		});
		btnP100.setEnabled(false);
		btnP100.setMargin(new Insets(0, 0, 0, 0));
		btnP100.setFont(new Font("Tahoma", Font.PLAIN, 9));
		btnP100.setBounds(41, 200, 29, 29);
		panel_results.add(btnP100);
		
		btnP10 = new JButton("-10");
		btnP10.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				historyIndex -= 10;
				historyIndex_padded = String.valueOf(historyIndex+1);
				if (historyIndex_padded.length() == 1) historyIndex_padded = "0" + historyIndex_padded;
				results_stepCount.setText("Step " + historyIndex_padded + " of " + historyLen_padded);
				solutionStep = history.get(historyIndex);
				for (Object tile: solutionStep.getTiles())
				{
					tIndex = solutionStep.getTiles().indexOf(tile);
					if (tile.equals(0))
						tile = " ";
					solutionModel.setValueAt(tile, Math.floorDiv(tIndex, 3), Math.floorMod(tIndex, 3));
				}
			}
		});
		btnP10.setEnabled(false);
		btnP10.setMargin(new Insets(0, 0, 0, 0));
		btnP10.setFont(new Font("Tahoma", Font.PLAIN, 10));
		btnP10.setBounds(10, 200, 29, 29);
		panel_results.add(btnP10);
	}
	
	/*
	 * clears the solution and the results from the Results pane
	 * triggers if a user changes any of the settings
	 */
	private void clearSolution(JButton[] toDisable)
	{
		status_currently.setText("On Standby");
		table_solution.setBackground(new Color(240,240,240));
		for (int x=0; x<3; x++)
			for (int y=0; y<3; y++)
				table_solution.setValueAt(" ", y, x);
		for (int i=0; i<toDisable.length; i++)
			toDisable[i].setEnabled(false);
		solution = null;
		foundSolution = false;
		historyIndex = 0;
		results_elapsed.setText(null);
		results_loops.setText(null);
		results_stepCount.setText("Step 00 of 00");
		results_stepCount.setEnabled(false);
		results_cost.setText("Cost: 0");
		results_cost.setEnabled(false);
		lblMax.setText("Max: 0");
		lblMax.setEnabled(false);
	}
}
