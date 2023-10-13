package simulation.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.util.Locale;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.event.MouseInputAdapter;
import simulation.Simulation;
import simulation.body.object.*;
import simulation.body.particle.Particle;
import simulation.util.Vec2;
import simulation.util.constructor.ObjectParams;

class SimulationPanel extends JPanel {

  private static final long serialVersionUID = 343208457896132L;

  private final Timer timer;
  private final Simulation simulation;

  private final Vec2 initialMousePosition = new Vec2();
  private final Vec2 mousePosition = new Vec2();

  private enum PressType {
    NONE,
    NEW_PARTICLE,
    RECTANGLE,
    CIRCLE,
    GRAB_PARTICLES,
    DELETE_OBJECT,
  }

  private PressType pressed = PressType.NONE;

  private final class CustomMouseListener extends MouseInputAdapter {

    @Override
    public void mousePressed(MouseEvent e) {
      initialMousePosition.set(e.getX(), e.getY());
      mousePosition.set(e.getX(), e.getY());

      if (mouseMode == MouseMode.NEW_OBJECT) {
        pressed = PressType.valueOf(objectType.name());
      } else pressed = PressType.valueOf(mouseMode.name());

      if (mouseMode == MouseMode.GRAB_PARTICLES) {
        simulation.grab(initialMousePosition);
      }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
      mousePosition.set(e.getX(), e.getY());

      if (pressed == PressType.GRAB_PARTICLES) {
        simulation.moveGrab(mousePosition);
      }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
      pressed = PressType.NONE;

      if (mouseMode == MouseMode.DELETE_OBJECT) {
        simulation.deleteObject(mousePosition);
        return;
      }

      mousePosition.sub(initialMousePosition);

      switch (mouseMode) {
        case NEW_PARTICLE:
          newParticle(initialMousePosition, mousePosition, getParticleType());
          break;
        case NEW_OBJECT:
          switch (objectType) {
            case RECTANGLE:
              newObject(
                new ObjectParams(
                  initialMousePosition,
                  (short) Math.round(mousePosition.x()),
                  (short) Math.round(mousePosition.y())
                ),
                Rectangle.class
              );
              break;
            case CIRCLE:
              newObject(
                new ObjectParams(
                  initialMousePosition,
                  (short) Math.round(mousePosition.getLength())
                ),
                Circle.class
              );
              break;
            default:
              break;
          }
          break;
        case GRAB_PARTICLES:
          simulation.releaseGrab();
          break;
        default:
          break;
      }
    }

    @SuppressWarnings("unchecked")
    private Class<? extends Particle> getParticleType() {
      try {
        return (Class<? extends Particle>) Class.forName(
          "simulation.body.particle." + getFormattedString(particleType.name())
        );
      } catch (ClassNotFoundException e) {
        System.out.println("Could not find particle");
        return null;
      }
    }

    private String getFormattedString(String str) {
      if (str == null) return null;

      final String[] parts = str.split("_");

      if (parts.length == 1) {
        return formatString(str);
      }

      final StringBuilder sb = new StringBuilder();

      for (String s : parts) sb.append(formatString(s));

      return sb.toString().trim();
    }

    private String formatString(String str) {
      return (
        str.substring(0, 1).toUpperCase(Locale.ROOT) +
        str.substring(1).toLowerCase(Locale.ROOT)
      );
    }
  }

  private static final String NO_NULLS = "NO NULLS ALLOWED!";

  enum MouseMode {
    NEW_PARTICLE,
    NEW_OBJECT,
    GRAB_PARTICLES,
    DELETE_OBJECT;

    public static MouseMode valueOfStr(String mode) {
      try {
        return valueOf(mode.trim().replace(" ", "_").toUpperCase(Locale.ROOT));
      } catch (IllegalArgumentException e) {
        System.out.println("Invalid mode: " + mode);
        return null;
      } catch (NullPointerException e) {
        System.out.println(NO_NULLS);
        return null;
      }
    }
  }

  enum ParticleType {
    PARTICLE,
    ATTRACTOR_PARTICLE,
    REPULSER_PARTICLE,
    CHARGED_PARTICLE;

