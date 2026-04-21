import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;

public class GamePanel extends JPanel {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 500;

    private Timer gameLoop;
    private Player player;
    private ArrayList<Platform> platforms;
    private ArrayList<Enemy> enemies;
    private ArrayList<Coin> coins;
    private ArrayList<Fireball> fireballs;

    private int score = 0;
    private int cameraX = 0;
    private GameState gameState = GameState.START;

    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private boolean spacePressed = false;
    private boolean xPressed = false;

    private int levelWidth = 2400;

    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(new Color(107, 140, 255));
        setFocusable(true);

        initGame();
        setupControls();

        gameLoop = new Timer(16, e -> {
            if (gameState == GameState.PLAYING) {
                update();
                repaint();
            }
        });
        gameLoop.start();
    }

    private void initGame() {
        player = new Player(100, 300);
        platforms = new ArrayList<>();
        enemies = new ArrayList<>();
        coins = new ArrayList<>();
        fireballs = new ArrayList<>();
        score = 0;
        cameraX = 0;

        platforms.add(new Platform(0, 420, levelWidth, 80));

        platforms.add(new Platform(200, 320, 150, 20));
        platforms.add(new Platform(500, 280, 100, 20));
        platforms.add(new Platform(700, 320, 150, 20));
        platforms.add(new Platform(950, 250, 100, 20));
        platforms.add(new Platform(1150, 320, 200, 20));
        platforms.add(new Platform(1450, 280, 100, 20));
        platforms.add(new Platform(1650, 220, 150, 20));
        platforms.add(new Platform(1900, 280, 100, 20));
        platforms.add(new Platform(2100, 320, 200, 20));

        enemies.add(new Enemy(400, 390, 50));
        enemies.add(new Enemy(800, 390, 50));
        enemies.add(new Enemy(1200, 390, 50));
        enemies.add(new Enemy(1600, 390, 50));
        enemies.add(new Enemy(2000, 390, 50));

        for (int i = 0; i < 15; i++) {
            int x = 300 + i * 150 + (int)(Math.random() * 50);
            int y = 200 + (int)(Math.random() * 150);
            coins.add(new Coin(x, y));
        }
    }

    private void setupControls() {
        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT: leftPressed = true; break;
                    case KeyEvent.VK_RIGHT: rightPressed = true; break;
                    case KeyEvent.VK_SPACE:
                        if (!spacePressed) {
                            player.jump();
                            spacePressed = true;
                        }
                        break;
                    case KeyEvent.VK_X:
                        if (!xPressed) {
                            shootFireball();
                            xPressed = true;
                        }
                        break;
                    case KeyEvent.VK_ENTER:
                        if (gameState == GameState.START || gameState == GameState.GAMEOVER) {
                            restartGame();
                        }
                        break;
                    case KeyEvent.VK_ESCAPE:
                        if (gameState == GameState.PLAYING) {
                            gameState = GameState.START;
                        }
                        break;
                }
            }

            public void keyReleased(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT: leftPressed = false; break;
                    case KeyEvent.VK_RIGHT: rightPressed = false; break;
                    case KeyEvent.VK_SPACE: spacePressed = false; break;
                    case KeyEvent.VK_X: xPressed = false; break;
                }
            }
        });
    }

    private void shootFireball() {
        if (player.canShoot()) {
            fireballs.add(new Fireball(player.getX() + (player.isFacingRight() ? 30 : -10), player.getY() + 15, player.isFacingRight()));
            player.resetFireballCooldown();
        }
    }

    private void restartGame() {
        initGame();
        gameState = GameState.PLAYING;
    }

    private void update() {
        if (leftPressed) player.moveLeft();
        if (rightPressed) player.moveRight();
        if (!leftPressed && !rightPressed) player.stop();

        player.update();

        for (Platform platform : platforms) {
            player.collideWith(platform);
        }

        for (Iterator<Enemy> it = enemies.iterator(); it.hasNext(); ) {
            Enemy enemy = it.next();
            enemy.update();

            if (player.getBounds().intersects(enemy.getBounds())) {
                if (player.isFalling() && player.getY() + player.getHeight() < enemy.getY() + enemy.getHeight() / 2) {
                    enemy.stomp();
                    player.bounce();
                    score += 100;
                } else if (player.isAttacking()) {
                    enemy.defeat();
                    score += 200;
                    it.remove();
                } else {
                    player.hit();
                    if (player.getLives() <= 0) {
                        gameState = GameState.GAMEOVER;
                    }
                }
            }
        }

        for (Iterator<Coin> it = coins.iterator(); it.hasNext(); ) {
            Coin coin = it.next();
            if (player.getBounds().intersects(coin.getBounds())) {
                coin.collect();
                score += 50;
                it.remove();
            }
        }

        for (Iterator<Fireball> it = fireballs.iterator(); it.hasNext(); ) {
            Fireball fb = it.next();
            fb.update();

            boolean hitEnemy = false;
            for (Iterator<Enemy> it2 = enemies.iterator(); it2.hasNext(); ) {
                Enemy enemy = it2.next();
                if (fb.getBounds().intersects(enemy.getBounds())) {
                    enemy.defeat();
                    score += 200;
                    it2.remove();
                    fb.setActive(false);
                    hitEnemy = true;
                    break;
                }
            }

            if (!fb.isActive() || fb.getX() < cameraX - 50 || fb.getX() > cameraX + WIDTH + 50) {
                it.remove();
            }
        }

        cameraX = Math.max(0, Math.min(player.getX() - WIDTH / 3, levelWidth - WIDTH));

        if (player.getY() > HEIGHT + 100) {
            player.hit();
            if (player.getLives() <= 0) {
                gameState = GameState.GAMEOVER;
            } else {
                player.respawn();
                cameraX = 0;
            }
        }

        if (player.getX() >= levelWidth - 50) {
            gameState = GameState.WIN;
        }
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawBackground(g2d);

        g2d.translate(-cameraX, 0);

        for (Platform platform : platforms) {
            platform.draw(g2d);
        }

        for (Coin coin : coins) {
            coin.draw(g2d);
        }

        for (Enemy enemy : enemies) {
            enemy.draw(g2d);
        }

        for (Fireball fb : fireballs) {
            fb.draw(g2d);
        }

        player.draw(g2d);

        g2d.translate(cameraX, 0);

        drawHUD(g2d);

        drawGameState(g2d);
    }

    private void drawBackground(Graphics2D g2d) {
        g2d.setColor(new Color(107, 140, 255));
        g2d.fillRect(0, 0, WIDTH, HEIGHT);

        g2d.setColor(new Color(180, 200, 255));
        for (int i = 0; i < 20; i++) {
            int x = (i * 100 - cameraX / 3) % (WIDTH + 200) - 100;
            int y = 50 + (i % 3) * 40;
            g2d.fillOval(x, y, 30, 20);
        }

        if (player.getY() > 300) {
            g2d.setColor(new Color(139, 90, 43, 100));
            g2d.fillRect(0, 0, WIDTH, 50);
        }
    }

    private void drawHUD(Graphics2D g2d) {
        g2d.setColor(Color.BLACK);
        g2d.fillRect(5, 5, 150, 30);
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 18));
        g2d.drawString("Score: " + score, 15, 28);

        g2d.setColor(Color.BLACK);
        g2d.fillRect(WIDTH - 110, 5, 105, 30);
        g2d.setColor(Color.WHITE);
        g2d.drawString("Lives: " + player.getLives(), WIDTH - 100, 28);
    }

    private void drawGameState(Graphics2D g2d) {
        if (gameState == GameState.START) {
            g2d.setColor(new Color(0, 0, 0, 180));
            g2d.fillRect(0, 0, WIDTH, HEIGHT);

            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 48));
            g2d.drawString("MARIO GAME", WIDTH / 2 - 150, HEIGHT / 2 - 50);

            g2d.setFont(new Font("Arial", Font.PLAIN, 20));
            g2d.drawString("Controls:", WIDTH / 2 - 50, HEIGHT / 2 + 20);
            g2d.drawString("Arrow Keys - Move", WIDTH / 2 - 80, HEIGHT / 2 + 50);
            g2d.drawString("Space - Jump", WIDTH / 2 - 60, HEIGHT / 2 + 80);
            g2d.drawString("X - Shoot Fireball", WIDTH / 2 - 85, HEIGHT / 2 + 110);

            g2d.setFont(new Font("Arial", Font.BOLD, 24));
            g2d.drawString("Press ENTER to Start", WIDTH / 2 - 130, HEIGHT / 2 + 160);
        }

        if (gameState == GameState.GAMEOVER) {
            g2d.setColor(new Color(0, 0, 0, 180));
            g2d.fillRect(0, 0, WIDTH, HEIGHT);

            g2d.setColor(Color.RED);
            g2d.setFont(new Font("Arial", Font.BOLD, 48));
            g2d.drawString("GAME OVER", WIDTH / 2 - 130, HEIGHT / 2 - 20);

            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.PLAIN, 24));
            g2d.drawString("Final Score: " + score, WIDTH / 2 - 80, HEIGHT / 2 + 30);

            g2d.setFont(new Font("Arial", Font.BOLD, 20));
            g2d.drawString("Press ENTER to Restart", WIDTH / 2 - 120, HEIGHT / 2 + 80);
         }

        if (gameState == GameState.WIN) {
            g2d.setColor(new Color(0, 0, 0, 180));
            g2d.fillRect(0, 0, WIDTH, HEIGHT);

            g2d.setColor(Color.GREEN);
            g2d.setFont(new Font("Arial", Font.BOLD, 48));
            g2d.drawString("YOU WIN!", WIDTH / 2 - 100, HEIGHT / 2 - 20);

            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.PLAIN, 24));
            g2d.drawString("Final Score: " + score, WIDTH / 2 - 80, HEIGHT / 2 + 30);

            g2d.setFont(new Font("Arial", Font.BOLD, 20));
            g2d.drawString("Press ENTER to Play Again", WIDTH / 2 - 130, HEIGHT / 2 + 80);
        }
      
    }

    private enum GameState {
        START, PLAYING, GAMEOVER, WIN
    }

}
