package app.view;

import app.dao.ClienteDAO;
import app.model.Cliente;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.List;

public class ClienteForm {
    private JTextField txtNombre;
    private JTextField txtNit;
    private JTextField txtTelefono;
    private JCheckBox chkEstado;
    private JButton btnLimpiar;
    private JTextField txtBuscar;
    private JTable tableClientes;
    private JButton btnGuardar;
    private JButton btnActualizar;
    private JButton btnEliminar;
    JPanel mainPanel;

    private final ClienteDAO clienteDAO;
    private int clienteSeleccionadoId = -1;

    public ClienteForm() {
        clienteDAO = new ClienteDAO();
        cargarDatosEnTabla(null);

        btnLimpiar.addActionListener(e -> limpiarCampos());
        btnGuardar.addActionListener(e -> guardarCliente());
        btnActualizar.addActionListener(e -> actualizarCliente());
        btnEliminar.addActionListener(e -> eliminarCliente());

        tableClientes.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int filaSeleccionada = tableClientes.getSelectedRow();
                if (filaSeleccionada != -1) {
                    clienteSeleccionadoId = (int) tableClientes.getValueAt(filaSeleccionada, 0);
                    txtNombre.setText((String) tableClientes.getValueAt(filaSeleccionada, 1));
                    txtNit.setText((String) tableClientes.getValueAt(filaSeleccionada, 2));
                    txtTelefono.setText((String) tableClientes.getValueAt(filaSeleccionada, 3));
                    String estadoTexto = (String) tableClientes.getValueAt(filaSeleccionada, 4);
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
        String[] columnas = {"ID", "Nombre", "NIT", "Teléfono", "Estado"};
        DefaultTableModel model = new DefaultTableModel(null, columnas);
        tableClientes.setModel(model);

        try {
            List<Cliente> listaClientes;
            if (filtro != null && !filtro.isEmpty()) {
                listaClientes = clienteDAO.buscarPorNombre(filtro);
            } else {
                listaClientes = clienteDAO.listar();
            }

            for (Cliente c : listaClientes) {
                String estado = (c.getEstado() == 1) ? "Activo" : "Inactivo";
                model.addRow(new Object[]{c.getId(), c.getNombre(), c.getNit(), c.getTelefono(), estado});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(mainPanel, "Error al cargar los datos: " + e.getMessage(), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void limpiarCampos() {
        txtNombre.setText("");
        txtNit.setText("");
        txtTelefono.setText("");
        chkEstado.setSelected(true);
        clienteSeleccionadoId = -1;
    }

    private void guardarCliente() {
        String nombre = txtNombre.getText().trim();
        String nit = txtNit.getText().trim();
        String telefono = txtTelefono.getText().trim();
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(mainPanel, "El campo Nombre es obligatorio.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int estado = chkEstado.isSelected() ? 1 : 0;
        Cliente nuevoCliente = new Cliente(nombre, nit, telefono, estado);
        try {
            int id = clienteDAO.insertar(nuevoCliente);
            if (id != -1) {
                JOptionPane.showMessageDialog(mainPanel, "Cliente guardado exitosamente con ID: " + id, "Éxito", JOptionPane.INFORMATION_MESSAGE);
                limpiarCampos();
                cargarDatosEnTabla(null);
            } else {
                JOptionPane.showMessageDialog(mainPanel, "Error al guardar el cliente.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(mainPanel, "Error de base de datos: " + ex.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void actualizarCliente() {
        if (clienteSeleccionadoId == -1) {
            JOptionPane.showMessageDialog(mainPanel, "Seleccione un cliente de la tabla para actualizar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String nombre = txtNombre.getText().trim();
        String nit = txtNit.getText().trim();
        String telefono = txtTelefono.getText().trim();
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(mainPanel, "El campo Nombre es obligatorio.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int estado = chkEstado.isSelected() ? 1 : 0;
        Cliente clienteActualizado = new Cliente(clienteSeleccionadoId, nombre, nit, telefono, estado);
        try {
            boolean exito = clienteDAO.actualizar(clienteActualizado);
            if (exito) {
                JOptionPane.showMessageDialog(mainPanel, "Cliente actualizado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                limpiarCampos();
                cargarDatosEnTabla(null);
            } else {
                JOptionPane.showMessageDialog(mainPanel, "Error al actualizar el cliente.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(mainPanel, "Error de base de datos: " + ex.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void eliminarCliente() {
        if (clienteSeleccionadoId == -1) {
            JOptionPane.showMessageDialog(mainPanel, "Seleccione un cliente de la tabla para eliminar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirmacion = JOptionPane.showConfirmDialog(mainPanel,
                "¿Está seguro de que desea eliminar lógicamente este cliente?",
                "Confirmar Eliminación",
                JOptionPane.YES_NO_OPTION);
        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                boolean exito = clienteDAO.eliminar(clienteSeleccionadoId);
                if (exito) {
                    JOptionPane.showMessageDialog(mainPanel, "Cliente eliminado (baja lógica) exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    limpiarCampos();
                    cargarDatosEnTabla(null);
                } else {
                    JOptionPane.showMessageDialog(mainPanel, "Error al eliminar el cliente.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(mainPanel, "Error de base de datos: " + ex.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }
}