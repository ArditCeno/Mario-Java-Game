import java.awt.*;

public class Enemy {
    private int x, y;
    private int width = 32;
    private int height = 32;
    private int speed = 2;
    private int direction = -1;
    private int animationFrame = 0;
    private int animationTimer = 0;
    private boolean alive = true;
    private boolean stomped = false;
    private int deathTimer = 0;

    private int startX;
    private int patrolRange = 100;

    public Enemy(int x, int y, int patrolRange) {
        this.x = x;
        this.y = y;
        this.startX = x;
        this.patrolRange = patrolRange;
    }

    public void update() {
        if (!alive) {
            deathTimer++;
            return;
        }

        if (!stomped) {
            x += speed * direction;

            if (x <= startX - patrolRange || x >= startX + patrolRange) {
                direction *= -1;
            }

            animationTimer++;
            if (animationTimer > 10) {
                animationFrame = (animationFrame + 1) % 2;
                animationTimer = 0;
            }
        }
    }

    public void draw(Graphics2D g2d) {
        if (!alive && deathTimer > 30) return;

        Graphics2D g2 = (Graphics2D) g2d.create();

        if (!alive) {
            g2.translate(x + width / 2, y + height);
            g2.rotate(Math.PI);
            g2.translate(-x - width / 2, -y - height);
        }

        if (stomped) {
            g2.translate(x, y + height - 16);
            g2.scale(1, 0.5f);
            g2.translate(-x, -y - height + 16);
        }

        g2.setColor(new Color(139, 90, 43));
        g2.fillRect(x + 4, y + 4, 24, 20);

        g2.setColor(new Color(205, 133, 63));
        g2.fillRect(x + 2, y + 6, 6, 14);
        g2.fillRect(x + 24, y + 6, 6, 14);

        g2.setColor(Color.BLACK);
        g2.fillRect(x + 8, y + 8, 6, 6);
        g2.fillRect(x + 18, y + 8, 6, 6);

        g2.setColor(Color.WHITE);
        g2.fillRect(x + 9, y + 9, 3, 3);
        g2.fillRect(x + 19, y + 9, 3, 3);

        g2.setColor(Color.BLACK);
        g2.fillRect(x + 10, y + 16, 12, 4);

        if (animationFrame == 1) {
            g2.fillRect(x + 2, y + 24, 8, 6);
            g2.fillRect(x + 22, y + 24, 8, 6);
        } else {
            g2.fillRect(x, y + 22, 10, 6);
            g2.fillRect(x + 22, y + 22, 10, 6);
        }

        g2.dispose();
    }

    public void stomp() {
        stomped = true;
        alive = false;
    }

    public void defeat() {
        alive = false;
    }

    public Rectangle getBounds() {
        if (stomped) {
            return new Rectangle(x, y + height - 8, width, 8);
        }
        return new Rectangle(x + 2, y + 2, width - 4, height - 4);
    }

    public int getY() { return y; }
    public int getHeight() { return height; }
}
