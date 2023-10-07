package simulation.swing;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class PausePanel extends JPanel {

  public PausePanel(int fontSize) {
    setLayout(new GridLayout(2, 1));
    setBackground(new Color(100, 100, 100, 100));
    setOpaque(false);
    final JLabel label1 = new JLabel("Simulation Paused");
    label1.setFont(new Font("Gill Sans", Font.BOLD, fontSize * 2));
    label1.setHorizontalAlignment(SwingConstants.CENTER);
    label1.setVerticalAlignment(SwingConstants.BOTTOM);
    label1.setForeground(Color.LIGHT_GRAY);
    add(label1);
    final JLabel label2 = new JLabel("Press space to resume...");
    label2.setFont(new Font("Gill Sans", Font.BOLD, fontSize));
    label2.setHorizontalAlignment(SwingConstants.CENTER);
    label2.setVerticalAlignment(SwingConstants.TOP);
    label2.setForeground(Color.LIGHT_GRAY);
    add(label2);
  }

  @Override
  protected void paintComponent(Graphics g) {
    g.setColor(getBackground());
    g.fillRect(0, 0, getWidth(), getHeight());
    super.paintComponent(g);
  }
}
