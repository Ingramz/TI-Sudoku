import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.text.ParseException;
import java.util.Scanner;

public class Sudoku {
    final Object lock = new Object(); // Lock for checking if UI is still open

    Sudoku(boolean custom) {
        JFrame jf = new JFrame("Sudoku lahendaja");
        FileDialog fd = new FileDialog(jf, "Ava algseisu sisaldav fail", FileDialog.LOAD);
        fd.setVisible(true);

        // Get the first file location
        File[] files = fd.getFiles();
        if (files.length == 0) {
            jf.dispose();
            error("Faili ei valitud.");
            return;
        }

        int[][] input;
        int[][] start;
        int[][] regions = {
                {1, 1, 1, 2, 2, 2, 3, 3, 3},
                {1, 1, 1, 2, 2, 2, 3, 3, 3},
                {1, 1, 1, 2, 2, 2, 3, 3, 3},
                {4, 4, 4, 5, 5, 5, 6, 6, 6},
                {4, 4, 4, 5, 5, 5, 6, 6, 6},
                {4, 4, 4, 5, 5, 5, 6, 6, 6},
                {7, 7, 7, 8, 8, 8, 9, 9, 9},
                {7, 7, 7, 8, 8, 8, 9, 9, 9},
                {7, 7, 7, 8, 8, 8, 9, 9, 9}
        };

        // Read in the first file
        try {
            String f = readFile(files[0]);
            input = readInput(f, true);
        } catch (IOException e) {
            jf.dispose();
            error("Viga faili lugemisel.");
            return;
        } catch (ParseException pe) {
            jf.dispose();
            error("Vigaselt vormindatud fail.");
            return;
        }

        // Read in the second file, if needed
        if (custom) {
            fd = new FileDialog(jf, "Ava regioonide fail", FileDialog.LOAD);
            fd.setVisible(true);

            files = fd.getFiles();
            if (files.length == 0) {
                jf.dispose();
                error("Faili ei valitud.");
                return;
            }

            try {
                String f = readFile(files[0]);
                regions = readInput(f, false);
            } catch (IOException e) {
                jf.dispose();
                error("Viga faili lugemisel.");
                return;
            } catch (ParseException pe) {
                jf.dispose();
                error("Vigaselt vormindatud fail.");
                return;
            }
        }

        if (input != null) {
            // Copy input to starting state
            start = new int[9][9];
            for (int i = 0; i < 9; i++)
                System.arraycopy(input[i], 0, start[i], 0, 9);

            // Attempt to solve the game
            int[][] solution;
            try {
                solution = solve(input, regions);
            } catch (ParseException pe) {
                jf.dispose();
                error("Vigane algseis");
                return;
            }

            if (solution == null) {
                jf.dispose();
                error("Mittelahenduv mäng.");
                return;
            }

            // Display it for the user
            jf.setMinimumSize(new Dimension(300, 300));
            jf.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            jf.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    super.windowClosed(e);
                    synchronized (lock) {
                        lock.notify();
                    }
                }
            });
            jf.getContentPane().add(new SudokuPanel(start, input, regions));
            jf.setVisible(true);
            jf.pack();

            // Do not continue to the main window before this one has been closed
            synchronized (lock) {
                while (jf.isDisplayable())
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        break;
                    }
            }
        }
    }

    static void error(String s) {
        JOptionPane.showMessageDialog(null,
                s,
                "Sudoku lahendaja",
                JOptionPane.ERROR_MESSAGE);
    }

    // Solution via backtracking, first add 1 to each line, then 2, 3...
    // Also keep track where a number has been placed in 1) row, 2) column, 3) region
    int[][] solve(int[][] input, int[][] regions) throws ParseException {
        // Before starting to place numbers, we need to tell where in columns, rows and regions the numbers have been
        // placed already
        boolean[][] usedInRow = new boolean[9][9];
        boolean[][] usedInColumn = new boolean[9][9];
        boolean[][] usedInRegion = new boolean[9][9];
        for (int row = 0; row < 9; row++)
            for (int column = 0; column < 9; column++) {
                int val = input[row][column] - 1;
                if (val != -1) {
                    int region = regions[row][column] - 1;
                    if (usedInRow[val][row] || usedInColumn[val][column] || usedInRegion[val][region])
                        throw new ParseException("Konflikteeruvad rea/veeru/regiooni väärtused", 0);

                    usedInRow[val][row] = usedInColumn[val][column] = usedInRegion[val][region] = true;
                }
            }

        return place(1, 0, input, regions, usedInRow, usedInColumn, usedInRegion);
    }

    private int[][] place(int num, int line, int[][] input, int[][] regions, boolean[][] usedInRow, boolean[][] usedInColumn, boolean[][] usedInRegion) {
        // We have reached the end
        if (num == 10) {
            for (int i = 0; i < 9; i++)
                for (int j = 0; j < 9; j++)
                    if (input[i][j] == 0)
                        return null;

            return input;
        }

        // Filled all lines with number num, start with next one
        if (line == 9)
            return place(num + 1, 0, input, regions, usedInRow, usedInColumn, usedInRegion);

        int val = num - 1;
        if (usedInRow[val][line]) // Skip this line
            return place(num, line + 1, input, regions, usedInRow, usedInColumn, usedInRegion);

        // Attempt placing num to each free column and on successful placement, recursively continue
        for (int col = 0; col < 9; col++) {
            int region = regions[line][col] - 1;
            if (input[line][col] == 0 && !usedInColumn[val][col] && !usedInRegion[val][region]) {
                usedInRow[val][line] = usedInColumn[val][col] = usedInRegion[val][region] = true;
                input[line][col] = num;
                int[][] res = place(num, line + 1, input, regions, usedInRow, usedInColumn, usedInRegion);
                if (res != null)
                    return res;
                usedInRow[val][line] = usedInColumn[val][col] = usedInRegion[val][region] = false;
                input[line][col] = 0;
            }
        }

        // State where all placements ended invalid, triggers backtracking
        return null;
    }

    // Parse input into grid
    int[][] readInput(String s, boolean blanks) throws ParseException {
        String pattern = "[1-9]" + (blanks ? "|-" : "");
        int[][] result = new int[9][9];
        Scanner sc = new Scanner(s);
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (sc.hasNext(pattern)) {
                    String t = sc.next(pattern);
                    if (t.equals("-"))
                        result[i][j] = 0;
                    else
                        result[i][j] = Integer.parseInt(t);
                } else {
                    throw new ParseException("Vigane sisend", 0);
                }
            }
        }
        sc.close();
        return result;
    }

    // Read the file
    String readFile(File f) throws IOException {
        byte[] encoded = Files.readAllBytes(f.toPath());
        return new String(encoded, Charset.defaultCharset());
    }
}
