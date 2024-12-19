package game;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class SnakeGame extends JPanel implements ActionListener, KeyListener {
    // Lop dai dien cho o vuong (Tile) cua ran va do an
    // Represents a tile for snake and food
    private class Tile {
        int x; // Vi tri x tren ban do (Position x on the board)
        int y; // Vi tri y tren ban do (Position y on the board)

        Tile(int x, int y) {
            this.x = x;
            this.y = x;
        }
    }

    // Kich thuoc ban do (Board dimensions)
    int boardWidth;
    int boardHeight;
    int tileSize = 25; // Kich thuoc cua mot o vuong (Size of each tile)

    // Ran (Snake)
    Tile snakeHead; // Dau cua ran (Head of the snake)
    ArrayList<Tile> snakeBody; // Than cua ran (Body of the snake)

    // Do an (Food)
    Tile food; // Vi tri cua do an (Position of the food)
    Random random; // Tao so ngau nhien (Random number generator)

    // Logic cho tro choi (Game logic)
    int velocityX; // Toc do di chuyen theo truc X (Speed along X-axis)
    int velocityY; // Toc do di chuyen theo truc Y (Speed along Y-axis)
    Timer gameLoop; // Vong lap cua tro choi (Game loop timer)

    boolean gameOver = false; // Trang thai ket thuc tro choi (Game over state)
    int score = 0; // Diem so hien tai (Current score)
    int highScore = 0; // Diem cao nhat (High score)
    boolean gameStarted = false; // Kiem tra tro choi da duoc bat dau (Check if game has started)
    boolean autoPlay = false; // Che do tu dong choi (Auto-play mode)

    // Khoi tao tro choi (Constructor for the game)
    SnakeGame(int boardWidth, int boardHeight) {
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
        setPreferredSize(new Dimension(this.boardWidth, this.boardHeight)); // Thiet lap kich thuoc cua khung (Set panel size)
        setBackground(Color.black); // Mau nen cua tro choi (Background color)
        addKeyListener(this); // Them lang nghe su kien ban phim (Add key listener)
        setFocusable(true);

        // Hien thong bao va yeu cau xac nhan truoc khi bat dau tro choi (Show message and require confirmation before starting game)
        int confirm = JOptionPane.showConfirmDialog(this, "Welcome to Snake Game!\nPress OK to start the game.", "Game Instructions", JOptionPane.OK_CANCEL_OPTION);
        if (confirm == JOptionPane.OK_OPTION) {
            gameStarted = true;
            gameLoop = new Timer(100, this);
            resetGame();
        } else {
            System.exit(0); // Thoat tro choi neu nguoi dung khong xac nhan (Exit game if user does not confirm)
        }
    }

    // Dat lai trang thai tro choi (Reset game state)
    private void resetGame() {
        snakeHead = new Tile(5, 5); // Vi tri bat dau cua dau ran (Initial head position)
        snakeBody = new ArrayList<>(); // Khoi tao danh sach than ran (Initialize snake body)
        food = new Tile(10, 10); // Vi tri ban dau cua do an (Initial food position)
        random = new Random(); // Khoi tao bo sinh so ngau nhien (Initialize random generator)
        placeFood(); // Dat do an moi (Place food)
        velocityX = 1; // Huong di chuyen ban dau (Initial movement direction)
        velocityY = 0;
        gameOver = false; // Trang thai ket thuc tro choi mac dinh (Default game over state)
        score = 0; // Dat lai diem so (Reset score)
        autoPlay = false; // Tat che do tu dong choi (Disable auto-play mode)
        gameLoop.start(); // Bat dau vong lap tro choi (Start game loop)
    }

    // Ve giao dien tro choi (Draw game interface)
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    // Ve cac thanh phan tren man hinh (Draw components on screen)
    public void draw(Graphics g) {
        // Ve cac duong luoi (Draw grid lines)
        g.setColor(Color.black);
        for (int i = 0; i < boardWidth / tileSize; i++) {
            g.drawLine(i * tileSize, 0, i * tileSize, boardHeight);
            g.drawLine(0, i * tileSize, boardWidth, i * tileSize);
        }

        // Ve do an (Draw food)
        g.setColor(Color.red);
        g.fill3DRect(food.x * tileSize, food.y * tileSize, tileSize, tileSize, true);

        // Ve dau ran (Draw snake head)
        g.setColor(Color.green);
        g.fill3DRect(snakeHead.x * tileSize, snakeHead.y * tileSize, tileSize, tileSize, true);

        // Ve than ran (Draw snake body)
        for (Tile snakePart : snakeBody) {
            g.setColor(snakeBody.size() >= 5 ? Color.cyan : Color.green); // Mau thay doi khi ran dai (Color changes with length)
            g.fill3DRect(snakePart.x * tileSize, snakePart.y * tileSize, tileSize, tileSize, true);
        }

        // Hien thi diem so va diem cao nhat (Display score and high score)
        g.setFont(new Font("Arial", Font.BOLD, 16));

        // Nhan diem so (Score label)
        g.setColor(Color.white);
        String scoreLabelText = "Score:";
        int scoreLabelX = boardWidth - 590;
        int scoreLabelY = 20;
        g.drawString(scoreLabelText, scoreLabelX, scoreLabelY);

        // Gia tri diem so (Score value)
        g.setColor(Color.blue);
        String scoreValueText = String.valueOf(score);
        int scoreValueX = scoreLabelX + g.getFontMetrics().stringWidth(scoreLabelText) + 10;
        g.drawString(scoreValueText, scoreValueX, scoreLabelY);

        // Nhan diem cao nhat (High score label)
        g.setColor(Color.white);
        String highScoreLabelText = "High Score:";
        int highScoreLabelX = boardWidth - 590;
        int highScoreLabelY = 40;
        g.drawString(highScoreLabelText, highScoreLabelX, highScoreLabelY);

        // Gia tri diem cao nhat (High score value)
        g.setColor(Color.white);
        String highScoreValueText = String.valueOf(highScore);
        int highScoreValueX = highScoreLabelX + g.getFontMetrics().stringWidth(highScoreLabelText) + 10;
        g.drawString(highScoreValueText, highScoreValueX, highScoreLabelY);

        // Hien thi ban quyen o day man hinh (Display copyright at the bottom)
        g.setFont(new Font("Arial", Font.PLAIN, 14));
        String copyrightText = "© nguyenquocvinh";
        int copyrightX = (boardWidth - g.getFontMetrics().stringWidth(copyrightText)) / 2;
        int copyrightY = boardHeight - 20;
        g.drawString(copyrightText, copyrightX, copyrightY);

        // Man hinh ket thuc tro choi (Game over screen)
        if (gameOver) {
            g.setColor(Color.red);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            String gameOverText1 = "Game Over! Press R to Restart";
            String gameOverText2 = "or H to Hack";
            int gameOverX1 = (boardWidth - g.getFontMetrics().stringWidth(gameOverText1)) / 2;
            int gameOverX2 = (boardWidth - g.getFontMetrics().stringWidth(gameOverText2)) / 2;
            int gameOverY1 = boardHeight / 2 - 15;
            int gameOverY2 = boardHeight / 2 + 15;
            g.drawString(gameOverText1, gameOverX1, gameOverY1);
            g.drawString(gameOverText2, gameOverX2, gameOverY2);
        }
    }

    // Dat vi tri moi cho do an (Place food in a new position)
    public void placeFood() {
        food.x = random.nextInt(boardWidth / tileSize);
        food.y = random.nextInt(boardHeight / tileSize);
    }

    // Di chuyen ran (Move the snake)
    public void move() {
        if (gameOver || !gameStarted) return;

        if (autoPlay) {
            // Che do tu dong choi (Auto-play mode logic)
            if (snakeHead.x < food.x) {
                velocityX = 1;
                velocityY = 0;
            } else if (snakeHead.x > food.x) {
                velocityX = -1;
                velocityY = 0;
            } else if (snakeHead.y < food.y) {
                velocityX = 0;
                velocityY = 1;
            } else if (snakeHead.y > food.y) {
                velocityX = 0;
                velocityY = -1;
            }
        }

        // An do an (Eat food)
        if (collision(snakeHead, food)) {
            snakeBody.add(new Tile(food.x, food.y));
            placeFood();
            score++;
            if (score > highScore) {
                highScore = score;
            }
        }

        // Di chuyen than ran (Move snake body)
        for (int i = snakeBody.size() - 1; i >= 0; i--) {
            Tile snakePart = snakeBody.get(i);
            if (i == 0) { // Gan dau ran (Right before the head)
                snakePart.x = snakeHead.x;
                snakePart.y = snakeHead.y;
            } else {
                Tile prevSnakePart = snakeBody.get(i - 1);
                snakePart.x = prevSnakePart.x;
                snakePart.y = prevSnakePart.y;
            }
        }

        // Di chuyen dau ran (Move snake head)
        snakeHead.x += velocityX;
        snakeHead.y += velocityY;

        // Dieu kien ket thuc tro choi (Game over conditions)
        for (Tile snakePart : snakeBody) {
            if (collision(snakeHead, snakePart)) {
                gameOver = true;
            }
        }

        if (snakeHead.x < 0 || snakeHead.x >= boardWidth / tileSize ||
                snakeHead.y < 0 || snakeHead.y >= boardHeight / tileSize) {
            gameOver = true;
        }
    }

    // Kiem tra va cham giua hai Tile (Check collision between two tiles)
    public boolean collision(Tile tile1, Tile tile2) {
        return tile1.x == tile2.x && tile1.y == tile2.y;
    }

    // Ham thuc thi moi khi Timer kich hoat (Method called on timer event)
    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver) {
            gameLoop.stop();
        }
    }

    // Xu ly su kien ban phim (Handle keyboard events)
    @Override
    public void keyPressed(KeyEvent e) {
        if (gameOver && e.getKeyCode() == KeyEvent.VK_R) {
            resetGame();
            repaint();
        } else if (gameOver && e.getKeyCode() == KeyEvent.VK_H) {
            autoPlay = true; // Bat che do tu dong choi (Enable auto-play mode)
            gameOver = false; // Huy trang thai ket thuc tro choi (Cancel game over state)
            gameLoop.start(); // Khoi dong lai vong lap tro choi (Restart game loop)
        } else if (!autoPlay) { // Bo qua dieu khien khi che do tu dong (Ignore controls in auto-play mode)
            if (e.getKeyCode() == KeyEvent.VK_UP && velocityY != 1) {
                velocityX = 0;
                velocityY = -1;
            } else if (e.getKeyCode() == KeyEvent.VK_DOWN && velocityY != -1) {
                velocityX = 0;
                velocityY = 1;
            } else if (e.getKeyCode() == KeyEvent.VK_LEFT && velocityX != 1) {
                velocityX = -1;
                velocityY = 0;
            } else if (e.getKeyCode() == KeyEvent.VK_RIGHT && velocityX != -1) {
                velocityX = 1;
                velocityY = 0;
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}
}
