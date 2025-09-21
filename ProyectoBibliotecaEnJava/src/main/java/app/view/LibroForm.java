
package app.view;

import app.dao.AutorDAO;
import app.dao.CategoriaDAO;
import app.dao.LibroDAO;
import app.model.Autor;
import app.model.Categoria;
import app.model.Libro;
import app.model.LibroConAutor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class LibroForm {
    public JPanel mainPanel;
    private JTextField txtNombre;
    private JTextField txtAnio;
    private JComboBox<Item> cbAutor;
    private JComboBox<Item> cbCategoria;
    private JCheckBox chkEstado;
    private JButton btnGuardar;
    private JButton btnActualizar;
    private JButton btnEliminar;
    private JButton btnLimpiar;
    private JTable tableLibros;
    private JTextField txtBuscar;

    private final LibroDAO libroDAO;
    private final AutorDAO autorDAO;
    private final CategoriaDAO categoriaDAO;
    private int libroSeleccionadoId = -1;

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

    public LibroForm() {
        libroDAO = new LibroDAO();
        autorDAO = new AutorDAO();
        categoriaDAO = new CategoriaDAO();

        cargarAutores();
        cargarCategorias();
        cargarDatosEnTabla(null);

        btnLimpiar.addActionListener(e -> limpiarCampos());
        btnGuardar.addActionListener(e -> guardarLibro());
        btnActualizar.addActionListener(e -> actualizarLibro());
        btnEliminar.addActionListener(e -> eliminarLibro());

        tableLibros.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int filaSeleccionada = tableLibros.getSelectedRow();
                if (filaSeleccionada != -1) {
                    libroSeleccionadoId = (int) tableLibros.getValueAt(filaSeleccionada, 0);
                    txtNombre.setText((String) tableLibros.getValueAt(filaSeleccionada, 1));
                    txtAnio.setText(String.valueOf(tableLibros.getValueAt(filaSeleccionada, 2)));

                    // Cargar el autor y la categoría del libro seleccionado en los JComboBox
                    String autorNombre = (String) tableLibros.getValueAt(filaSeleccionada, 3);
                    String categoriaNombre = (String) tableLibros.getValueAt(filaSeleccionada, 4);
                    setComboBoxSelection(cbAutor, autorNombre);
                    setComboBoxSelection(cbCategoria, categoriaNombre);

                    String estadoTexto = (String) tableLibros.getValueAt(filaSeleccionada, 5);
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

    private void cargarAutores() {
        try {
            cbAutor.removeAllItems();
            List<Autor> listaAutores = autorDAO.listar();
            for (Autor a : listaAutores) {
                cbAutor.addItem(new Item(a.getId(), a.getNombre()));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(mainPanel, "Error al cargar autores: " + e.getMessage(), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void cargarCategorias() {
        try {
            cbCategoria.removeAllItems();
            List<Categoria> listaCategorias = categoriaDAO.listar();
            for (Categoria c : listaCategorias) {
                cbCategoria.addItem(new Item(c.getId(), c.getNombre()));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(mainPanel, "Error al cargar categorías: " + e.getMessage(), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void cargarDatosEnTabla(String filtro) {
        String[] columnas = {"ID", "Nombre", "Año", "Autor", "Categoría", "Estado"};
        DefaultTableModel model = new DefaultTableModel(null, columnas);
        tableLibros.setModel(model);

        try {
            List<LibroConAutor> listaLibros;
            if (filtro != null && !filtro.isEmpty()) {
                listaLibros = libroDAO.buscarPorNombre(filtro);
            } else {
                listaLibros = libroDAO.listarConAutorYCategoria();
            }

            for (LibroConAutor l : listaLibros) {
                String estado = (l.getEstado() == 1) ? "Activo" : "Inactivo";
                model.addRow(new Object[]{l.getId(), l.getNombre(), l.getAnio(), l.getAutorNombre(), l.getCategoriaNombre(), estado});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(mainPanel, "Error al cargar los datos: " + e.getMessage(), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void limpiarCampos() {
        txtNombre.setText("");
        txtAnio.setText("");
        cbAutor.setSelectedIndex(0);
        cbCategoria.setSelectedIndex(0);
        chkEstado.setSelected(true);
        libroSeleccionadoId = -1;
    }

    private void guardarLibro() {
        String nombre = txtNombre.getText().trim();
        String anioTexto = txtAnio.getText().trim();
        int idAutor = ((Item) cbAutor.getSelectedItem()).getId();
        int idCategoria = ((Item) cbCategoria.getSelectedItem()).getId();

        if (nombre.isEmpty() || anioTexto.isEmpty()) {
            JOptionPane.showMessageDialog(mainPanel, "Los campos Nombre y Año son obligatorios.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int anio = Integer.parseInt(anioTexto);
            int estado = chkEstado.isSelected() ? 1 : 0;
            Libro nuevoLibro = new Libro(nombre, anio, idAutor, idCategoria, estado);

            int id = libroDAO.insertar(nuevoLibro);
            if (id != -1) {
                JOptionPane.showMessageDialog(mainPanel, "Libro guardado exitosamente con ID: " + id, "Éxito", JOptionPane.INFORMATION_MESSAGE);
                limpiarCampos();
                cargarDatosEnTabla(null);
            } else {
                JOptionPane.showMessageDialog(mainPanel, "Error al guardar el libro.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(mainPanel, "El campo Año debe ser un número válido.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(mainPanel, "Error de base de datos: " + ex.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void actualizarLibro() {
        if (libroSeleccionadoId == -1) {
            JOptionPane.showMessageDialog(mainPanel, "Seleccione un libro de la tabla para actualizar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String nombre = txtNombre.getText().trim();
        String anioTexto = txtAnio.getText().trim();
        int idAutor = ((Item) cbAutor.getSelectedItem()).getId();
        int idCategoria = ((Item) cbCategoria.getSelectedItem()).getId();

        if (nombre.isEmpty() || anioTexto.isEmpty()) {
            JOptionPane.showMessageDialog(mainPanel, "Los campos Nombre y Año son obligatorios.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int anio = Integer.parseInt(anioTexto);
            int estado = chkEstado.isSelected() ? 1 : 0;
            Libro libroActualizado = new Libro(libroSeleccionadoId, nombre, anio, idAutor, idCategoria, estado);

            boolean exito = libroDAO.actualizar(libroActualizado);
            if (exito) {
                JOptionPane.showMessageDialog(mainPanel, "Libro actualizado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                limpiarCampos();
                cargarDatosEnTabla(null);
            } else {
                JOptionPane.showMessageDialog(mainPanel, "Error al actualizar el libro.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(mainPanel, "El campo Año debe ser un número válido.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(mainPanel, "Error de base de datos: " + ex.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void eliminarLibro() {
        if (libroSeleccionadoId == -1) {
            JOptionPane.showMessageDialog(mainPanel, "Seleccione un libro de la tabla para eliminar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(mainPanel,
                "¿Está seguro de que desea eliminar lógicamente este libro?",
                "Confirmar Eliminación",
                JOptionPane.YES_NO_OPTION);

        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                boolean exito = libroDAO.eliminar(libroSeleccionadoId);
                if (exito) {
                    JOptionPane.showMessageDialog(mainPanel, "Libro eliminado (baja lógica) exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    limpiarCampos();
                    cargarDatosEnTabla(null);
                } else {
                    JOptionPane.showMessageDialog(mainPanel, "Error al eliminar el libro.", "Error", JOptionPane.ERROR_MESSAGE);
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