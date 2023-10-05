package simulation.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

public class SimulationFrame extends JFrame {

  private SimulationPanel simPanel;

  public SimulationFrame() {
    simPanel = new SimulationPanel();
    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    setTitle("Particle Simulation");
    setLayout(new BorderLayout());
    Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
    simPanel.resizeSimulation(
      new Dimension((int) (size.width * 0.8), (int) (size.height * 0.8)),
      false
    );
    minimise();
    simPanel.start();

    addKeyListener(
      new KeyAdapter() {
        @Override
        public void keyPressed(KeyEvent e) {
          switch (e.getKeyCode()) {
            case KeyEvent.VK_ESCAPE:
              System.exit(0);
              break;
            case KeyEvent.VK_F11:
              if (isMaximized()) {
                minimise();
              } else maximise();
              break;
            case KeyEvent.VK_SPACE:
              if (simPanel.isRunning()) {
                simPanel.pause();
              } else simPanel.resume();
              break;
            default:
              break;
          }
        }
      }
    );

    addMouseListener(
      new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          int x = e.getX();
          int y = e.getY();
          Insets insets = getInsets();
          if (insets != null) {
            x -= insets.left;
            y -= insets.top;
          }
          simPanel.newParticle(x, y);
        }
      }
    );

    addComponentListener(
      new ComponentAdapter() {
        @Override
        public void componentResized(ComponentEvent e) {
          simPanel.resizeSimulation(getInnerSize(), isMaximized());
        }
      }
    );
  }

  private boolean isMaximized() {
    return getExtendedState() == Frame.MAXIMIZED_BOTH && isVisible();
  }

  private void maximise() {
    if (isMaximized()) return;
    dispose();
    setUndecorated(true);
    setExtendedState(Frame.MAXIMIZED_BOTH);
    add(simPanel, BorderLayout.CENTER);
    setVisible(true);
  }

  private void minimise() {
    if (getExtendedState() == Frame.NORMAL && isVisible()) return;
    dispose();
    setExtendedState(Frame.NORMAL);
    setUndecorated(false);
    add(simPanel, BorderLayout.CENTER);
    pack();
    setLocationRelativeTo(null);
    setVisible(true);
  }

  private Dimension getInnerSize() {
    Dimension size = getSize();
    Insets insets = getInsets();
    if (insets != null) {
      size.width -= insets.left + insets.right;
      size.height -= insets.top + insets.bottom;
    }
    return size;
  }
}
