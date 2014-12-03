import javax.swing.*;

public class Main {
    public static void main(String[] args) throws Exception {
        // Set to native look and feel for buttons and windows.
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {/* Nothing bad will happen if this fails. */}

        Object[] options = {"Lahenda standardne", "Lahenda muu kujuga", "Välju"};
        while (true) {
            // Ask for input
            int n = JOptionPane.showOptionDialog(null,
                    "<html><b>Sudoku lahendaja</b><br>2014 Indrek Ardel<br><br>Vali tegevus</html>",
                    "Sudoku lahendaja",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[2]);

            if (n == 0)
                new Sudoku(false);
            else if (n == 1)
                new Sudoku(true);
            else
                break;
        }
    }
}
