package game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class App {
    public static void main(String[] args) {
        int boardWidth = 600;
        int boardHeight = 610;

        JFrame frame = new JFrame("GUI Project: Nguyen Quoc Vinh - Vinkay");
        frame.setSize(boardWidth, boardHeight + 50); // Extra space for banner
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Tạo banner panel
        JPanel bannerPanel = new JPanel();
        bannerPanel.setBackground(Color.BLACK); // Đặt màu nền banner thành đen
        JLabel bannerLabel = new JLabel("VINKAY SNAKE GAMES");
        bannerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        bannerLabel.setForeground(Color.WHITE); // Đặt màu chữ ban đầu
        bannerPanel.add(bannerLabel);
        frame.add(bannerPanel, BorderLayout.NORTH);

        // Hiệu ứng đổi màu chữ liên tục
        Timer colorTimer = new Timer(100, new ActionListener() {
            private int red = 255;
            private int green = 0;
            private int blue = 0;
            private int step = 5;

            @Override
            public void actionPerformed(ActionEvent e) {
                // Tính toán màu tiếp theo theo chu kỳ RGB
                if (red == 255 && green < 255 && blue == 0) {
                    green += step;
                } else if (green == 255 && red > 0 && blue == 0) {
                    red -= step;
                } else if (green == 255 && blue < 255 && red == 0) {
                    blue += step;
                } else if (blue == 255 && green > 0 && red == 0) {
                    green -= step;
                } else if (blue == 255 && red < 255 && green == 0) {
                    red += step;
                } else if (red == 255 && blue > 0 && green == 0) {
                    blue -= step;
                }

                // Cập nhật màu chữ
                bannerLabel.setForeground(new Color(red, green, blue));
            }
        });

        colorTimer.start();

        // Thêm game vào giao diện
        SnakeGame snakeGame = new SnakeGame(boardWidth, boardHeight);
        frame.add(snakeGame, BorderLayout.CENTER);

        frame.setVisible(true);
    }
}
