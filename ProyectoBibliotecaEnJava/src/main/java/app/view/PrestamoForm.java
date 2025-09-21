package app.view;

import app.dao.ClienteDAO;
import app.dao.LibroDAO;
import app.dao.PrestamoDAO;
import app.dao.UsuarioDAO;
import app.model.*;
import app.core.Sesion;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.List;

public class PrestamoForm {
    public JPanel mainPanel;
    private JComboBox<Item> cbCliente;
    private JComboBox<Item> cbLibro;
    private JComboBox<Item> cbUsuario;
    private JCheckBox chkEstado;
    private JButton btnGuardar;
    private JButton btnActualizar;
    private JButton btnEliminar;
    private JButton btnLimpiar;
    private JTable tablePrestamos;
    private JTextField txtBuscar;

    private final PrestamoDAO prestamoDAO;
    private final ClienteDAO clienteDAO;
    private final UsuarioDAO usuarioDAO;
    private int prestamoSeleccionadoId = -1;

    // Clase interna para manejar los items del JComboBox
    private static class Item {
        private final int id;
        private final String nombre;

        public Item(int id, String nombre) {
            this.id = id;
            this.nombre = nombre;
        }

        public int getId() {
            return id;
        }

        @Override
        public String toString() {
            return nombre;
        }
    }

