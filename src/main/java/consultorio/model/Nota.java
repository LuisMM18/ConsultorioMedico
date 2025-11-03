package consultorio.model;
import java.time.LocalDate;

public class Nota {
    private int idNotas;
    private int idCitasRef;
    private String titulo;
    private String textoNota;
    private LocalDate fechaNota; // NUEVO

    public int getIdNotas() { return idNotas; }
    public void setIdNotas(int idNotas) { this.idNotas = idNotas; }

    public int getIdCitasRef() { return idCitasRef; }
    public void setIdCitasRef(int idCitasRef) { this.idCitasRef = idCitasRef; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getTextoNota() { return textoNota; }
    public void setTextoNota(String textoNota) { this.textoNota = textoNota; }

    public LocalDate getFechaNota() { return fechaNota; }
    public void setFechaNota(LocalDate fechaNota) { this.fechaNota = fechaNota; }


    @Override public String toString() {
        return (titulo == null || titulo.isBlank()) ? "(Sin título)" : titulo;
    }
}
