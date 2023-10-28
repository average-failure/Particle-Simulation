package simulation;

import javax.swing.SwingUtilities;
import simulation.swing.SimulationFrame;

public class App {

  public static void main(String[] args) {
    Settings.load();
    SwingUtilities.invokeLater(SimulationFrame::new);
  }
}
