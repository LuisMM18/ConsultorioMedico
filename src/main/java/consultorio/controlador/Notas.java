package consultorio.controlador;

import javafx.beans.property.*;
import java.time.LocalDate;

public class Notas {
    private final IntegerProperty id;
    private final StringProperty titulo;
    private final StringProperty contenido;
    private final ObjectProperty<LocalDate> fecha;

    public Notas(int id, String titulo, String contenido, LocalDate fecha) {
        this.id = new SimpleIntegerProperty(id);
        this.titulo = new SimpleStringProperty(titulo);
        this.contenido = new SimpleStringProperty(contenido);
        this.fecha = new SimpleObjectProperty<>(fecha);
    }

    public Notas(IntegerProperty id, StringProperty titulo, StringProperty contenido, ObjectProperty<LocalDate> fecha) {
        this.id = id;
        this.titulo = titulo;
        this.contenido = contenido;
        this.fecha = fecha;
    }

    public int getId() { return id.get(); }
    public String getTitulo() { return titulo.get(); }
    public String getContenido() { return contenido.get(); }
    public LocalDate getFecha() { return fecha.get(); }

    public IntegerProperty idProperty() { return id; }
    public StringProperty tituloProperty() { return titulo; }
    public StringProperty contenidoProperty() { return contenido; }
    public ObjectProperty<LocalDate> fechaProperty() { return fecha; }
}

