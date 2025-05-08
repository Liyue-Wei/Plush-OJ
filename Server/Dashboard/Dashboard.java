package Dashboard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Dashboard {

    public static void main(String[] args) {
        // Swing operations should be performed on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

    private static void createAndShowGUI() {
        // 1. Create the main window (JFrame)
        JFrame frame = new JFrame("My First Swing App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Exit application when window is closed
        frame.setSize(400, 300); // Set window size

        // 2. Create components
        JLabel label = new JLabel("Hello, Swing World!");
        JButton button = new JButton("Click Me!");

        // 3. Add an action listener to the button
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                label.setText("Button was clicked!");
            }
        });

        // 4. Set a layout manager for the frame's content pane
        // FlowLayout arranges components in a left-to-right flow, wrapping if necessary.
        frame.getContentPane().setLayout(new FlowLayout());

        // 5. Add components to the frame's content pane
        frame.getContentPane().add(label);
        frame.getContentPane().add(button);

        // 6. Make the window visible
        // It's good practice to call pack() before setVisible(true) if you want the window
        // to be sized to fit its components, but here we've set a fixed size.
        // frame.pack(); // Alternatively, size the frame to fit its components
        frame.setLocationRelativeTo(null); // Center the window on the screen
        frame.setVisible(true);

        System.out.println("Welcome to Plush::OJ Server (with a Swing UI window!)");
    }
}