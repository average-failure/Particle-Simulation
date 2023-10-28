package simulation.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;
import simulation.Settings;

public final class SimulationFrame extends JFrame {

  private static final String EXIT = "exit";
  private static final String MIN_MAX = "minimise/maximise";
  private static final String PAUSE_RESUME = "pause/resume";

  private final JLayeredPane content = new JLayeredPane();
  private final SimulationPanel simPanel = new SimulationPanel();
  private boolean wasMaximised = false;

  public SimulationFrame() {
    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    setTitle("Particle Simulation");
    setLayout(new BorderLayout());
    setContentPane(content);

    final Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
    size.setSize(size.width * 0.8, size.height * 0.8);
    content.setPreferredSize(size);

    simPanel.resizeSimulation();

    content.setLayout(new BorderLayout());
    content.setLayer(content, JLayeredPane.DEFAULT_LAYER);
    content.add(simPanel, BorderLayout.CENTER);

    final SidePanel sliderPanel = new SidePanel(
      SidePanel.Side.RIGHT,
      this::addSliders
    );
    content.setLayer(sliderPanel, JLayeredPane.PALETTE_LAYER);
    content.add(sliderPanel, BorderLayout.EAST);

    final BottomPanel configPanel = new BottomPanel(simPanel);
    content.setLayer(configPanel, JLayeredPane.PALETTE_LAYER);
    content.add(configPanel, BorderLayout.SOUTH);

    minimise();
    simPanel.start();

    getAllComponents(this)
      .forEach(c -> {
        for (int condition : new int[] {
          JComponent.WHEN_IN_FOCUSED_WINDOW,
          JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT,
          JComponent.WHEN_FOCUSED,
        }) c
          .getInputMap(condition)
          .put(KeyStroke.getKeyStroke("SPACE"), "none");
      });

    putInput("ESCAPE", EXIT);
    putAction(EXIT, () -> System.exit(0));

    putInput("SPACE", PAUSE_RESUME);
    putAction(
      PAUSE_RESUME,
      () -> {
        if (simPanel.isRunning()) simPanel.pause(); else simPanel.resume();
      }
    );

    putInput("F11", MIN_MAX);
    putAction(
      MIN_MAX,
      () -> {
        if (isMaximized(true)) {
          minimise();
        } else maximise();
      }
    );

    addComponentListener(
      new ComponentAdapter() {
        @Override
        public void componentResized(ComponentEvent e) {
          simPanel.resizeSimulation();
          if (!isMaximized(false)) content.setPreferredSize(getInnerSize());
        }
      }
    );
  }

  void resizeSimulation() {
    simPanel.resizeSimulation();
  }

  private List<JComponent> getAllComponents(Container c) {
    Component[] comps = c.getComponents();
    List<JComponent> compList = new ArrayList<>();
    for (Component comp : comps) {
      if (!(comp instanceof JComponent)) continue;

      compList.add((JComponent) comp);
      compList.addAll(getAllComponents((Container) comp));
    }
    return compList;
  }

  private void putInput(String keyString, String actionMapKey) {
    content
      .getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
      .put(KeyStroke.getKeyStroke(keyString), actionMapKey);
  }

  private void putAction(String key, Runnable action) {
    content
      .getActionMap()
      .put(
        key,
        new AbstractAction() {
          @Override
          public void actionPerformed(ActionEvent e) {
            action.run();
          }
        }
      );
  }

  private void addSliders(Container panel) {
    for (Settings setting : Settings.values()) {
      final StringBuilder sb = new StringBuilder();
      for (String name : setting.name().split("_")) {
        sb.append(
          name.substring(0, 1).toUpperCase(Locale.ROOT) +
          name.substring(1).toLowerCase(Locale.ROOT) +
          " "
        );
      }
      final float value = Settings.get(setting) * 1000;
      panel.add(
        new Slider(
          sb.toString().trim(),
          Math.round(value / 5),
          Math.round(value * 5),
          (int) value
        )
      );
    }
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