    public PrestamoForm() {
        prestamoDAO = new PrestamoDAO();
        clienteDAO = new ClienteDAO();
        LibroDAO libroDAO = new LibroDAO();
        usuarioDAO = new UsuarioDAO();

        cargarClientes();
        cargarLibros();
        cargarUsuarios();
        cargarDatosEnTabla(null);

        btnLimpiar.addActionListener(e -> limpiarCampos());
        btnGuardar.addActionListener(e -> guardarPrestamo());
        btnActualizar.addActionListener(e -> actualizarPrestamo());
        btnEliminar.addActionListener(e -> eliminarPrestamo());

        tablePrestamos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int filaSeleccionada = tablePrestamos.getSelectedRow();
                if (filaSeleccionada != -1) {
                    prestamoSeleccionadoId = (int) tablePrestamos.getValueAt(filaSeleccionada, 0);

                    // Cargar los items seleccionados en los JComboBox
                    setComboBoxSelection(cbCliente, (String) tablePrestamos.getValueAt(filaSeleccionada, 1));
                    setComboBoxSelection(cbLibro, (String) tablePrestamos.getValueAt(filaSeleccionada, 2));
                    setComboBoxSelection(cbUsuario, (String) tablePrestamos.getValueAt(filaSeleccionada, 4));

                    String estadoTexto = (String) tablePrestamos.getValueAt(filaSeleccionada, 5);
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

    private void cargarClientes() {
        try {
            cbCliente.removeAllItems();
            List<Cliente> listaClientes = clienteDAO.listar();
            for (Cliente c : listaClientes) {
                cbCliente.addItem(new Item(c.getId(), c.getNombre()));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(mainPanel, "Error al cargar clientes: " + e.getMessage(), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void cargarLibros() {
        try {
            cbLibro.removeAllItems();
            List<Libro> listaLibros = new LibroDAO().listar();
            for (Libro l : listaLibros) {
                cbLibro.addItem(new Item(l.getId(), l.getNombre()));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(mainPanel, "Error al cargar libros: " + e.getMessage(), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void cargarUsuarios() {
        try {
            cbUsuario.removeAllItems();
            List<Usuario> listaUsuarios = usuarioDAO.listar();
            for (Usuario u : listaUsuarios) {
                cbUsuario.addItem(new Item(u.getId(), u.getNombre()));
            }
            // Si la sesión está activa, pre-seleccionar el usuario actual
            if (Sesion.getUsuario() != null) {
                setComboBoxSelection(cbUsuario, Sesion.getUsuario().getNombre());
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(mainPanel, "Error al cargar usuarios: " + e.getMessage(), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void cargarDatosEnTabla(String filtro) {
        String[] columnas = {"ID", "Cliente", "Libro", "Fecha", "Usuario", "Estado"};
        DefaultTableModel model = new DefaultTableModel(null, columnas);
        tablePrestamos.setModel(model);

        try {
            List<PrestamoConEntidades> listaPrestamos;
            if (filtro != null && !filtro.isEmpty()) {
                listaPrestamos = prestamoDAO.buscarPorNombreCliente(filtro);
            } else {
                listaPrestamos = prestamoDAO.listar();
            }

            for (PrestamoConEntidades p : listaPrestamos) {
                String estado = (p.getEstado() == 1) ? "Activo" : "Devuelto";
                model.addRow(new Object[]{p.getId(), p.getClienteNombre(), p.getLibroNombre(), p.getFecha(), p.getUsuarioNombre(), estado});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(mainPanel, "Error al cargar los datos: " + e.getMessage(), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void limpiarCampos() {
        cbCliente.setSelectedIndex(0);
        cbLibro.setSelectedIndex(0);
        cbUsuario.setSelectedIndex(0);
        chkEstado.setSelected(true);
        prestamoSeleccionadoId = -1;
    }

    private void guardarPrestamo() {
        if (cbCliente.getSelectedItem() == null || cbLibro.getSelectedItem() == null || cbUsuario.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(mainPanel, "Todos los campos son obligatorios.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int idCliente = ((Item) cbCliente.getSelectedItem()).getId();
        int idLibro = ((Item) cbLibro.getSelectedItem()).getId();
        int idUsuario = ((Item) cbUsuario.getSelectedItem()).getId();
        int estado = chkEstado.isSelected() ? 1 : 0;

        try {
            // Validar que el libro no esté prestado
            if (!prestamoDAO.verificarLibroDisponible(idLibro)) {
                JOptionPane.showMessageDialog(mainPanel, "El libro seleccionado ya está prestado. No se puede registrar un nuevo préstamo.", "Libro No Disponible", JOptionPane.WARNING_MESSAGE);
                return; // Detiene la ejecución si el libro no está disponible
            }

            Prestamo nuevoPrestamo = new Prestamo(idCliente, idLibro, idUsuario, estado);
            int id = prestamoDAO.insertar(nuevoPrestamo);
            if (id != -1) {
                JOptionPane.showMessageDialog(mainPanel, "Préstamo guardado exitosamente con ID: " + id, "Éxito", JOptionPane.INFORMATION_MESSAGE);
                limpiarCampos();
                cargarDatosEnTabla(null);
            } else {
                JOptionPane.showMessageDialog(mainPanel, "Error al guardar el préstamo.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(mainPanel, "Error de base de datos: " + ex.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void actualizarPrestamo() {
        if (prestamoSeleccionadoId == -1) {
            JOptionPane.showMessageDialog(mainPanel, "Seleccione un préstamo de la tabla para actualizar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idCliente = ((Item) cbCliente.getSelectedItem()).getId();
        int idLibro = ((Item) cbLibro.getSelectedItem()).getId();
        int idUsuario = ((Item) cbUsuario.getSelectedItem()).getId();
        int estado = chkEstado.isSelected() ? 1 : 0;

        Prestamo prestamoActualizado = new Prestamo(prestamoSeleccionadoId, idCliente, idLibro, idUsuario, estado);

        try {
            boolean exito = prestamoDAO.actualizar(prestamoActualizado);
            if (exito) {
                JOptionPane.showMessageDialog(mainPanel, "Préstamo actualizado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                limpiarCampos();
                cargarDatosEnTabla(null);
            } else {
                JOptionPane.showMessageDialog(mainPanel, "Error al actualizar el préstamo.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(mainPanel, "Error de base de datos: " + ex.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void eliminarPrestamo() {
        if (prestamoSeleccionadoId == -1) {
            JOptionPane.showMessageDialog(mainPanel, "Seleccione un préstamo de la tabla para eliminar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(mainPanel,
                "¿Está seguro de que desea eliminar lógicamente este préstamo?",
                "Confirmar Eliminación",
                JOptionPane.YES_NO_OPTION);

        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                // Para la eliminación lógica, simplemente actualizamos el estado a 0.
                Prestamo p = new Prestamo(prestamoSeleccionadoId, 0); // Solo necesitamos el ID y el nuevo estado
                boolean exito = prestamoDAO.eliminar(p.getId());
                if (exito) {
                    JOptionPane.showMessageDialog(mainPanel, "Préstamo eliminado (baja lógica) exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    limpiarCampos();
                    cargarDatosEnTabla(null);
                } else {
                    JOptionPane.showMessageDialog(mainPanel, "Error al eliminar el préstamo.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(mainPanel, "Error de base de datos: " + ex.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    // Helper para seleccionar un item en el JComboBox por su nombre
    private void setComboBoxSelection(JComboBox<Item> comboBox, String nombre) {
        for (int i = 0; i < comboBox.getItemCount(); i++) {
            if (comboBox.getItemAt(i).nombre.equals(nombre)) {
                comboBox.setSelectedIndex(i);
                return;
            }
        }
    }
}