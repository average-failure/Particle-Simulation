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
import javax.swing.JLayeredPane;
import javax.swing.WindowConstants;

public final class SimulationFrame extends JFrame {

  private final JLayeredPane content = new JLayeredPane();

  private boolean wasMaximised = false;

  public SimulationFrame() {
    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    setTitle("Particle Simulation");
    setLayout(new BorderLayout());
    setContentPane(content);

    final SimulationPanel simPanel = new SimulationPanel();
    final Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
    size.setSize(size.width * 0.8, size.height * 0.8);
    simPanel.resizeSimulation(size);
    content.setLayout(new BorderLayout());
    JLayeredPane.putLayer(simPanel, JLayeredPane.DEFAULT_LAYER);
    content.add(simPanel, BorderLayout.CENTER);
    content.setPreferredSize(size);
    minimise();
    simPanel.start();

    addKeyListener(
      new KeyAdapter() {
        private PausePanel pausePanel;

        @Override
        public void keyPressed(KeyEvent e) {
          switch (e.getKeyCode()) {
            case KeyEvent.VK_ESCAPE:
              System.exit(0);
              break;
            case KeyEvent.VK_F11:
              if (isMaximized(true)) {
                minimise();
              } else maximise();
              break;
            case KeyEvent.VK_SPACE:
              if (simPanel.isRunning()) {
                simPanel.pause();
                pausePanel =
                  new PausePanel(Math.min(getWidth(), getHeight()) / 20);
                JLayeredPane.putLayer(pausePanel, JLayeredPane.MODAL_LAYER);
                pausePanel.setSize(getSize());
                content.add(pausePanel);
              } else {
                simPanel.resume();
                content.remove(pausePanel);
              }
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
          final Insets insets = getInsets();
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
          Dimension innerSize = getInnerSize();
          simPanel.resizeSimulation(innerSize);
          if (!isMaximized(false)) content.setPreferredSize(innerSize);
        }
      }
    );
  }

  private boolean isMaximized(boolean isDecorated) {
    return (
      getExtendedState() == Frame.MAXIMIZED_BOTH &&
      (!isDecorated || isUndecorated()) &&
      isVisible()
    );
  }

  private void maximise() {
    if (isMaximized(true)) return;
    if (getExtendedState() == Frame.MAXIMIZED_BOTH) wasMaximised = true;
    dispose();
    setUndecorated(true);
    setExtendedState(Frame.MAXIMIZED_BOTH);
    setVisible(true);
  }

  private void minimise() {
    if (getExtendedState() == Frame.NORMAL && isVisible()) return;
    dispose();
    setUndecorated(false);
    pack();
    if (wasMaximised) {
      setExtendedState(Frame.MAXIMIZED_BOTH);
      wasMaximised = false;
    } else setExtendedState(Frame.NORMAL);
    setLocationRelativeTo(null);
    setVisible(true);
  }

  private Dimension getInnerSize() {
    final Dimension size = getSize();
    final Insets insets = getInsets();
    if (insets != null) {
      size.width -= insets.left + insets.right;
      size.height -= insets.top + insets.bottom;
    }
    return size;
  }
}