    public static ParticleType valueOfStr(String type) {
      try {
        return valueOf(type.trim().replace(" ", "_").toUpperCase(Locale.ROOT));
      } catch (IllegalArgumentException e) {
        System.out.println("Invalid type: " + type);
        return null;
      } catch (NullPointerException e) {
        System.out.println(NO_NULLS);
        return null;
      }
    }
  }

  enum ObjectType {
    RECTANGLE,
    CIRCLE;

    public static ObjectType valueOfStr(String type) {
      try {
        return valueOf(type.trim().replace(" ", "_").toUpperCase(Locale.ROOT));
      } catch (IllegalArgumentException e) {
        System.out.println("Invalid type: " + type);
        return null;
      } catch (NullPointerException e) {
        System.out.println(NO_NULLS);
        return null;
      }
    }
  }

  private MouseMode mouseMode = MouseMode.NEW_PARTICLE;
  private ParticleType particleType = ParticleType.PARTICLE;
  private ObjectType objectType = ObjectType.RECTANGLE;

  public SimulationPanel() {
    setBackground(Color.BLACK);
    setLayout(new BorderLayout());

    simulation = new Simulation();
    timer =
      new Timer(
        10,
        e -> {
          simulation.update();
          repaint();
        }
      );
    final CustomMouseListener listener = new CustomMouseListener();

    addMouseListener(listener);

    addMouseMotionListener(listener);
  }

  public void setMouseMode(String mode) {
    MouseMode modeEnum = MouseMode.valueOfStr(mode);
    if (modeEnum == null) return;
    mouseMode = modeEnum;
  }

  public void setParticleType(String type) {
    ParticleType typeEnum = ParticleType.valueOfStr(type);
    if (typeEnum == null) return;
    particleType = typeEnum;
  }

  public void setObjectType(String type) {
    ObjectType typeEnum = ObjectType.valueOfStr(type);
    if (typeEnum == null) return;
    objectType = typeEnum;
  }

  public void start() {
    resizeSimulation();
    simulation.start();
    timer.start();
  }

  public boolean isRunning() {
    return timer.isRunning();
  }

  public void resizeSimulation() {
    final int width = getWidth();
    final int height = getHeight();
    if (width == 0 || height == 0) return;
    simulation.resize((short) width, (short) height);
  }

  public void resume() {
    if (timer.isRunning()) return;
    timer.start();
  }

  public void pause() {
    if (!timer.isRunning()) return;
    timer.stop();
  }

  public void newParticle(
    Vec2 position,
    Vec2 velocity,
    Class<? extends Particle> type
  ) {
    if (!timer.isRunning()) return;
    simulation.newParticle(position, velocity, type);
  }

  public void newObject(
    ObjectParams params,
    Class<? extends Environment> type
  ) {
    if (!timer.isRunning()) return;
    simulation.newObject(params, type);
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    simulation.draw(g);
    if (pressed != PressType.NONE) drawOverlay(g);
  }

  private void drawOverlay(Graphics g) {
    g.setColor(Color.WHITE);
    switch (pressed) {
      case NEW_PARTICLE:
        g.drawLine(
          Math.round(initialMousePosition.x()),
          Math.round(initialMousePosition.y()),
          Math.round(mousePosition.x()),
          Math.round(mousePosition.y())
        );
        break;
      case RECTANGLE:
        drawRectangle(g);
        break;
      case CIRCLE:
        final int radius = Math.round(
          new Vec2(mousePosition).sub(initialMousePosition).getLength()
        );
        g.drawOval(
          Math.round(initialMousePosition.x() - radius),
          Math.round(initialMousePosition.y() - radius),
          radius * 2,
          radius * 2
        );
        break;
      default:
        break;
    }
  }

  private void drawRectangle(Graphics g) {
    float x = initialMousePosition.x();
    float y = initialMousePosition.y();

    float width = mousePosition.x() - x;
    float height = mousePosition.y() - y;

    if (width < 0) {
      width *= -1;
      x -= width;
    }

    if (height < 0) {
      height *= -1;
      y -= height;
    }

    g.drawRect(
      Math.round(x),
      Math.round(y),
      Math.round(width),
      Math.round(height)
    );
  }
}
