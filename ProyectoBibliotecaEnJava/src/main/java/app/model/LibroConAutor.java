package app.model;

public class LibroConAutor extends Libro {
    private String autorNombre;
    private String categoriaNombre; // Nuevo atributo para el nombre de la categoria

    public LibroConAutor(int id, String nombre, int anio, String autorNombre, String categoriaNombre, int estado) {
        super(id, nombre, anio, 0, 0, estado); // Se usan 0 como placeholder para los IDs de FK
        this.autorNombre = autorNombre;
        this.categoriaNombre = categoriaNombre;
    }

    public String getAutorNombre() {
        return autorNombre;
    }

    public void setAutorNombre(String autorNombre) {
        this.autorNombre = autorNombre;
    }

    public String getCategoriaNombre() {
        return categoriaNombre;
    }

    public void setCategoriaNombre(String categoriaNombre) {
        this.categoriaNombre = categoriaNombre;
    }

    @Override
    public String toString() {
        return "LibroConAutor{" +
                "id=" + getId() +
                ", nombre='" + getNombre() + '\'' +
                ", anio=" + getAnio() +
                ", autorNombre='" + autorNombre + '\'' +
                ", categoriaNombre='" + categoriaNombre + '\'' +
                ", estado=" + getEstado() +
                '}';
    }
}