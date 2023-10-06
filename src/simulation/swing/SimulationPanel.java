package simulation.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JPanel;
import javax.swing.Timer;
import simulation.Simulation;

public class SimulationPanel extends JPanel {

  private static final long serialVersionUID = 343208457896132L;

  private final Timer timer;
  private final Simulation simulation;

  public SimulationPanel() {
    setBackground(Color.BLACK);
    simulation = new Simulation();
    timer =
      new Timer(
        10,
        e -> {
          simulation.update();
          repaint();
        }
      );
  }

  public void start() {
    simulation.start();
    timer.start();
  }

  public boolean isRunning() {
    return timer.isRunning();
  }

  public void resizeSimulation(Dimension size) {
    simulation.resize((short) size.width, (short) size.height);
  }

  public void resume() {
    if (timer.isRunning()) return;
    timer.start();
  }

  public void pause() {
    if (!timer.isRunning()) return;
    timer.stop();
  }

  public void newParticle(int x, int y) {
    if (!timer.isRunning()) return;
    simulation.newParticle(x, y);
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    simulation.draw(g);
  }
}
