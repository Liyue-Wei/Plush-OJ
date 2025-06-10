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

        final JFrame frame = new JFrame();
        frame.setTitle("Plush::OJ Dashboard"); // 新增視窗名稱
        frame.setUndecorated(true); // 無邊框
        frame.setSize(frameWidth, frameHeight);
        frame.setLocationRelativeTo(null);

        BufferedImage finalLogoImg = logoImg; // for lambda use

        final int[] breathTick = {0}; // 呼吸動畫計時

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;

                // ====== 抗鋸齒與高品質渲染 ======
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
                g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
                g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
                g2.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
                // ====== END 抗鋸齒與高品質渲染 ======

                int w = getWidth();
                int h = getHeight();

                // ====== 呼吸光暈效果 ======
                int glowWidth = 32; // 光暈寬度
                // 計算呼吸動畫的 alpha 變化 (0.5~1.0)
                double breath = 0.5 + 0.5 * Math.sin(breathTick[0] * 0.05);
                for (int i = glowWidth; i > 0; i--) {
                    float alpha = (float)(breath * 0.18f * (1f - (float)i / glowWidth));
                    g2.setColor(new Color(255, 255, 255, Math.round(255 * alpha)));
                    g2.setStroke(new BasicStroke(i * 2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.drawRect(i, i, w - i * 2, h - i * 2);
                }
                // ====== END 呼吸光暈效果 ======

                // ====== 斜邊光暈效果 ======
                int sideGlowWidth = 44;
                for (int i = sideGlowWidth; i > 0; i--) {
                    float alpha = (float)(breath * 0.18f * (1f - (float)i / sideGlowWidth));
                    // 左上斜邊光暈
                    Polygon leftGlow = new Polygon();
                    leftGlow.addPoint(0, 0);
                    leftGlow.addPoint(cut + i * 2, 0);
                    leftGlow.addPoint(0, cut + i * 2);
                    g2.setColor(new Color(255, 255, 255, Math.round(255 * alpha)));
                    g2.fillPolygon(leftGlow);

                    // 右下斜邊光暈
                    Polygon rightGlow = new Polygon();
                    rightGlow.addPoint(w, h);
                    rightGlow.addPoint(w - cut - i * 2, h);
                    rightGlow.addPoint(w, h - cut - i * 2);
                    g2.setColor(new Color(255, 255, 255, Math.round(255 * alpha)));
                    g2.fillPolygon(rightGlow);
                }
                // ====== END 斜邊光暈效果 ======

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
        panel.setLayout(null); // 設定為絕對定位

        // 呼吸動畫 Timer
        new javax.swing.Timer(2, e -> {
            breathTick[0]++;
            panel.repaint();
        }).start();

        // ====== 新增科技感的關閉與最小化按鈕 ======
        int btnSize = 36;
        int btnMargin = 18;

        // 自訂科技感按鈕
        class TechButton extends JButton {
            private final Color glowColor, normalColor, hoverColor;
            private boolean hover = false;
            public TechButton(String text, Color normal, Color hover, Color glow) {
                super(text);
                this.normalColor = normal;
                this.hoverColor = hover;
                this.glowColor = glow;
                setFocusPainted(false);
                setBorderPainted(false);
                setContentAreaFilled(false);
                setOpaque(false);
                setFont(new Font("Consolas", Font.BOLD, 24));
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                setForeground(normalColor);
                addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseEntered(java.awt.event.MouseEvent evt) {
                        TechButton.this.hover = true; TechButton.this.repaint();
                    }
                    public void mouseExited(java.awt.event.MouseEvent evt) {
                        TechButton.this.hover = false; TechButton.this.repaint();
                    }
                });
            }
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int arc = 18;
                // 半透明底
                g2.setColor(new Color(30, 30, 30, hover ? 180 : 120));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
                // 發光外框
                if (hover) {
                    g2.setColor(glowColor);
                    g2.setStroke(new BasicStroke(3f));
                    g2.drawRoundRect(2, 2, getWidth()-4, getHeight()-4, arc, arc);
                }
                // 文字
                g2.setColor(hover ? hoverColor : normalColor);
                FontMetrics fm = g2.getFontMetrics();
                int tx = (getWidth() - fm.stringWidth(getText())) / 2;
                int ty = (getHeight() + fm.getAscent()) / 2 - 2;
                g2.drawString(getText(), tx, ty);
                g2.dispose();
            }
        }

        // 關閉鈕
        TechButton closeBtn = new TechButton("x",
            new Color(255, 80, 80), new Color(255, 180, 180), new Color(255, 0, 0, 120));
        closeBtn.setToolTipText("close");
        closeBtn.setBounds(frameWidth - btnSize - btnMargin, btnMargin, btnSize, btnSize);
        closeBtn.addActionListener(e -> System.exit(0));

        // 最小化鈕
        TechButton minBtn = new TechButton("-",
            new Color(80, 200, 255), new Color(180, 255, 255), new Color(0, 255, 255, 120));
        minBtn.setToolTipText("minimize");
        minBtn.setBounds(frameWidth - btnSize * 2 - btnMargin - 8, btnMargin, btnSize, btnSize);
        minBtn.addActionListener(e -> frame.setState(JFrame.ICONIFIED));

        panel.add(closeBtn);
        panel.add(minBtn);
        // ====== END ======

        frame.setContentPane(panel);
        frame.setBackground(new Color(0,0,0,0)); // 讓JFrame背景也透明
        frame.setVisible(true);
    }
}
