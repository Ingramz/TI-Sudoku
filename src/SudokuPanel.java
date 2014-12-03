import javax.swing.*;
import java.awt.*;

public class SudokuPanel extends JPanel {
    int[][] original;
    int[][] solved;
    int[][] regions;

    public SudokuPanel(int[][] original, int[][] solved, int[][] regions) {
        this.solved = solved;
        this.original = original;
        this.regions = regions;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        Graphics2D g2d = (Graphics2D) g;
        // For nicer graphics, turn on antialiasing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        this.setBackground(new Color(234, 234, 234)); // #EAEAEA
        // Make sure the game field is always a square that will fit inside the window.
        int fieldLength = Math.min(this.getHeight(), this.getWidth());
        int startX = (this.getWidth() - fieldLength) / 2;
        int startY = (this.getHeight() - fieldLength) / 2;

        int sq_len = (fieldLength - 10) / 9;
        int vis_len = 9 * sq_len + 10;

        // Colors for regions
        Color[] colors = {
                new Color(238, 136, 136),
                new Color(238, 170, 136),
                new Color(238, 238, 136),
                new Color(136, 238, 136),
                new Color(136, 238, 238),
                new Color(136, 170, 238),
                new Color(238, 136, 238),
                new Color(170, 136, 238),
                new Color(238, 136, 170)
        };

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                // Draw the colored square
                g2d.setColor(colors[regions[i][j] - 1]);
                g2d.fillRect(1 + startX + j * sq_len + j,
                        1 + startY + i * sq_len + i,
                        sq_len, sq_len);

                // Then an inner border for every area which touches another region or the border of grid
                // This makes them visually thicker.
                g2d.setColor(Color.black);
                int squareLeft = 1 + startX + j * sq_len + j;
                int squareTop = 1 + startY + i * sq_len + i;
                if (j == 0 || regions[i][j] != regions[i][j - 1])
                    g2d.drawLine(squareLeft, squareTop, squareLeft, squareTop + sq_len);

                if (j == 8 || regions[i][j] != regions[i][j + 1])
                    g2d.drawLine(squareLeft + sq_len - 1, squareTop, squareLeft + sq_len - 1, squareTop + sq_len - 1);

                if (i == 0 || regions[i][j] != regions[i - 1][j])
                    g2d.drawLine(squareLeft, squareTop, squareLeft + sq_len, squareTop);

                if (i == 8 || regions[i][j] != regions[i + 1][j])
                    g2d.drawLine(squareLeft, squareTop + sq_len - 1, squareLeft + sq_len, squareTop + sq_len - 1);

                String num = solved[i][j] + "";

                if (solved[i][j] == original[i][j])
                    g2d.setColor(Color.blue); // Color for numbers that were present in input
                else
                    g2d.setColor(Color.black); // Color for numbers that were placed by solver

                // Draw the number inside the square
                g2d.setFont(new Font(g2d.getFont().getName(), Font.PLAIN, (int) (sq_len * 0.8)));
                int strlen = (int) g2d.getFontMetrics().getStringBounds(num, g2d).getWidth();
                int strhei = (int) g2d.getFontMetrics().getStringBounds(num, g2d).getHeight();
                int horizStart = sq_len / 2 - strlen / 2;
                int vertStart = strhei / 4;
                g2d.drawString(num, horizStart + squareLeft, squareTop + sq_len - vertStart);
            }
        }

        // Horizontal and vertical lines for the grid (1px)
        g2d.setColor(Color.black);
        for (int i = 0; i < 10; i++) {
            g2d.drawLine(startX, startY + i * sq_len + i, startX + vis_len - 1, startY + i * sq_len + i);
            g2d.drawLine(startX + i * sq_len + i, startY, startX + i * sq_len + i, startY + vis_len - 1);
        }
    }
}
