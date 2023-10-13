package simulation.swing;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

class PausePanel extends JPanel {

  private class EmptyPanel extends JPanel {

    EmptyPanel() {
      setOpaque(false);
    }
  }

  private final JLabel label1;
  private final JLabel label2;

  public PausePanel() {
    final int rows = 7;
    setLayout(new GridLayout(rows, 1));
    setBackground(new Color(100, 100, 100, 100));
    setOpaque(false);
    setVisible(false);

    final MouseAdapter mouseAdapter = new MouseAdapter() {};
    addMouseListener(mouseAdapter);
    addMouseMotionListener(mouseAdapter);

    for (byte i = 0; i < rows / 2; i++) add(new EmptyPanel());

    label1 = new JLabel("Simulation Paused");
    label1.setHorizontalAlignment(SwingConstants.CENTER);
    label1.setVerticalAlignment(SwingConstants.BOTTOM);
    label1.setForeground(Color.LIGHT_GRAY);
    add(label1);

    label2 = new JLabel("Press space to resume...");
    label2.setHorizontalAlignment(SwingConstants.CENTER);
    label2.setVerticalAlignment(SwingConstants.TOP);
    label2.setForeground(Color.LIGHT_GRAY);
    add(label2);
  }

  public void resizeFont(int fontSize) {
    label1.setFont(new Font("Gill Sans", Font.BOLD, fontSize * 2));
    label2.setFont(new Font("Gill Sans", Font.BOLD, fontSize));
  }

  @Override
  protected void paintComponent(Graphics g) {
    g.setColor(getBackground());
    g.fillRect(0, 0, getWidth(), getHeight());
    super.paintComponent(g);
  }
}
