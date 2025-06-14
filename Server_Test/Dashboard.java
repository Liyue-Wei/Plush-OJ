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
        // 启动动画步骤
        String[] bootSteps = {"系统启动中...", "资料库启动中...", "网络服务启动中...", "Ollama自检中...", "FOFE FW自检中..."};
        JDialog bootDialog = new JDialog((Frame)null, true);
        bootDialog.setUndecorated(true);
        bootDialog.setSize(400, 120);
        bootDialog.setLocationRelativeTo(null);
        bootDialog.setBackground(new Color(0,0,0,0));

        JPanel bootPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(new Color(30, 30, 30, 230));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
            }
        };
        bootPanel.setOpaque(false);
        bootPanel.setLayout(null);

        JLabel bootLabel = new JLabel(bootSteps[0], SwingConstants.CENTER);
        bootLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 24));
        bootLabel.setForeground(new Color(80, 255, 80));
        bootLabel.setBounds(0, 30, 400, 40);
        bootPanel.add(bootLabel);

        bootDialog.setContentPane(bootPanel);

        // 定时切换步骤
        Timer bootTimer = new Timer(1250, null);
        final int[] step = {0};
        bootTimer.addActionListener(e3 -> {
            step[0]++;
            if (step[0] < bootSteps.length) {
                bootLabel.setText(bootSteps[step[0]]);
            } else {
                bootTimer.stop();
                bootDialog.dispose();
            }
        });
        bootTimer.setInitialDelay(0);
        bootTimer.start();
        bootDialog.setVisible(true);

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

        String[] toolboxBtnNames = {"欧润吉", "更新?", "坤球", "这啥?"};
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

                // 第二個按鈕（index 1）彈出 Windows 更新畫面
                if (i == 1) {
                    btn.addActionListener(ev -> {
                        JDialog updateDialog = new JDialog((JFrame)null, true);
                        updateDialog.setUndecorated(true);
                        updateDialog.setAlwaysOnTop(true);
                        updateDialog.setSize(Toolkit.getDefaultToolkit().getScreenSize());
                        updateDialog.setLocationRelativeTo(null);

                        // 隱藏系統游標
                        java.awt.Toolkit tk = java.awt.Toolkit.getDefaultToolkit();
                        java.awt.Image emptyImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
                        updateDialog.setCursor(tk.createCustomCursor(emptyImg, new Point(0, 0), "blank"));

                        // 用于旋转的角度变量
                        final int[] angle = {0};

                        JPanel updatePanel = new JPanel() {
                            @Override
                            protected void paintComponent(Graphics g) {
                                super.paintComponent(g);
                                Graphics2D g2 = (Graphics2D) g;
                                g2.setColor(new Color(0, 120, 215)); // Windows 10 藍
                                g2.fillRect(0, 0, getWidth(), getHeight());

                                // 画圆形 loading（会转）
                                int cx = getWidth() / 2, cy = getHeight() / 2 - 80;
                                g2.setColor(Color.WHITE);
                                for (int j = 0; j < 12; j++) {
                                    double angleRad = Math.toRadians((j + angle[0]) * 30);
                                    int r = 40, dotR = 8;
                                    int dx = (int) (Math.cos(angleRad) * r);
                                    int dy = (int) (Math.sin(angleRad) * r);
                                    float alpha = 0.2f + 0.8f * j / 12f;
                                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                                    g2.fillOval(cx + dx - dotR, cy + dy - dotR, dotR * 2, dotR * 2);
                                }
                                g2.setComposite(AlphaComposite.SrcOver);

                                // 文字整体上移并居中
                                g2.setFont(new Font("Microsoft YaHei", Font.PLAIN, 32));
                                String msg1 = "正在进行更新";
                                String msg2 = "第 2 步，共 3 步：正在安装功能和驱动程序";
                                String msg3 = "50% 完成";
                                String msg4 = "请不要关闭计算机，这可能需要一段时间";
                                FontMetrics fm = g2.getFontMetrics();
                                g2.drawString(msg1, cx - fm.stringWidth(msg1) / 2, cy + 100);
                                g2.setFont(new Font("Microsoft YaHei", Font.PLAIN, 22));
                                fm = g2.getFontMetrics();
                                g2.drawString(msg2, cx - fm.stringWidth(msg2) / 2, cy + 150);
                                g2.drawString(msg3, cx - fm.stringWidth(msg3) / 2, cy + 190);
                                g2.setFont(new Font("Microsoft YaHei", Font.PLAIN, 18));
                                fm = g2.getFontMetrics();
                                g2.drawString(msg4, cx - fm.stringWidth(msg4) / 2, cy + 240);
                            }
                        };
                        updatePanel.setFocusable(true);
                        updateDialog.setContentPane(updatePanel);

                        // 定时器让 loading 转动
                        Timer timer = new Timer(80, e2 -> {
                            angle[0] = (angle[0] + 1) % 12;
                            updatePanel.repaint();
                        });
                        timer.start();

                        // 关闭窗口时停止动画
                        updateDialog.addWindowListener(new java.awt.event.WindowAdapter() {
                            @Override
                            public void windowClosed(java.awt.event.WindowEvent e) {
                                timer.stop();
                            }
                        });

                        // 攔截所有按鍵，只有 Ctrl+Enter 能關閉
                        updateDialog.addKeyListener(new java.awt.event.KeyAdapter() {
                            @Override
                            public void keyPressed(java.awt.event.KeyEvent e) {
                                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER && e.isControlDown()) {
                                    updateDialog.dispose();
                                }
                            }
                        });

                        // 讓面板獲得焦點以接收按鍵
                        updateDialog.addWindowListener(new java.awt.event.WindowAdapter() {
                            @Override
                            public void windowOpened(java.awt.event.WindowEvent e) {
                                updatePanel.requestFocusInWindow();
                            }
                        });

                        // 设置 Key Bindings，只有 Ctrl+Enter 能关闭
                        InputMap im = updatePanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
                        ActionMap am = updatePanel.getActionMap();
                        im.put(KeyStroke.getKeyStroke("ctrl ENTER"), "closeUpdateDialog");
                        am.put("closeUpdateDialog", new AbstractAction() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                updateDialog.dispose();
                            }
                        });

                        updateDialog.setVisible(true);
                    });
                }

                if (i == 0) {
                    btn.addActionListener(ev -> {
                        // 用 JDialog 实现全屏透明窗口
                        JDialog gameDialog = new JDialog((JFrame)null, true);
                        gameDialog.setUndecorated(true);
                        gameDialog.setAlwaysOnTop(true);
                        gameDialog.setSize(Toolkit.getDefaultToolkit().getScreenSize());
                        gameDialog.setLocationRelativeTo(null);
                        gameDialog.setBackground(new Color(0,0,0,2));  // 稍为留一点，避免完全透明导致无法点击

                        // 自定义面板，绘制图片
                        JPanel panelDORO = new JPanel() {
                            BufferedImage tcImg;
                            BufferedImage cursorImg;
                            BufferedImage doroImg;
                            Point mousePos = MouseInfo.getPointerInfo().getLocation(); // ORANGE 目标位置
                            Point doroPos; // DORO 当前显示位置
                            boolean isSpinning = false;
                            double spinAngle = 0;
                            Point lastDoroPos = null;      // 记录上一次DORO位置
                            boolean doroFaceLeft = false;  // DORO是否朝左

                            // 在panelDORO类成员变量中添加
                            int spinCount = 0; // 已转圈数
                            double lastSpinAngle = 0; // 上一帧角度
                            boolean doroEaten = false; // 是否已被吃掉

                            {
                                try {
                                    tcImg = javax.imageio.ImageIO.read(new java.io.File("C:\\Users\\eric2\\Downloads\\PET\\TC.png"));
                                } catch (Exception ex) {
                                    tcImg = null;
                                }
                                try {
                                    cursorImg = javax.imageio.ImageIO.read(new java.io.File("C:\\Users\\eric2\\Downloads\\PET\\ORANGE.png"));
                                } catch (Exception ex) {
                                    cursorImg = null;
                                }
                                try {
                                    doroImg = javax.imageio.ImageIO.read(new java.io.File("C:\\Users\\eric2\\Downloads\\PET\\DORO.png"));
                                } catch (Exception ex) {
                                    doroImg = null;
                                }
                                // 初始DORO在右下角
                                if (doroImg != null) {
                                    doroPos = new Point(
                                        getWidth() - doroImg.getWidth() + 2560,
                                        getHeight() - doroImg.getHeight() + 1440
                                    );
                                } else {
                                    doroPos = new Point(getWidth() - 100, getHeight() - 100);
                                }
                                // 隱藏系統游標
                                java.awt.Toolkit tk = java.awt.Toolkit.getDefaultToolkit();
                                java.awt.Image emptyImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
                                setCursor(tk.createCustomCursor(emptyImg, new Point(0, 0), "blank"));

                                // 定時刷新鼠標位置和 DORO 位置
                                new javax.swing.Timer(16, e -> {
                                    // 获取全局鼠标位置并转为本地坐标
                                    Point p = MouseInfo.getPointerInfo().getLocation();
                                    SwingUtilities.convertPointFromScreen(p, this);
                                    mousePos = p;

                                    // 判断距离
                                    double dx = mousePos.x - doroPos.x;
                                    double dy = mousePos.y - doroPos.y;
                                    double dist = Math.hypot(dx, dy);

                                    isSpinning = dist < 100;

                                    if (isSpinning) {
                                        // 旋转
                                        spinAngle += 0.75; // 旋转速度
                                        int r = 45; // 旋转半径
                                        doroPos.x = (int) (mousePos.x + Math.cos(spinAngle) * r);
                                        doroPos.y = (int) (mousePos.y + Math.sin(spinAngle) * r);

                                        // 计算转圈数
                                        if (spinAngle - lastSpinAngle > 2 * Math.PI) {
                                            spinCount++;
                                            lastSpinAngle = spinAngle;
                                        }
                                        // 满10圈弹窗
                                        if (spinCount >= 10 && !doroEaten) {
                                            doroEaten = true;
                                            cursorImg = null; // 隐藏 ORANGE
                                            repaint(); // 立即重绘
                                            int option = JOptionPane.showOptionDialog(
                                                this,
                                                "欧润吉被吃掉了，怎么办？",
                                                "欧润吉危机",
                                                JOptionPane.YES_NO_OPTION,
                                                JOptionPane.WARNING_MESSAGE,
                                                null,
                                                new Object[]{"再来一颗", "离开"},
                                                "再来一颗"
                                            );
                                            if (option == JOptionPane.YES_OPTION) {
                                                // 再来一颗，重置圈数和状态
                                                spinCount = 0;
                                                doroEaten = false;
                                                // 可重置DORO位置或圖片等
                                                try {
                                                    cursorImg = javax.imageio.ImageIO.read(new java.io.File("C:\\Users\\eric2\\Downloads\\PET\\ORANGE.png"));
                                                } catch (Exception ex) {
                                                    JOptionPane.showMessageDialog(this, "ORANGE图片加载失败！");
                                                }
                                            } else if (option == JOptionPane.NO_OPTION) {
                                                // 离开，关闭窗口
                                                SwingUtilities.getWindowAncestor(this).dispose();
                                            }
                                        }
                                    } else {
                                        // DORO 位置缓动追踪 ORANGE
                                        double alpha = 0.05; // 越小越慢
                                        doroPos.x += (int)((mousePos.x - doroPos.x) * alpha);
                                        doroPos.y += (int)((mousePos.y - doroPos.y) * alpha);
                                        // 只要不是转圈就重置角度基准
                                        lastSpinAngle = spinAngle;
                                    }

                                    // 判断方向
                                    if (lastDoroPos != null && doroPos != null) {
                                        doroFaceLeft = doroPos.x > lastDoroPos.x;
                                    }
                                    if (doroPos != null) {
                                        lastDoroPos = new Point(doroPos.x, doroPos.y);
                                    }

                                    // ===== 检查DORO是否碰到TC =====
                                    if (doroImg != null && tcImg != null) {
                                        // DORO中心
                                        int doroCenterX = doroPos.x + doroImg.getWidth() / 10;
                                        int doroCenterY = doroPos.y + doroImg.getHeight() / 10;
                                        // TC区域
                                        Rectangle tcRect = new Rectangle(16, 16, 191, 300);
                                        if (tcRect.contains(doroCenterX, doroCenterY)) {
                                            doroImg = null; // DORO被垃圾筒吃掉
                                            repaint(); // 立即重绘
                                            int option = JOptionPane.showOptionDialog(
                                                this,
                                                "DORO被垃圾筒吃掉了，怎么办?",
                                                "DORO危机",
                                                JOptionPane.YES_NO_OPTION,
                                                JOptionPane.WARNING_MESSAGE,
                                                null,
                                                new Object[]{"拯救DORO", "离开"},
                                                "拯救DORO"
                                            );
                                            if (option == JOptionPane.YES_OPTION) {
                                                // 随机决定是否拯救成功
                                                boolean rescueSuccess = Math.random() < 0.5;  // 50% 概率
                                                if (rescueSuccess) {
                                                    JOptionPane.showMessageDialog(this, "你成功拯救了DORO！");
                                                    // 重置DORO位置和状态
                                                    try {
                                                        doroImg = javax.imageio.ImageIO.read(new java.io.File("C:\\Users\\eric2\\Downloads\\PET\\DORO.png"));
                                                        doroPos = new Point(
                                                            getWidth() - doroImg.getWidth() + 2560,
                                                            getHeight() - doroImg.getHeight() + 1440
                                                        );
                                                        spinCount = 0; // 重置圈数
                                                        isSpinning = false; // 停止旋转
                                                    } catch (Exception ex) {
                                                        JOptionPane.showMessageDialog(this, "DORO图片加载失败！");
                                                    }
                                                } else {
                                                    JOptionPane.showMessageDialog(this, "你试图拯救DORO，但失败了……");
                                                    JOptionPane.showMessageDialog(this, "拯救失败，DORO消失了……");
                                                    // 关闭当前窗口
                                                    SwingUtilities.getWindowAncestor(this).dispose();
                                                }
                                            } else if (option == JOptionPane.NO_OPTION) {
                                                JOptionPane.showMessageDialog(this, "你选择了离开，DORO消失了……");
                                                // 关闭当前窗口
                                                SwingUtilities.getWindowAncestor(this).dispose();
                                            }
                                        }
                                    }

                                    repaint();
                                }).start();
                            }

                            @Override
                            protected void paintComponent(Graphics g) {
                                super.paintComponent(g);
                                // 固定左上角顯示TC.png
                                if (tcImg != null) {
                                    g.drawImage(tcImg, 16, 16, 191, 300, null);
                                }
                                // ORANGE.png跟隨鼠標
                                if (cursorImg != null && mousePos != null) {
                                    int w = cursorImg.getWidth(), h = cursorImg.getHeight();
                                    g.drawImage(cursorImg, mousePos.x, mousePos.y, w, h, null);
                                }
                                // DORO.png 跟隨 ORANGE 或旋轉，原圖大小
                                if (doroImg != null && doroPos != null) {
                                    int dw = doroImg.getWidth(), dh = doroImg.getHeight();
                                    Graphics2D g2 = (Graphics2D) g.create();
                                    if (doroFaceLeft) {
                                        // 镜像绘制
                                        g2.drawImage(doroImg, doroPos.x + dw, doroPos.y, -dw, dh, null);
                                    } else {
                                        g2.drawImage(doroImg, doroPos.x, doroPos.y, dw, dh, null);
                                    }
                                    g2.dispose();
                                }
                            }
                        };
                        panelDORO.setOpaque(false);
                        panelDORO.setFocusable(true);
                        gameDialog.setContentPane(panelDORO);

                        // 只允許 Ctrl+Enter 關閉
                        InputMap im = panelDORO.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
                        ActionMap am = panelDORO.getActionMap();
                        im.put(KeyStroke.getKeyStroke("ctrl ENTER"), "closeGameDialog");
                        am.put("closeGameDialog", new AbstractAction() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                gameDialog.dispose();
                            }
                        });

                        // 讓面板獲得焦點以接收快捷鍵
                        gameDialog.addWindowListener(new java.awt.event.WindowAdapter() {
                            @Override
                            public void windowOpened(java.awt.event.WindowEvent e) {
                                panelDORO.requestFocusInWindow();
                            }
                        });

                        gameDialog.setVisible(true);
                    });
                }

                if (i == 2) {
                    btn.addActionListener(ev -> {
                        JDialog kunDialog = new JDialog((JFrame)null, true);
                        kunDialog.setUndecorated(true);
                        kunDialog.setAlwaysOnTop(true);
                        kunDialog.setSize(Toolkit.getDefaultToolkit().getScreenSize());
                        kunDialog.setLocationRelativeTo(null);
                        kunDialog.setBackground(new Color(0,0,0,32));  // 透明背景

                        JPanel gamePanel = new JPanel() {
                            int width = kunDialog.getWidth();
                            int height = kunDialog.getHeight();
                            int ballSize = 32;
                            int ballX = width / 2, ballY = height / 2;
                            int ballSpeedX = 6 * 10, ballSpeedY = -8 * 10;
                            int paddleW = 160, paddleH = 18;
                            int paddleX = width / 2 - paddleW / 2;
                            int paddleY = height - 540;
                            boolean running = true;
                            boolean gameOver = false;
                            BufferedImage ballImg;
                            BufferedImage paddleImg;
                            BufferedImage paddleImgGameOver; // 新增

                            // 新增旋转动画变量
                            double paddleAngle = 0;
                            boolean rotating = false;
                            long rotateStartTime = 0;
                            final int ROTATE_DURATION = 120; // 动画持续时间（毫秒）

                            {
                                setOpaque(false);
                                // 加载球图片
                                try {
                                    ballImg = javax.imageio.ImageIO.read(new java.io.File("C:\\Users\\eric2\\Downloads\\PET\\BALL.png"));
                                    ballSize = ballImg.getWidth();
                                } catch (Exception ex) {
                                    ballImg = null;
                                }
                                // 加载板子图片
                                try {
                                    paddleImg = javax.imageio.ImageIO.read(new java.io.File("C:\\Users\\eric2\\Downloads\\PET\\KUN.png"));
                                    paddleW = paddleImg.getWidth();
                                    paddleH = paddleImg.getHeight();
                                } catch (Exception ex) {
                                    paddleImg = null;
                                }
                                // 加载游戏结束时的板子图片
                                try {
                                    paddleImgGameOver = javax.imageio.ImageIO.read(new java.io.File("C:\\Users\\eric2\\Downloads\\PET\\KUN2.png"));
                                } catch (Exception ex) {
                                    paddleImgGameOver = null;
                                }
                                setFocusable(true);
                                addKeyListener(new java.awt.event.KeyAdapter() {
                                    @Override
                                    public void keyPressed(java.awt.event.KeyEvent e) {
                                        if (!running) return;
                                        if (e.getKeyCode() == java.awt.event.KeyEvent.VK_LEFT) {
                                            paddleX -= 40;
                                            if (paddleX < 0) paddleX = 0;
                                        } else if (e.getKeyCode() == java.awt.event.KeyEvent.VK_RIGHT) {
                                            paddleX += 40;
                                            if (paddleX > width - paddleW) paddleX = width - paddleW;
                                        }
                                    }
                                });
                                addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
                                    @Override
                                    public void mouseMoved(java.awt.event.MouseEvent e) {
                                        if (!running) return;
                                        paddleX = e.getX() - paddleW / 2;
                                        if (paddleX < 0) paddleX = 0;
                                        if (paddleX > width - paddleW) paddleX = width - paddleW;
                                    }
                                });
                                new javax.swing.Timer(16, e -> {
                                    if (!running) return;
                                    ballX += ballSpeedX;
                                    ballY += ballSpeedY;
                                    if (ballX <= 0 || ballX >= width - ballSize) ballSpeedX = -ballSpeedX;
                                    if (ballY <= 0) ballSpeedY = -ballSpeedY;
                                    if (ballY > height - ballSize) {
                                        running = false;
                                        gameOver = true;
                                        repaint();
                                        javax.swing.SwingUtilities.invokeLater(() -> {
                                            JOptionPane.showMessageDialog(this, "尼干麻 害海唉呦!", "小黑子胜利!", JOptionPane.INFORMATION_MESSAGE);
                                            kunDialog.dispose();
                                        });
                                        return;
                                    }
                                    Rectangle ballRect = new Rectangle(ballX, ballY, ballSize, ballSize);
                                    Rectangle paddleRect = new Rectangle(paddleX, paddleY, paddleW, paddleH);
                                    if (ballRect.intersects(paddleRect) && ballSpeedY > 0) {
                                        ballSpeedY = -ballSpeedY;
                                        int hitPos = ballX + ballSize / 2 - paddleX;
                                        double ratio = (hitPos - paddleW / 2) / (double)(paddleW / 2);
                                        ballSpeedX = (int)(ratio * 10);

                                        // 撞击时触发旋转动画
                                        paddleAngle = -15.0;
                                        rotating = true;
                                        rotateStartTime = System.currentTimeMillis();
                                    }
                                    // 旋转动画处理
                                    if (rotating) {
                                        long now = System.currentTimeMillis();
                                        long elapsed = now - rotateStartTime;
                                        if (elapsed >= ROTATE_DURATION) {
                                            paddleAngle = 0;
                                            rotating = false;
                                        } else {
                                            paddleAngle = -15.0 * (1 - elapsed / (double)ROTATE_DURATION);
                                        }
                                    }
                                    repaint();
                                }).start();
                            }

                            @Override
                            protected void paintComponent(Graphics g) {
                                super.paintComponent(g);
                                // 画球（用图片）
                                if (ballImg != null) {
                                    g.drawImage(ballImg, ballX, ballY, ballSize, ballSize, null);
                                } else {
                                    g.setColor(new Color(80,255,80,220));
                                    g.fillOval(ballX, ballY, ballSize, ballSize);
                                }
                                // 画板子（用图片+旋转），游戏结束时换图
                                BufferedImage showPaddle = (!gameOver) ? paddleImg : paddleImgGameOver;
                                if (showPaddle != null) {
                                    Graphics2D g2d = (Graphics2D) g.create();
                                    int cx = paddleX + paddleW / 2;
                                    int cy = paddleY + paddleH / 2;
                                    g2d.rotate(Math.toRadians(paddleAngle), cx, cy);
                                    g2d.drawImage(showPaddle, paddleX, paddleY, paddleW, paddleH, null);
                                    g2d.dispose();
                                } else {
                                    g.setColor(new Color(80,200,255,220));
                                    g.fillRoundRect(paddleX, paddleY, paddleW, paddleH, 12, 12);
                                }
                                // 画边界
                                g.setColor(new Color(255,255,255,60));
                                g.fillRect(0, 0, width, 8);
                                g.fillRect(0, 0, 8, height);
                                g.fillRect(width-8, 0, 8, height);
                                // if (gameOver) {
                                //     g.setFont(new Font("微软雅黑", Font.BOLD, 64));
                                //     g.setColor(new Color(255,80,80,200));
                                //     g.drawString("Game Over", width/2-180, height/2);
                                // }
                            }
                        };
                        gamePanel.setOpaque(false);
                        kunDialog.setContentPane(gamePanel);

                        InputMap im = gamePanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
                        ActionMap am = gamePanel.getActionMap();
                        im.put(KeyStroke.getKeyStroke("ctrl ENTER"), "closeKunDialog");
                        am.put("closeKunDialog", new AbstractAction() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                kunDialog.dispose();
                            }
                        });

                        kunDialog.addWindowListener(new java.awt.event.WindowAdapter() {
                            @Override
                            public void windowOpened(java.awt.event.WindowEvent e) {
                                gamePanel.requestFocusInWindow();
                            }
                        });

                        kunDialog.setVisible(true);
                    });
                }

                // 第四個按鈕（index 3）打開特定網址
                if (i == 3) {
                    btn.addActionListener(ev -> {
                        try {
                            java.awt.Desktop.getDesktop().browse(new java.net.URI("http://192.168.1.233:8554/"));
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(null, "无法打开网址: " + ex.getMessage());
                        }
                    });
                }
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
