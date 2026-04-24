import java.awt.*;

public class Coin {
    private int x, y;
    private int radius = 12;
    private int animationFrame = 0;
    private int animationTimer = 0;
    private boolean collected = false;
    private int collectAnimation = 0;

    public Coin(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void update() {
        if (collected) {
            collectAnimation++;
        } else {
            animationTimer++;
            if (animationTimer > 15) {
                animationFrame = (animationFrame + 1) % 4;
                animationTimer = 0;
            }
        }
    }

    public void draw(Graphics2D g2d) {
        if (collected && collectAnimation > 20) return;

        Graphics2D g2 = (Graphics2D) g2d.create();

        if (collected) {
            g2.translate(x, y - collectAnimation * 3);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1 - collectAnimation / 20f));
        }

        double scale = 0.5 + Math.abs(2 - animationFrame) * 0.25;

        g2.setColor(new Color(255, 215, 0));
        g2.fillOval((int)(x - radius * scale), (int)(y - radius * scale),
                    (int)(radius * 2 * scale), (int)(radius * 2 * scale));

        g2.setColor(new Color(255, 245, 100));
        g2.fillOval((int)(x - radius * 0.6 * scale), (int)(y - radius * 0.6 * scale),
                    (int)(radius * scale), (int)(radius * scale));

        g2.setColor(new Color(200, 160, 0));
        g2.drawString("$", x - 4, y + 5);

        g2.dispose();
    }

    public void collect() {
        collected = true;
    }

    public Rectangle getBounds() {
        return new Rectangle(x - radius, y - radius, radius * 2, radius * 2);
    }
}
