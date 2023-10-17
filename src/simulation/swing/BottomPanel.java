package simulation.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

public class BottomPanel extends JPanel {

  private class InnerPanel extends JPanel {

    public InnerPanel(Consumer<JPanel> fillFn) {
      setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
      setBackground(new Color(150, 150, 150));
      fillFn.accept(this);
    }
  }

  public BottomPanel(SimulationPanel simPanel) {
    setBackground(new Color(200, 200, 200));
    setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
    setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 0));

    add(
      new InnerPanel(panel ->
        fillComboBox(
          panel,
          "Mouse Mode",
          new String[] {
            "New Particle",
            "New Object",
            "Grab Particles",
            "Delete Object",
          },
          simPanel::setMouseMode
        )
      )
    );
    add(
      new InnerPanel(panel ->
        fillComboBox(
          panel,
          "Particle Type",
          new String[] {
            "Particle",
            "Attractor Particle",
            "Repulser Particle",
            "Charged Particle",
            "Copy Particle",
            "Splat Particle",
          },
          simPanel::setParticleType
        )
      )
    );
    add(
      new InnerPanel(panel ->
        fillComboBox(
          panel,
          "Object Type",
          new String[] { "Rectangle", "Circle" },
          simPanel::setObjectType
        )
      )
    );
  }

  public void add(InnerPanel comp) {
    super.add(comp);
    add(Box.createRigidArea(new Dimension(10, 0)));
  }

  private void fillComboBox(
    JPanel panel,
    String title,
    String[] options,
    Consumer<String> actionFn
  ) {
    panel.setBorder(createEtchedTitledBorder(title));
    ((JComboBox<?>) panel.add(new JComboBox<>(options))).addActionListener(e ->
        actionFn.accept(
          String.valueOf(((JComboBox<?>) e.getSource()).getSelectedItem())
        )
      );
  }

  private Border createEtchedTitledBorder(String title) {
    return BorderFactory.createCompoundBorder(
      BorderFactory.createCompoundBorder(
        BorderFactory.createEmptyBorder(5, 5, 5, 5),
        BorderFactory.createTitledBorder(
          BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
          title,
          TitledBorder.CENTER,
          TitledBorder.DEFAULT_JUSTIFICATION
        )
      ),
      BorderFactory.createEmptyBorder(5, 5, 5, 5)
    );
  }
}
