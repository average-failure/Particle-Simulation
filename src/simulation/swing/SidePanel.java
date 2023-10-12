package simulation.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.Window;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

class SidePanel extends JPanel {

  public enum Side {
    RIGHT,
    LEFT,
  }

  public SidePanel(Side side, Consumer<Container> addComponents) {
    setLayout(new BorderLayout());
    setBackground(new Color(200, 200, 200));

    final JPanel panel = new JPanel();
    panel.setLayout(new GridLayout(0, 1, 30, 30));
    panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    addComponents.accept(panel);

    final JButton button = new JButton(side == Side.RIGHT ? "<" : ">");
    button.setBorder(
      BorderFactory.createCompoundBorder(
        BorderFactory.createEmptyBorder(5, 5, 5, 5),
        BorderFactory.createCompoundBorder(
          BorderFactory.createLoweredBevelBorder(),
          BorderFactory.createEmptyBorder(10, 10, 10, 10)
        )
      )
    );
    button.setBackground(Color.LIGHT_GRAY);
    add(button, BorderLayout.WEST);

    final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    button.addActionListener(e -> {
      Window frame = SwingUtilities.getWindowAncestor(this);
      if (frame == null) return;

      if (panel.getParent() == this) {
        remove(panel);
        button.setText(side == Side.RIGHT ? "<" : ">");
      } else {
        add(panel, BorderLayout.EAST);
        button.setText(side == Side.RIGHT ? ">" : "<");
      }

      executorService.schedule(
        ((SimulationFrame) frame)::resizeSimulation,
        50,
        TimeUnit.MILLISECONDS
      );
    });
  }
}
