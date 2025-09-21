package app.view;

import app.dao.CategoriaDAO;
import app.model.Categoria;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.List;

public class CategoriaForm {
    private JButton btnGuardar;
    private JTextField txtNombre;
    private JCheckBox chkEstado;
    private JTextField txtBuscar;
    private JTable tableCategorias;
    private JButton btnLimpiar;
    private JButton btnEliminar;
    private JButton btnActualizar;
    JPanel mainPanel;

    private final CategoriaDAO categoriaDAO;
    private int categoriaSeleccionadaId = -1;

    public CategoriaForm() {
        categoriaDAO = new CategoriaDAO();
        cargarDatosEnTabla(null);

        btnLimpiar.addActionListener(e -> limpiarCampos());
        btnGuardar.addActionListener(e -> guardarCategoria());
        btnActualizar.addActionListener(e -> actualizarCategoria());
        btnEliminar.addActionListener(e -> eliminarCategoria());

        tableCategorias.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int filaSeleccionada = tableCategorias.getSelectedRow();
                if (filaSeleccionada != -1) {
                    categoriaSeleccionadaId = (int) tableCategorias.getValueAt(filaSeleccionada, 0);
                    txtNombre.setText((String) tableCategorias.getValueAt(filaSeleccionada, 1));
                    String estadoTexto = (String) tableCategorias.getValueAt(filaSeleccionada, 2);
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
        String[] columnas = {"ID", "Nombre", "Estado"};
        DefaultTableModel model = new DefaultTableModel(null, columnas);
        tableCategorias.setModel(model);

        try {
            List<Categoria> listaCategorias;
            if (filtro != null && !filtro.isEmpty()) {
                listaCategorias = categoriaDAO.buscarPorNombre(filtro);
            } else {
                listaCategorias = categoriaDAO.listar();
            }

            for (Categoria c : listaCategorias) {
                String estado = (c.getEstado() == 1) ? "Activo" : "Inactivo";
                model.addRow(new Object[]{c.getId(), c.getNombre(), estado});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(mainPanel, "Error al cargar los datos: " + e.getMessage(), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void limpiarCampos() {
        txtNombre.setText("");
        chkEstado.setSelected(true);
        categoriaSeleccionadaId = -1;
    }

    private void guardarCategoria() {
        String nombre = txtNombre.getText().trim();
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(mainPanel, "El campo Nombre es obligatorio.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int estado = chkEstado.isSelected() ? 1 : 0;
        Categoria nuevaCategoria = new Categoria(nombre, estado);
        try {
            int id = categoriaDAO.insertar(nuevaCategoria);
            if (id != -1) {
                JOptionPane.showMessageDialog(mainPanel, "Categoría guardada exitosamente con ID: " + id, "Éxito", JOptionPane.INFORMATION_MESSAGE);
                limpiarCampos();
                cargarDatosEnTabla(null);
            } else {
                JOptionPane.showMessageDialog(mainPanel, "Error al guardar la categoría.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(mainPanel, "Error de base de datos: " + ex.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void actualizarCategoria() {
        if (categoriaSeleccionadaId == -1) {
            JOptionPane.showMessageDialog(mainPanel, "Seleccione una categoría de la tabla para actualizar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String nombre = txtNombre.getText().trim();
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(mainPanel, "El campo Nombre es obligatorio.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int estado = chkEstado.isSelected() ? 1 : 0;
        Categoria categoriaActualizada = new Categoria(categoriaSeleccionadaId, nombre, estado);
        try {
            boolean exito = categoriaDAO.actualizar(categoriaActualizada);
            if (exito) {
                JOptionPane.showMessageDialog(mainPanel, "Categoría actualizada exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                limpiarCampos();
                cargarDatosEnTabla(null);
            } else {
                JOptionPane.showMessageDialog(mainPanel, "Error al actualizar la categoría.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(mainPanel, "Error de base de datos: " + ex.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void eliminarCategoria() {
        if (categoriaSeleccionadaId == -1) {
            JOptionPane.showMessageDialog(mainPanel, "Seleccione una categoría de la tabla para eliminar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirmacion = JOptionPane.showConfirmDialog(mainPanel,
                "¿Está seguro de que desea eliminar lógicamente esta categoría?",
                "Confirmar Eliminación",
                JOptionPane.YES_NO_OPTION);
        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                boolean exito = categoriaDAO.eliminar(categoriaSeleccionadaId);
                if (exito) {
                    JOptionPane.showMessageDialog(mainPanel, "Categoría eliminada (baja lógica) exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    limpiarCampos();
                    cargarDatosEnTabla(null);
                } else {
                    JOptionPane.showMessageDialog(mainPanel, "Error al eliminar la categoría.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(mainPanel, "Error de base de datos: " + ex.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }
}
