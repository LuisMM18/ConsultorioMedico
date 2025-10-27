package consultorio.model;

public class Nota {
    private int idNotas;
    private int idCitasRef;
    private String titulo;
    private String textoNota;

    public int getIdNotas() { return idNotas; }
    public void setIdNotas(int idNotas) { this.idNotas = idNotas; }

    public int getIdCitasRef() { return idCitasRef; }
    public void setIdCitasRef(int idCitasRef) { this.idCitasRef = idCitasRef; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getTextoNota() { return textoNota; }
    public void setTextoNota(String textoNota) { this.textoNota = textoNota; }

    @Override public String toString() {
        return (titulo == null || titulo.isBlank()) ? "(Sin título)" : titulo;
    }
}
