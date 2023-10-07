package simulation;

import simulation.swing.SimulationFrame;

public class App {

  public static void main(String[] args) {
    Settings.load();
    new SimulationFrame();
  }
}
