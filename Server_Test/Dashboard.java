import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;

public class Dashboard {
    @SuppressWarnings("UseSpecificCatch")
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
        new javax.swing.Timer(1, e -> {
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
                    @SuppressWarnings("override")
                    public void mouseEntered(java.awt.event.MouseEvent evt) {
                        TechButton.this.hover = true; TechButton.this.repaint();
                    }
                    @SuppressWarnings("override")
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

        // ====== 新增五個科技感按鈕 ======
        int btnW = 180, btnH = 36, btnGap = 16;
        int btnStartX = 60, btnY = frameHeight - 355; // 位置可依需求調整

        TechButton[] funcBtns = new TechButton[6];
        String[] btnNames = {"Run Server", "NAT ", "Bridge", "AIP", "POST", "Toolbox"};
        Color[] btnColors = {
            new Color(255, 255, 255, 200), // 白色帶透明
            new Color(220, 220, 220, 180), // 淺灰
            new Color(200, 200, 200, 180), // 灰
            new Color(180, 180, 180, 180), // 深灰
            new Color(230, 230, 230, 180), // 淺灰
            new Color(210, 210, 210, 180)  // 灰
        };
        Color[] btnGlow = {
            new Color(255, 255, 255, 100), // 白色發光
            new Color(220, 220, 220, 80),  // 灰色發光
            new Color(200, 200, 200, 80),
            new Color(180, 180, 180, 80),
            new Color(230, 230, 230, 80),
            new Color(210, 210, 210, 80)
        };
        String[] btnTips = {
            "啟動伺服器",
            "Network Address Translation",
            "Bridge Network",
            "Automatic Installation Procedure",
            "Power On Self Test",
            "工具箱"
        };
        for (int i = 0; i < 6; i++) {
            funcBtns[i] = new TechButton(btnNames[i], btnColors[i], btnColors[i].brighter(), btnGlow[i]);
            funcBtns[i].setBounds(btnStartX + i * (btnW + btnGap), btnY, btnW, btnH);
            funcBtns[i].setToolTipText(btnTips[i]); // 個別描述
            panel.add(funcBtns[i]);
        }

        // 1. 斜角外框
        JComponent bashBorder = new JComponent() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                int cut = 36;
                int w = getWidth(), h = getHeight();
                int t = 2;
                g2.setStroke(new BasicStroke(t));
                g2.setColor(new Color(219, 219, 219));
                Polygon border = new Polygon();
                border.addPoint(cut, 0);
                border.addPoint(w, 0);
                border.addPoint(w, h - cut);
                border.addPoint(w - cut, h);
                border.addPoint(0, h);
                border.addPoint(0, cut);
                border.addPoint(cut, 0);
                g2.drawPolygon(border);
                g2.dispose();
            }
        };
        // ====【調整位置區域】====
        bashBorder.setBounds(60, frameHeight - 305, frameWidth - 120, 200);
        // ====【調整位置區域】====
        bashBorder.setOpaque(false);
        panel.add(bashBorder);

        // 2. 透明多行文字框（縮小）
        JTextArea bashTextArea = new JTextArea();
        bashTextArea.setFont(new Font("Consolas", Font.PLAIN, 18));
        bashTextArea.setBackground(new Color(30, 30, 30, 0)); // 完全透明
        bashTextArea.setForeground(new Color(0, 255, 128));
        bashTextArea.setCaretColor(new Color(0, 255, 128));
        bashTextArea.setBorder(BorderFactory.createEmptyBorder());
        bashTextArea.setLineWrap(true);
        bashTextArea.setWrapStyleWord(true);

        int margin = 32;
        // ====【調整位置區域】====
        int bashBoxX = 60 + margin;
        int bashBoxY = frameHeight - 305 + margin;
        int bashBoxW = frameWidth - 120 - margin * 2;
        int bashBoxH = 200 - margin * 2;
        // ====【調整位置區域】====

