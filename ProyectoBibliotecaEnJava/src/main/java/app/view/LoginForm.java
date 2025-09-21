package app.view;

import app.core.Sesion;
import app.dao.UsuarioDAO;
import app.model.Usuario;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class LoginForm {
    public JPanel mainPanel;
    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private JButton btnLogin;

    public LoginForm() {
        // Escuchador de eventos para el botón de login
        mainPanel.setPreferredSize(new Dimension(360, 200));
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Obtener el nombre de usuario y la contraseña
                String username = txtUsuario.getText();
                String password = new String(txtPassword.getPassword());

                // Validar campos vacíos
                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Por favor, complete todos los campos.",
                            "Error de Validación", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Usamos un bloque try-catch para manejar errores de la base de datos
                try {
                    // Instanciamos el DAO para acceder a la base de datos
                    UsuarioDAO usuarioDAO = new UsuarioDAO();
                    // Intentamos validar el login
                    Usuario usuario = usuarioDAO.validarLogin(username, password);

                    // Si el usuario no es null, el login fue exitoso
                    if (usuario != null) {
                        // Guardamos el usuario en la sesión
                        Sesion.login(usuario);
                        JOptionPane.showMessageDialog(null, "¡Bienvenido, " + usuario.getNombre() + "!",
                                "Login Exitoso", JOptionPane.INFORMATION_MESSAGE);

                        // Abrimos el menú principal
                        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(mainPanel);
                        frame.setContentPane(new MainMenuForm().mainPanel);
                        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                        int width = (int) (screenSize.width * 0.6); // 60% del ancho de la pantalla
                        int height = (int) (screenSize.height * 0.6); // 60% del alto de la pantalla

                        // Establecer el tamaño y centrar la ventana
                        frame.setSize(width, height);
                        frame.setLocationRelativeTo(null);
                        frame.setTitle("Sistema de Biblioteca - Menú Principal");
                    } else {
                        // Si el usuario es null, las credenciales son incorrectas
                        JOptionPane.showMessageDialog(null, "Usuario o contraseña incorrectos.",
                                "Error de Login", JOptionPane.ERROR_MESSAGE);
                        txtPassword.setText(""); // Limpiamos el campo de contraseña
                    }
                } catch (SQLException ex) {
                    // Manejo de errores de SQL
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error al conectar con la base de datos: " + ex.getMessage(),
                            "Error de Conexión", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    public static void main(String[] args) {
        showForm();
    }

    // MÉTODO AGREGADO PARA QUE MainMenuForm PUEDA VOLVER AL LOGIN
    public static void showForm() {
        JFrame frame = new JFrame("Login");
        frame.setContentPane(new LoginForm().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

}