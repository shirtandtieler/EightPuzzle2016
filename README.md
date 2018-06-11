# EightPuzzle2016
My implementation of 8-Puzzle, written in 2016 for a university course.

A bit of warning: since this was written in my first year of knowing how to code, the UI is pretty poorly written. My intention in posting is just to display my ability and knowledge of various algorithms. My competancy of writing properly styled code can be seen in my more recent projects :)

For the unaware, 8-Puzzle is a type of [sliding puzzle](https://en.wikipedia.org/wiki/Sliding_puzzle) where a 3x3 board with 9 spaces starts with 8 numbered tiles. Given some starting configuration of the 8 tiles, the goal is to slide the tiles around to achieve a desired end result.

**Assignment Information**

My professor's requirements were that the winning board would start with the '1' tile in the top left, continuing clockwise for the rest of the tiles (up to the '8' tile), like so:
```
123
8 4
765
```

The assignment was to create a solver that could show the user the steps to reach the goal. The user would be allowed to choose from one of three preset starting boards or input one of their own. They would then select which of six algorithms the program would use to find a solution. The choices included:
- Breadth First Search
- Depth First Search
- Best First
- Uniform Cost
- A* w/heuristic of the number of misplaced tiles
- A* w/heuristic of the sum of each tile's Manhattan distance to their desired end position

If a solution was not found within 5 minutes, the program would tell the user that no solution could be found in a reasonable amount of time.

The professor had stated that a simple text-based program (i.e. interacting just from the command line) was all that was needed. However with my interest in making user-interfaces, I wanted to explore making a full runnable program using AWT + Swing. At the time, I had only made very simple programs using Python's Tkinter, so it was very rewarding to create this program, which at the time I had considered relatively complex to what I had previously done.