        JScrollPane bashScrollPane = new JScrollPane(bashTextArea);
        bashScrollPane.setBounds(bashBoxX, bashBoxY, bashBoxW, bashBoxH);
        bashScrollPane.setBorder(BorderFactory.createEmptyBorder());
        bashScrollPane.setOpaque(false); // 讓 JScrollPane 透明
        bashScrollPane.getViewport().setOpaque(false); // 讓 Viewport 也透明
        panel.add(bashScrollPane);

        bashTextArea.getInputMap().put(KeyStroke.getKeyStroke("ctrl ENTER"), "submitBash");
        bashTextArea.getActionMap().put("submitBash", new AbstractAction() {
            @Override
            @SuppressWarnings("UseSpecificCatch")
            public void actionPerformed(ActionEvent e) {
                String cmd = bashTextArea.getText();
                try {
                    // 執行 Windows cmd 指令，若要 bash 請改為 "bash", "-c", cmd
                    Process process = new ProcessBuilder(
                        "cmd", "/c", "chcp 65001 >nul && set LANG=en_US && " + cmd
                    ).redirectErrorStream(true).start();
                java.util.Scanner s = new java.util.Scanner(process.getInputStream(), "UTF-8").useDelimiter("\\A");
                    String output = s.hasNext() ? s.next() : "";
                    bashTextArea.setText(output);
                } catch (Exception ex) {
                    bashTextArea.setText("執行錯誤: " + ex.getMessage());
                }
            }
        });

        // ====== cmd 模擬區 ======
        String userDir = System.getProperty("user.dir");
        String prompt = userDir + ">";
        bashTextArea.setText(prompt);

