import java.awt.*;

public class Fireball {
    private int x, y;
    private int speed = 10;
    private boolean direction;
    private boolean active = true;
    private int animationFrame = 0;
    private int animationTimer = 0;

    public Fireball(int x, int y, boolean facingRight) {
        this.x = x;
        this.y = y;
        this.direction = facingRight;
    }

    public void update() {
        x += direction ? speed : -speed;

        animationTimer++;
        if (animationTimer > 3) {
            animationFrame = (animationFrame + 1) % 4;
            animationTimer = 0;
        }
    }

    public void draw(Graphics2D g2d) {
        if (!active) return;

        int[] xPoints = new int[8];
        int[] yPoints = new int[8];

        for (int i = 0; i < 8; i++) {
            double angle = Math.toRadians(i * 45 + animationFrame * 20);
            int r = 8 + (i % 2) * 4;
            xPoints[i] = x + (int)(Math.cos(angle) * r);
            yPoints[i] = y + (int)(Math.sin(angle) * r);
        }

        g2d.setColor(Color.ORANGE);
        g2d.fillPolygon(xPoints, yPoints, 8);

        g2d.setColor(Color.YELLOW);
        g2d.fillOval(x - 6, y - 6, 12, 12);

        g2d.setColor(Color.WHITE);
        g2d.fillOval(x - 3, y - 3, 6, 6);
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }

    public Rectangle getBounds() {
        return new Rectangle(x - 10, y - 10, 20, 20);
    }

    public int getX() {
        return x;
    }
}
