package app.view;

import app.dao.AutorDAO;
import app.model.Autor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.List;

public class AutorForm {
    public JPanel mainPanel;
    private JTable tableAutores;
    private JTextField txtNombre;
    private JTextArea txtBiografia;
    private JButton btnGuardar;
    private JButton btnLimpiar;
    private JButton btnActualizar;
    private JButton btnEliminar;
    private JCheckBox chkEstado;
    private JTextField txtBuscar;

    private final AutorDAO autorDAO;
    private int autorSeleccionadoId = -1; // Usado para guardar el ID del autor seleccionado en la tabla

    public AutorForm() {
        // Inicializa el DAO para la conexión a la base de datos
        autorDAO = new AutorDAO();
        // Carga los datos en la tabla al iniciar el formulario sin filtro
        cargarDatosEnTabla(null);

        // Limpiar los campos
        btnLimpiar.addActionListener(e -> limpiarCampos());
        // Guardar un nuevo autor
        btnGuardar.addActionListener(e -> guardarAutor());
        // Actualizar un autor existente
        btnActualizar.addActionListener(e -> actualizarAutor());
        // Eliminar un autor (baja lógica)
        btnEliminar.addActionListener(e -> eliminarAutor());

        // Evento de clic en la tabla para cargar datos en los campos
        tableAutores.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int filaSeleccionada = tableAutores.getSelectedRow();
                if (filaSeleccionada != -1) {
                    // Obtener el ID de la fila seleccionada
                    autorSeleccionadoId = (int) tableAutores.getValueAt(filaSeleccionada, 0);

                    // Cargar los datos en los campos
                    txtNombre.setText((String) tableAutores.getValueAt(filaSeleccionada, 1));
                    txtBiografia.setText((String) tableAutores.getValueAt(filaSeleccionada, 2));
                    // El estado se obtiene como un String del modelo y se convierte a boolean
                    String estadoTexto = (String) tableAutores.getValueAt(filaSeleccionada, 3);
                    chkEstado.setSelected(estadoTexto.equals("Activo"));
                }
            }
        });

        // Evento para el buscador: cada vez que se presiona una tecla
        txtBuscar.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                // Obtiene el texto del buscador y lo usa para actualizar la tabla
                String filtro = txtBuscar.getText().trim();
                cargarDatosEnTabla(filtro);
            }
        });

    }

    // Metodo principal para cargar los datos en la tabla. Ahora acepta un filtro.
    private void cargarDatosEnTabla(String filtro) {
        String[] columnas = {"ID", "Nombre", "Biografía", "Estado"};
        DefaultTableModel model = new DefaultTableModel(null, columnas);
        tableAutores.setModel(model);

        try {
            List<Autor> listaAutores;
            // Si el filtro no es nulo y no está vacío, usamos el método de búsqueda
            if (filtro != null && !filtro.isEmpty()) {
                listaAutores = autorDAO.buscarPorNombre(filtro);
            } else {
                // Si no hay filtro, listamos todos los autores
                listaAutores = autorDAO.listar();
            }

            for (Autor a : listaAutores) {
                String estado = (a.getEstado() == 1) ? "Activo" : "Inactivo";
                model.addRow(new Object[]{a.getId(), a.getNombre(), a.getBiografia(), estado});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(mainPanel, "Error al cargar los datos: " + e.getMessage(), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void limpiarCampos() {
        txtNombre.setText("");
        txtBiografia.setText("");
        chkEstado.setSelected(true); // Por defecto, el estado es activo
        autorSeleccionadoId = -1;
    }

    private void guardarAutor() {
        String nombre = txtNombre.getText().trim();
        String biografia = txtBiografia.getText().trim();
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(mainPanel, "El campo Nombre es obligatorio.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int estado = chkEstado.isSelected() ? 1 : 0;
        Autor nuevoAutor = new Autor(nombre, biografia, estado);
        try {
            int id = autorDAO.insertar(nuevoAutor);
            if (id != -1) {
                JOptionPane.showMessageDialog(mainPanel, "Autor guardado exitosamente con ID: " + id, "Éxito", JOptionPane.INFORMATION_MESSAGE);
                limpiarCampos();
                cargarDatosEnTabla(null);
            } else {
                JOptionPane.showMessageDialog(mainPanel, "Error al guardar el autor.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(mainPanel, "Error de base de datos: " + ex.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void actualizarAutor() {
        if (autorSeleccionadoId == -1) {
            JOptionPane.showMessageDialog(mainPanel, "Seleccione un autor de la tabla para actualizar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String nombre = txtNombre.getText().trim();
        String biografia = txtBiografia.getText().trim();
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(mainPanel, "El campo Nombre es obligatorio.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int estado = chkEstado.isSelected() ? 1 : 0;
        Autor autorActualizado = new Autor(autorSeleccionadoId, nombre, biografia, estado);
        try {
            boolean exito = autorDAO.actualizar(autorActualizado);
            if (exito) {
                JOptionPane.showMessageDialog(mainPanel, "Autor actualizado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                limpiarCampos();
                cargarDatosEnTabla(null);
            } else {
                JOptionPane.showMessageDialog(mainPanel, "Error al actualizar el autor.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(mainPanel, "Error de base de datos: " + ex.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void eliminarAutor() {
        if (autorSeleccionadoId == -1) {
            JOptionPane.showMessageDialog(mainPanel, "Seleccione un autor de la tabla para eliminar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirmacion = JOptionPane.showConfirmDialog(mainPanel,
                "¿Está seguro de que desea eliminar lógicamente este autor?",
                "Confirmar Eliminación",
                JOptionPane.YES_NO_OPTION);
        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                boolean exito = autorDAO.eliminar(autorSeleccionadoId);
                if (exito) {
                    JOptionPane.showMessageDialog(mainPanel, "Autor eliminado (baja lógica) exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    limpiarCampos();
                    cargarDatosEnTabla(null);
                } else {
                    JOptionPane.showMessageDialog(mainPanel, "Error al eliminar el autor.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(mainPanel, "Error de base de datos: " + ex.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

}