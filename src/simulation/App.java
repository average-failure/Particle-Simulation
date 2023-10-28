package simulation;

import javax.swing.SwingUtilities;
import simulation.swing.SimulationFrame;

public class App {

  public static void main(String[] args) {
    SwingUtilities.invokeLater(SimulationFrame::new);
  }
}