        // 記錄 prompt 長度，防止刪除 prompt
        bashTextArea.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                int caret = bashTextArea.getCaretPosition();
                // 禁止刪除 prompt
                if ((e.getKeyCode() == java.awt.event.KeyEvent.VK_BACK_SPACE ||
                     e.getKeyCode() == java.awt.event.KeyEvent.VK_DELETE) &&
                    caret <= bashTextArea.getText().lastIndexOf(prompt) + prompt.length()) {
                    e.consume();
                }
                // Home 鍵跳到 prompt 後
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_HOME) {
                    int p = bashTextArea.getText().lastIndexOf(prompt) + prompt.length();
                    bashTextArea.setCaretPosition(p);
                    e.consume();
                }
            }
        });

        bashTextArea.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "submitBash");
        bashTextArea.getActionMap().put("submitBash", new AbstractAction() {
            @Override
            @SuppressWarnings("UseSpecificCatch")
            public void actionPerformed(ActionEvent e) {
                String text = bashTextArea.getText();
                int lastPrompt = text.lastIndexOf(prompt);
                if (lastPrompt == -1) return;
                String cmd = text.substring(lastPrompt + prompt.length()).trim();
                if (cmd.isEmpty()) {
                    bashTextArea.append("\n" + prompt);
                    bashTextArea.setCaretPosition(bashTextArea.getText().length());
                    return;
                }
                // 處理 cls 指令
                if (cmd.equalsIgnoreCase("cls")) {
                    bashTextArea.setText(prompt);
                    bashTextArea.setCaretPosition(bashTextArea.getText().length());
                    return;
                }
                // 開新執行緒執行指令，避免 UI 卡住
                new Thread(() -> {
                    try {
                        // 強制 powershell 輸出英文與 UTF-8
                        String psCmd = "$OutputEncoding = [Console]::OutputEncoding = [Text.Encoding]::UTF8; $env:LANG='en_US'; " + cmd;
                        Process process = new ProcessBuilder("powershell", "-Command", psCmd)
                            .redirectErrorStream(true).start();
                        java.io.InputStreamReader isr = new java.io.InputStreamReader(
                            process.getInputStream(), java.nio.charset.StandardCharsets.UTF_8);
                        java.util.Scanner s = new java.util.Scanner(isr).useDelimiter("\\A");
                        String output = s.hasNext() ? s.next() : "";
                        SwingUtilities.invokeLater(() -> {
                            bashTextArea.append("\n" + output + prompt);
                            bashTextArea.setCaretPosition(bashTextArea.getText().length());
                        });
                    } catch (Exception ex) {
                        SwingUtilities.invokeLater(() -> {
                            bashTextArea.append("\n執行錯誤: " + ex.getMessage() + "\n" + prompt);
                            bashTextArea.setCaretPosition(bashTextArea.getText().length());
                        });
                    }
                }).start();
            }
        });
        
        // 例如放在 bash 區塊左側，垂直排列
        int inputLabelW = 160, inputH = 32, inputGap = 18;
        int inputStartX = 80, inputStartY = frameHeight - 575; // 可依需求調整

        String[] inputLabels = {"Port Number : ", "AI Server URL : ", "Model : ", "Cluster Address : "};
        String[] inputDefaults = {"8080", "http://localhost:11434/api/generate", "deepseek-coder:6.7b", "192.168.1.1"};
        JTextField[] inputFields = new JTextField[4];

        for (int i = 0; i < 4; i++) {
            JLabel label = new JLabel(inputLabels[i]);
            label.setForeground(Color.WHITE);
            label.setFont(new Font("微軟正黑體", Font.BOLD, 18));
            label.setBounds(inputStartX, inputStartY + i * (inputH + inputGap), inputLabelW, inputH);
            panel.add(label);

            JTextField field = new JTextField();
            field.setFont(new Font("Consolas", Font.PLAIN, 18));
            field.setBounds(inputStartX + inputLabelW + 8, inputStartY + i * (inputH + inputGap), 360, inputH);
            field.setOpaque(false);
            field.setForeground(Color.WHITE);
            field.setCaretColor(Color.WHITE);
            field.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(180,180,180,180)));
            field.setText(inputDefaults[i]); // 設定預設值
            panel.add(field);

            inputFields[i] = field;
        }

        frame.setContentPane(panel);
        frame.setBackground(new Color(0,0,0,0)); // 讓JFrame背景也透明
        frame.setVisible(true);

        // ====== 右側系統監測區 ======
        int monitorBoxX = frameWidth - 620;
        int monitorBoxY = frameHeight - 580; 
        int monitorBoxW = 560;
        int monitorBoxH = 200;

        JLabel cpuLabel = new JLabel("CPU: 0%");
        cpuLabel.setForeground(new Color(80, 255, 80));
        cpuLabel.setFont(new Font("Consolas", Font.BOLD, 18));
        cpuLabel.setBounds(monitorBoxX, monitorBoxY, monitorBoxW, 32);
        panel.add(cpuLabel);

        JLabel ramLabel = new JLabel("RAM: 0 MB / 0 MB");
        ramLabel.setForeground(new Color(80, 200, 255));
        ramLabel.setFont(new Font("Consolas", Font.BOLD, 18));
        ramLabel.setBounds(monitorBoxX, monitorBoxY + 38, monitorBoxW, 32);
        panel.add(ramLabel);

        JLabel netLabel = new JLabel("Net: 0 KB/s ↑  0 KB/s ↓");
        netLabel.setForeground(new Color(255, 220, 80));
        netLabel.setFont(new Font("Consolas", Font.BOLD, 18));
        netLabel.setBounds(monitorBoxX, monitorBoxY + 76, monitorBoxW, 32);
        panel.add(netLabel);

        // ====== 歷史數據緩衝區 ======
        final int HISTORY = 60;
        java.util.List<Double> cpuHistory = new java.util.LinkedList<>();
        java.util.List<Double> ramHistory = new java.util.LinkedList<>();
        java.util.List<Double> netHistory = new java.util.LinkedList<>();

        // ====== 圖表面板 ======
        JPanel chartPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                int w = getWidth(), h = getHeight();
                // 背景
                g2.setColor(new Color(30,30,30,120));
                g2.fillRect(0, 0, w, h);

                // 畫格線
                g2.setColor(new Color(80,80,80,120));
                for(int i=1;i<6;i++) {
                    int y = h*i/6;
                    g2.drawLine(0, y, w, y);
                }

                // 畫 CPU 曲線
                g2.setColor(new Color(80,255,80));
                drawLine(g2, cpuHistory, w, h, 100);

                // 畫 RAM 曲線
                g2.setColor(new Color(80,200,255));
                drawLine(g2, ramHistory, w, h, 100);

                // 畫 Net 曲線
                g2.setColor(new Color(255,220,80));
                drawLine(g2, netHistory, w, h, 100);
            }
            private void drawLine(Graphics2D g2, java.util.List<Double> data, int w, int h, double max) {
                if(data.size()<2) return;
                int n = data.size();
                int prevX = 0, prevY = h - (int)(data.get(0)/max*h);
                for(int i=1;i<n;i++) {
                    int x = i*w/(HISTORY-1);
                    int y = h - (int)(data.get(i)/max*h);
                    g2.drawLine(prevX, prevY, x, y);
                    prevX = x; prevY = y;
                }
            }
        };
        // 調整圖表位置與寬度，避免壓到下方按鈕
        chartPanel.setBounds(monitorBoxX, monitorBoxY - 100 + monitorBoxH + 10, monitorBoxW, 80);
        chartPanel.setOpaque(false);
        panel.add(chartPanel);

        // ====== 監測數據更新 Timer ======
        final long[] lastNetBytes = {0};
        final long[] lastNetTime = {System.currentTimeMillis()};
        new javax.swing.Timer(250, e -> {
            // CPU
            double cpuLoad = 0;
            try {
                com.sun.management.OperatingSystemMXBean osBean =
                    (com.sun.management.OperatingSystemMXBean) java.lang.management.ManagementFactory.getOperatingSystemMXBean();
                // 优先使用 getCpuLoad()，如果不可用则回退
                try {
                    cpuLoad = osBean.getCpuLoad();
                } catch (NoSuchMethodError err) {
                    cpuLoad = osBean.getSystemCpuLoad(); // 兼容旧JDK
                }
                if (cpuLoad < 0) cpuLoad = 0;
            } catch (Exception ex) {}
            cpuLabel.setText(String.format("CPU: %.1f%%", cpuLoad * 100));

            // RAM
            long total = Runtime.getRuntime().totalMemory() / 1024 / 1024;
            long free = Runtime.getRuntime().freeMemory() / 1024 / 1024;
            long used = total - free;
            ramLabel.setText(String.format("RAM: %d MB / %d MB", used, total));

            // 網路（僅顯示本機流量，簡易版）
            long netSpeed = 0;
            try {
                @SuppressWarnings("unused")
                java.net.NetworkInterface ni = java.net.NetworkInterface.getNetworkInterfaces().nextElement();
                long bytes = 0;
                for (java.util.Enumeration<java.net.NetworkInterface> en = java.net.NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                    java.net.NetworkInterface nif = en.nextElement();
                    if (nif.isUp() && !nif.isLoopback()) {
                        for (java.util.Enumeration<java.net.NetworkInterface> sub = nif.getSubInterfaces(); sub.hasMoreElements();) {
                            java.net.NetworkInterface subNif = sub.nextElement();
                            bytes += subNif.getMTU(); // 這裡僅為示意，真實流量需用第三方庫
                        }
                    }
                }
                long now = System.currentTimeMillis();
                netSpeed = (bytes - lastNetBytes[0]) * 1000 / (now - lastNetTime[0] + 1);
                lastNetBytes[0] = bytes;
                lastNetTime[0] = now;
            } catch (Exception ex) {}
            netLabel.setText("Net: " + netSpeed + " KB/s ↑  0 KB/s ↓");

            // 更新歷史數據
            if (cpuHistory.size() >= HISTORY) cpuHistory.remove(0);
            cpuHistory.add(cpuLoad * 100);
            if (ramHistory.size() >= HISTORY) ramHistory.remove(0);
            ramHistory.add((double)used * 100 / (total == 0 ? 1 : total));
            if (netHistory.size() >= HISTORY) netHistory.remove(0);
            netHistory.add((double)netSpeed);

            chartPanel.repaint();
        }).start();

        // 在 main 裡 LOGO 畫完後加上這段
        int logoBtnX = 10;
        int logoBtnY = frameHeight - logoHeight - 10;
        JButton logoBtn = new JButton();
        logoBtn.setBounds(logoBtnX, logoBtnY, logoWidth, logoHeight);
        logoBtn.setOpaque(false);
        logoBtn.setContentAreaFilled(false);
        logoBtn.setBorderPainted(false);
        logoBtn.setFocusPainted(false);
        logoBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logoBtn.setToolTipText("前往 Plush-OJ GitHub");
        logoBtn.addActionListener(e -> {
            try {
                java.awt.Desktop.getDesktop().browse(new java.net.URI("https://github.com/Liyue-Wei/Plush-OJ"));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "無法開啟瀏覽器: " + ex.getMessage());
            }
        });
        panel.add(logoBtn);

        funcBtns[0].addActionListener(e -> {
            try {
                // 用 start 讓 bat 在新視窗獨立執行
                ProcessBuilder pb = new ProcessBuilder(
                    "cmd", "/c", "start", "\"runServer\"", "C:\\Users\\eric2\\Desktop\\Plush-OJ\\Server_Test\\runServer.bat"
                );
                pb.directory(new File("C:\\Users\\eric2\\Desktop\\Plush-OJ\\Server_Test"));
                pb.start();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "無法啟動伺服器: " + ex.getMessage());
            }
        });

        String[] toolboxBtnNames = {"桌宠", "更新?", "坤球", "这啥?"};
        // Toolbox 彈窗
        funcBtns[5].addActionListener(e -> {
            JDialog dialog = new JDialog(frame, "Toolbox", true);
            dialog.setSize(400, 220);
            dialog.setUndecorated(true); // 無邊框
            dialog.setLayout(null);
            dialog.setLocationRelativeTo(frame);
            dialog.setBackground(new Color(0,0,0,0)); // 讓JDialog背景真正透明

            // 自訂面板做斜切與半透明背景
            JPanel content = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    int w = getWidth(), h = getHeight();
                    int cut = 40; // 斜切長度

                    // 半透明深色背景
                    g2.setColor(new Color(40, 40, 40, 230));
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
                }
            };
            content.setOpaque(false); // 讓JPanel也透明
            content.setLayout(null);
            dialog.setContentPane(content);

            // ====== 中間對齊設定 ======
            int sub_paddingLeft = 48; // 左側 padding
            int sub_contentWidth = 400 - sub_paddingLeft * 2;

            // 標題（綠色）
            JLabel sub_title = new JLabel("神奇妙妙工具", SwingConstants.CENTER);
            sub_title.setForeground(new Color(80, 255, 80));
            sub_title.setFont(new Font("Microsoft YaHei", Font.BOLD, 26));
            sub_title.setBounds(sub_paddingLeft, 22, sub_contentWidth, 36);
            content.add(sub_title);

            // 四個功能按鈕（綠色系）
            int sub_btnW = 120, sub_btnH = 40, sub_btnGapX = 24, sub_btnGapY = 18;
            int sub_btnStartX = sub_paddingLeft + (sub_contentWidth - sub_btnW * 2 - sub_btnGapX) / 2;
            int sub_btnStartY = 70;
            for (int i = 0; i < 4; i++) {
                JButton btn = new JButton(toolboxBtnNames[i]);
                btn.setFont(new Font("Microsoft YaHei", Font.BOLD, 18));
                btn.setForeground(new Color(80, 255, 80));
                btn.setBackground(new Color(30, 50, 30, 220));
                btn.setFocusPainted(false);
                btn.setBorder(BorderFactory.createLineBorder(new Color(80, 255, 80, 180), 2, true));
                int x = sub_btnStartX + (i % 2) * (sub_btnW + sub_btnGapX);
                int y = sub_btnStartY + (i / 2) * (sub_btnH + sub_btnGapY);
                btn.setBounds(x, y, sub_btnW, sub_btnH);
                content.add(btn);
            }

            // 關閉鈕（右上角，綠色風格）
            TechButton closeBtn_sub = new TechButton("x",
                new Color(80, 255, 80), new Color(180, 255, 180), new Color(0, 255, 128, 120));
            closeBtn_sub.setToolTipText("close");
            closeBtn_sub.setBounds(350, 18, 36, 36);
            closeBtn_sub.addActionListener(ev -> dialog.dispose());
            content.add(closeBtn_sub);

            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setVisible(true);
        });
    }
}
