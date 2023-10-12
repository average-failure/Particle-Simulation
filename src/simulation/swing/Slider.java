package simulation.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Locale;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.plaf.basic.BasicSliderUI;
import simulation.Settings;

class Slider extends JPanel {

  private class Label extends JLabel {

    public Label(String text) {
      super(text, CENTER);
      setAlignmentX(CENTER_ALIGNMENT);
      setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
    }
  }

  private class CustomSlider extends JSlider {

    private class SliderUI extends BasicSliderUI {

      public SliderUI(JSlider slider) {
        super(slider);
      }

      @Override
      public void paintFocus(Graphics g) {
        /* not needed */
      }

      @Override
      protected Dimension getThumbSize() {
        return new Dimension(14, 14);
      }

      @Override
      public void paintThumb(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(
          RenderingHints.KEY_ANTIALIASING,
          RenderingHints.VALUE_ANTIALIAS_ON
        );
        g2.setColor(slider.getForeground());
        g2.fillOval(
          thumbRect.x,
          thumbRect.y,
          thumbRect.width,
          thumbRect.height
        );
      }

      @Override
      public void paintTrack(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(
          RenderingHints.KEY_ANTIALIASING,
          RenderingHints.VALUE_ANTIALIAS_ON
        );
        g2.setColor(slider.getBackground());
        if (slider.getOrientation() == VERTICAL) {
          g2.fillRoundRect(
            slider.getWidth() / 2 - 2,
            2,
            4,
            slider.getHeight(),
            1,
            1
          );
        } else {
          g2.fillRoundRect(
            2,
            slider.getHeight() / 2 - 2,
            slider.getWidth() - 5,
            4,
            1,
            1
          );
        }
      }
    }

    public CustomSlider(Label label, int min, int max, int value) {
      super(min, max, value);
      setOpaque(false);
      setBackground(new Color(180, 180, 180));
      setForeground(new Color(69, 124, 235));
      setUI(new SliderUI(this));

      addChangeListener(e -> {
        int v = ((JSlider) e.getSource()).getValue();
        Settings.put(
          Settings.valueOf(textRaw.replace(" ", "_").toUpperCase(Locale.ROOT)),
          v / 1000f
        );
        label.setText(text + (v / 1000f) + units);
      });
    }
  }

  private final String textRaw;
  private final String text;
  private final String units;

  public Slider(String text, String units, int min, int max, int value) {
    textRaw = text;
    this.text = text + ": ";
    this.units = units == null || units.isEmpty() ? "" : " " + units;
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    final Label label = new Label(this.text + value / 1000f + this.units);
    add(label);
    add(new CustomSlider(label, min, max, value));
    setOpaque(false);
  }

  public Slider(String text, int min, int max, int value) {
    this(text, null, min, max, value);
  }
}
