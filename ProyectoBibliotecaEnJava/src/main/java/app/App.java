package app;

import app.view.LoginForm;

import javax.swing.*;

public class App {
    public static void main(String[] args) {
        // Se ejecuta en el hilo de eventos de Swing para la seguridad de los hilos
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Sistema de Biblioteca - Login");
            frame.setContentPane(new LoginForm().mainPanel);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setLocationRelativeTo(null); // Centra la ventana en la pantalla
            frame.setVisible(true);
        });
    }
}