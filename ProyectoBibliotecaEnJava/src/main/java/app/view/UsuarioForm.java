// Archivo: src/main/java/app/view/UsuarioForm.java
package app.view;

import app.core.Sesion;
import app.dao.UsuarioDAO;
import app.model.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.List;

public class UsuarioForm {
    public JPanel mainPanel;
    private JTextField txtUsername;
    private JTextField txtNombre;
    private JPasswordField txtPassword;
    private JComboBox<String> cbRol;
    private JCheckBox chkEstado;
    private JButton btnGuardar;
    private JButton btnActualizar;
    private JButton btnEliminar;
    private JButton btnLimpiar;
    private JTable tableUsuarios;
    private JTextField txtBuscar;

    private UsuarioDAO usuarioDAO;
    private int usuarioSeleccionadoId = -1;

    public UsuarioForm() {
        if (!Sesion.hasRole("ADMIN")) {
            JOptionPane.showMessageDialog(null, "No tiene permisos para acceder a esta función.", "Acceso Denegado", JOptionPane.WARNING_MESSAGE);
            mainPanel.setVisible(false);
            return;
        }
        usuarioDAO = new UsuarioDAO();
        inicializarComponentes();
        cargarDatosEnTabla(null);
    }

    private void inicializarComponentes() {
        cbRol.addItem("ADMIN");
        cbRol.addItem("OPERADOR");

        btnLimpiar.addActionListener(e -> limpiarCampos());
        btnGuardar.addActionListener(e -> guardarUsuario());
        btnActualizar.addActionListener(e -> actualizarUsuario());
        btnEliminar.addActionListener(e -> eliminarUsuario());

        tableUsuarios.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int filaSeleccionada = tableUsuarios.getSelectedRow();
                if (filaSeleccionada != -1) {
                    // CÓDIGO CORREGIDO AQUÍ
                    usuarioSeleccionadoId = Integer.parseInt(tableUsuarios.getValueAt(filaSeleccionada, 0).toString());
                    txtUsername.setText((String) tableUsuarios.getValueAt(filaSeleccionada, 1));
                    txtNombre.setText((String) tableUsuarios.getValueAt(filaSeleccionada, 2));
                    txtPassword.setText("");
                    cbRol.setSelectedItem(tableUsuarios.getValueAt(filaSeleccionada, 4));
                    String estadoTexto = (String) tableUsuarios.getValueAt(filaSeleccionada, 5);
                    chkEstado.setSelected(estadoTexto.equals("Activo"));
                }
            }
        });

        txtBuscar.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String filtro = txtBuscar.getText().trim();
                cargarDatosEnTabla(filtro);
            }
        });
    }

    private void cargarDatosEnTabla(String filtro) {
        String[] columnas = {"ID", "Username", "Nombre", "Password", "Rol", "Estado"};
        DefaultTableModel model = new DefaultTableModel(null, columnas);
        tableUsuarios.setModel(model);

        try {
            List<Usuario> listaUsuarios;
            if (filtro != null && !filtro.isEmpty()) {
                listaUsuarios = usuarioDAO.buscarPorNombre(filtro);
            } else {
                listaUsuarios = usuarioDAO.listar();
            }

            for (Usuario u : listaUsuarios) {
                String estado = (u.getEstado() == 1) ? "Activo" : "Inactivo";
                model.addRow(new Object[]{u.getId(), u.getUsername(), u.getNombre(), "********", u.getRol(), estado});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(mainPanel, "Error al cargar los datos: " + e.getMessage(), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void limpiarCampos() {
        txtUsername.setText("");
        txtNombre.setText("");
        txtPassword.setText("");
        cbRol.setSelectedIndex(0);
        chkEstado.setSelected(true);
        usuarioSeleccionadoId = -1;
    }

    private void guardarUsuario() {
        String username = txtUsername.getText().trim();
        String nombre = txtNombre.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();
        String rol = (String) cbRol.getSelectedItem();

        if (username.isEmpty() || nombre.isEmpty() || password.isEmpty() || rol == null) {
            JOptionPane.showMessageDialog(mainPanel, "Todos los campos son obligatorios.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int estado = chkEstado.isSelected() ? 1 : 0;
        Usuario nuevoUsuario = new Usuario(username, nombre, password, rol, estado);

        try {
            int id = usuarioDAO.insertar(nuevoUsuario);
            if (id != -1) {
                JOptionPane.showMessageDialog(mainPanel, "Usuario guardado exitosamente con ID: " + id, "Éxito", JOptionPane.INFORMATION_MESSAGE);
                limpiarCampos();
                cargarDatosEnTabla(null);
            } else {
                JOptionPane.showMessageDialog(mainPanel, "Error al guardar el usuario.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(mainPanel, "Error de base de datos: " + ex.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void actualizarUsuario() {
        if (usuarioSeleccionadoId == -1) {
            JOptionPane.showMessageDialog(mainPanel, "Seleccione un usuario de la tabla para actualizar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String username = txtUsername.getText().trim();
        String nombre = txtNombre.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();
        String rol = (String) cbRol.getSelectedItem();

        if (username.isEmpty() || nombre.isEmpty() || rol == null) {
            JOptionPane.showMessageDialog(mainPanel, "Los campos de Username, Nombre y Rol son obligatorios.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int estado = chkEstado.isSelected() ? 1 : 0;
        String passwordToUpdate = password.isEmpty() ? null : password;

        Usuario usuarioActualizado = new Usuario(usuarioSeleccionadoId, username, nombre, passwordToUpdate, rol, estado);

        try {
            boolean exito = usuarioDAO.actualizar(usuarioActualizado);
            if (exito) {
                JOptionPane.showMessageDialog(mainPanel, "Usuario actualizado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                limpiarCampos();
                cargarDatosEnTabla(null);
            } else {
                JOptionPane.showMessageDialog(mainPanel, "Error al actualizar el usuario.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(mainPanel, "Error de base de datos: " + ex.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void eliminarUsuario() {
        if (usuarioSeleccionadoId == -1) {
            JOptionPane.showMessageDialog(mainPanel, "Seleccione un usuario de la tabla para eliminar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirmacion = JOptionPane.showConfirmDialog(mainPanel,
                "¿Está seguro de que desea eliminar lógicamente este usuario?",
                "Confirmar Eliminación",
                JOptionPane.YES_NO_OPTION);
        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                boolean exito = usuarioDAO.eliminar(usuarioSeleccionadoId);
                if (exito) {
                    JOptionPane.showMessageDialog(mainPanel, "Usuario eliminado (baja lógica) exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    limpiarCampos();
                    cargarDatosEnTabla(null);
                } else {
                    JOptionPane.showMessageDialog(mainPanel, "Error al eliminar el usuario.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(mainPanel, "Error de base de datos: " + ex.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }
}