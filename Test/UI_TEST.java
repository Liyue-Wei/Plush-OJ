import java.awt.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;

public class UI_TEST {
    public static void main(String[] args) {
        // 可調整參數
        int frameWidth = 1280;
        int frameHeight = 720;
        int cut = 140; // 斜切長度

        // 背景顏色 (RGBA)
        int r = 46, g = 46, b = 46, a = 230;
        Color bgColor = new Color(r, g, b, a);

        // LOGO 相關參數
        String logoPath = "C:\\Users\\eric2\\Desktop\\Plush-OJ\\Test\\LOGO.png";
        int logoWidth = 309;   // LOGO寬度，可自行調整
        int logoHeight = 60;  // LOGO高度，可自行調整

        // 載入LOGO圖片
        BufferedImage logoImg = null;
        try {
            logoImg = ImageIO.read(new File(logoPath));
        } catch (IOException e) {
            System.err.println("LOGO 載入失敗: " + e.getMessage());
        }

        JFrame frame = new JFrame();
        frame.setUndecorated(true); // 無邊框
        frame.setSize(frameWidth, frameHeight);
        frame.setLocationRelativeTo(null);

        BufferedImage finalLogoImg = logoImg; // for lambda use

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                int w = getWidth();
                int h = getHeight();

                // 啟用抗鋸齒與高品質縮放
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
                g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
                g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
                g2.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);

                // 填滿背景色
                g2.setColor(bgColor);
                g2.fillRect(0, 0, w, h);

                // 左上斜切（透明）
                Polygon leftTop = new Polygon();
                leftTop.addPoint(0, 0);
                leftTop.addPoint(cut, 0);
                leftTop.addPoint(0, cut);
                g2.setComposite(AlphaComposite.Clear);
                g2.fillPolygon(leftTop);
                g2.setComposite(AlphaComposite.SrcOver);

                // 右下斜切（透明）
                Polygon rightBottom = new Polygon();
                rightBottom.addPoint(w, h);
                rightBottom.addPoint(w - cut, h);
                rightBottom.addPoint(w, h - cut);
                g2.setComposite(AlphaComposite.Clear);
                g2.fillPolygon(rightBottom);
                g2.setComposite(AlphaComposite.SrcOver);

                // 畫LOGO在左下角
                if (finalLogoImg != null) {
                    int x = 10;
                    int y = h - logoHeight - 10;
                    g2.drawImage(finalLogoImg, x, y, logoWidth, logoHeight, null);
                }
            }
        };
        panel.setBackground(new Color(0,0,0,0)); // 透明背景
        frame.setContentPane(panel);
        frame.setBackground(new Color(0,0,0,0)); // 讓JFrame背景也透明
        frame.setVisible(true);
    }
}
