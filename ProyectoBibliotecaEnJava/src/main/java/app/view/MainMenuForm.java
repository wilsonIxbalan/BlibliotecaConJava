package app.view;

import app.core.Sesion;

import javax.swing.*;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainMenuForm {
    public JPanel mainPanel;
    private JButton btnAutores;
    private JButton btnLibros;
    private JButton btnCategorias;
    private JButton btnClientes;
    private JButton btnPrestamos;
    private JButton btnUsuarios;
    private JLabel lblUsuario;
    private JButton btnCerrarSesion;

    public MainMenuForm() {

        if (Sesion.getUsuario() != null) {
            lblUsuario.setText("Bienvenido, " + Sesion.getUsuario().getNombre() + " (" + Sesion.getUsuario().getRol() + ")");
        }

        // Deshabilitar botones si el usuario no es ADMIN
        if (!Sesion.hasRole("ADMIN")) {
            btnUsuarios.setEnabled(false);
            btnCategorias.setEnabled(false);
        }

        // Lógica de navegación para los botones

        // Evento para el botón de Autores
        btnAutores.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Abrir el formulario de autores
                JFrame frame = new JFrame("Gestión de Autores");
                frame.setContentPane(new AutorForm().mainPanel);
                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                int width = (int) (screenSize.width * 0.7); // 70% del ancho de la pantalla
                int height = (int) (screenSize.height * 0.7); // 70% del alto de la pantalla

                // Establecer el tamaño y centrar la ventana
                frame.setSize(width, height);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });

        // Evento para el botón de Categorias
        btnCategorias.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Crear el JFrame para el formulario de categorías
                JFrame frame = new JFrame("Gestión de Categorías");
                frame.setContentPane(new CategoriaForm().mainPanel);

                // Obtener el tamaño de la pantalla
                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                int width = (int) (screenSize.width * 0.7); // 70% del ancho de la pantalla
                int height = (int) (screenSize.height * 0.7); // 70% del alto de la pantalla

                // Establecer el tamaño y centrar la ventana
                frame.setSize(width, height);
                frame.setLocationRelativeTo(null);

                frame.setVisible(true);
            }
        });

        // Evento para el botón de Clientes
        btnClientes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Crear el JFrame para el formulario de clientes
                JFrame frame = new JFrame("Gestión de Clientes");
                frame.setContentPane(new ClienteForm().mainPanel);

                // Obtener el tamaño de la pantalla
                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                int width = (int) (screenSize.width * 0.7); // 70% del ancho de la pantalla
                int height = (int) (screenSize.height * 0.7); // 70% del alto de la pantalla

                // Establecer el tamaño y centrar la ventana
                frame.setSize(width, height);
                frame.setLocationRelativeTo(null);

                frame.setVisible(true);
            }
        });

        // Evento para el botón de Libros
        btnLibros.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame frame = new JFrame("Gestión de Libros");
                frame.setContentPane(new LibroForm().mainPanel);

                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                int width = (int) (screenSize.width * 0.7);
                int height = (int) (screenSize.height * 0.7);

                frame.setSize(width, height);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });

        // Evento para el botón de Usuarios
        btnUsuarios.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame frame = new JFrame("Gestión de Usuarios");
                frame.setContentPane(new UsuarioForm().mainPanel);

                // Obtener el tamaño de la pantalla
                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                int width = (int) (screenSize.width * 0.7);
                int height = (int) (screenSize.height * 0.7);

                // Establecer el tamaño y centrar la ventana
                frame.setSize(width, height);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });

        // Evento para el botón de Prestamos
        btnPrestamos.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame frame = new JFrame("Gestión de Préstamos");
                frame.setContentPane(new PrestamoForm().mainPanel);

                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                int width = (int) (screenSize.width * 0.7);
                int height = (int) (screenSize.height * 0.7);

                frame.setSize(width, height);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });

        btnCerrarSesion.addActionListener(e -> {
            Sesion.cerrarSesion();
            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(mainPanel);
            parentFrame.dispose();
            new LoginForm().showForm();
        });

    }

}