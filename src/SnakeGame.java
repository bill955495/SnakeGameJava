import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;
import java.util.Random;

public class SnakeGame {

    private JFrame frame;   // 遊戲窗口
    private SnakePanel snakePanel; // 貪食蛇的畫布
    private Snake snake;    // 貪食蛇
    private Food food;      // 食物
    private Timer timer;     // 定時器，用於定期更新畫面
    private boolean gameOver;  // 遊戲是否結束

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SnakeGame());
    }

    public SnakeGame() {
        frame = new JFrame("Snake Game");  // 創建 JFrame 對象，設置窗口標題
        frame.setSize(400, 400);            // 設置窗口大小
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // 設置窗口關閉操作
        frame.setResizable(false);         // 防止改變窗口大小

        snake = new Snake();                // 初始化蛇
        food = new Food();                  // 初始化食物
        snakePanel = new SnakePanel();      // 初始化畫布

        frame.add(snakePanel);              // 添加畫布到窗口
        frame.addKeyListener(new MyKeyListener());  // 添加按鍵監聽器
        frame.addMouseListener(new MyMouseListener()); // 添加滑鼠監聽器
        frame.setVisible(true);             // 顯示窗口

        startGame();  // 遊戲開始
    }

    // 遊戲開始
    private void startGame() {
        gameOver = false;
        snake.reset();   // 重置蛇的位置和方向
        food.generateFood();  // 生成食物
        timer = new Timer(100, new ActionListener() {  // 創建 Timer，每100毫秒執行一次 ActionListener
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!gameOver) {
                    snake.move();  // 定期更新遊戲，移動蛇
                    checkCollision(); // 檢查碰撞
                    snakePanel.repaint();  // 重新繪製畫布
                }
            }
        });
        timer.start();  // 啟動計時器，開始遊戲循環
    }

    // 檢查碰撞
    private void checkCollision() {
        Point head = snake.getBody().getFirst();

        // 檢查碰到牆
        if (head.x < 0 || head.x >= 20 || head.y < 0 || head.y >= 20) {
            gameOver();
        }

        // 檢查碰到自己
        for (int i = 1; i < snake.getBody().size(); i++) {
            if (head.equals(snake.getBody().get(i))) {
                gameOver();
                break;
            }
        }

        // 檢查是否吃到食物
        if (head.equals(food.getPosition())) {
            snake.eatFood();
            food.generateFood();
        }
    }

    // 結束遊戲
    private void gameOver() {
        gameOver = true;
        timer.stop();
        frame.repaint();  // 更新一次畫面以顯示 "重新開始"
    }

    // 貪食蛇的畫布
    private class SnakePanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            // 畫蛇的身體
            g.setColor(Color.GREEN);
            for (Point point : snake.getBody()) {
                g.fillRect(point.x * 20, point.y * 20, 20, 20);
            }

            // 畫食物
            g.setColor(Color.RED);
            g.fillRect(food.getPosition().x * 20, food.getPosition().y * 20, 20, 20);

            // 如果遊戲結束，在畫面中央顯示 "重新開始"
            if (gameOver) {
                g.setColor(Color.BLACK);
                g.setFont(new Font("Arial", Font.PLAIN, 20));
                String message = "Reset";
                int x = (getWidth() - g.getFontMetrics().stringWidth(message)) / 2;
                int y = getHeight() / 2;
                g.drawString(message, x, y);
            }
        }
    }

    // 食物類別
    private class Food {
        private Point position;  // 食物的位置

        public Food() {
            position = new Point(10, 10);  // 初始位置
        }

        public Point getPosition() {
            return position;
        }

        public void generateFood() {
            // 隨機生成新的食物位置，確保不超出畫布範圍且不在蛇的身體上
            Random random = new Random();
            int x, y;
            do {
                x = random.nextInt(19);  // 在0到19之間生成隨機數
                y = random.nextInt(19);
            } while (snake.getBody().contains(new Point(x, y)) || x < 0 || x >= 20 || y < 0 || y >= 20);
            position.setLocation(x, y);
        }
    }

    // 鍵盤監聽器
    private class MyKeyListener implements KeyListener {
        @Override
        public void keyPressed(KeyEvent e) {
            if (gameOver && e.getKeyCode() == KeyEvent.VK_ENTER) {
                startGame();  // 在遊戲結束後按 Enter 重新開始
            } else if (!gameOver) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                        if (snake.getDirection() != 3)
                            snake.setDirection(1);
                        break;
                    case KeyEvent.VK_DOWN:
                        if (snake.getDirection() != 1)
                            snake.setDirection(3);
                        break;
                    case KeyEvent.VK_LEFT:
                        if (snake.getDirection() != 0)
                            snake.setDirection(2);
                        break;
                    case KeyEvent.VK_RIGHT:
                        if (snake.getDirection() != 2)
                            snake.setDirection(0);
                        break;
                }
            }
        }

        @Override
        public void keyTyped(KeyEvent e) {}

        @Override
        public void keyReleased(KeyEvent e) {}
    }

    // 滑鼠監聽器
    private class MyMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (gameOver) {
                startGame();  // 在遊戲結束後點擊滑鼠重新開始
            }
        }
    }

    // 貪食蛇類別
    private class Snake {
        private LinkedList<Point> body;  // 蛇的身體
        private int direction;           // 蛇的運動方向

        public Snake() {
            body = new LinkedList<>();
            body.add(new Point(5, 5));  // 初始位置
            direction = 0;              // 初始方向為右
        }

        public LinkedList<Point> getBody() {
            return body;
        }

        public int getDirection() {
            return direction;
        }

        public void setDirection(int direction) {
            this.direction = direction;
        }

        public void move() {
            Point head = new Point(body.getFirst());  // 獲取蛇頭位置

            // 根據方向移動蛇頭
            switch (direction) {
                case 0:
                    head.x += 1;  // 向右移動
                    break;
                case 1:
                    head.y -= 1;  // 向上移動
                    break;
                case 2:
                    head.x -= 1;  // 向左移動
                    break;
                case 3:
                    head.y += 1;  // 向下移動
                    break;
            }

            // 添加新的蛇頭
            body.addFirst(head);

            // 移除尾部，保持蛇的長度
            body.removeLast();
        }

        public void eatFood() {
            // 在蛇尾部添加一個新的點，來模擬蛇吃到食物
            body.addLast(new Point(0, 0)); // 這裡可以是任何值，因為在下一個move時會更新位置
        }

        public void reset() {
            body.clear();
            body.add(new Point(5, 5));
            direction = 0;
        }
    }
}
