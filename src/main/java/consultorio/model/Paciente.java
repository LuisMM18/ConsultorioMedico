package consultorio.model;

public class Paciente {
    private int idPaciente;
    private String nombre;
    private String apellidoUNO;
    private String apellidoDOS;

    public int getIdPaciente() { return idPaciente; }
    public void setIdPaciente(int idPaciente) { this.idPaciente = idPaciente; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellidoUNO() { return apellidoUNO; }
    public void setApellidoUNO(String apellidoUNO) { this.apellidoUNO = apellidoUNO; }
    public String getApellidoDOS() { return apellidoDOS; }
    public void setApellidoDOS(String apellidoDOS) { this.apellidoDOS = apellidoDOS; }
    public String getNombreCompleto() {
        String s = nombre == null ? "" : nombre;
        if (apellidoUNO != null && !apellidoUNO.isEmpty()) s += " " + apellidoUNO;
        if (apellidoDOS != null && !apellidoDOS.isEmpty()) s += " " + apellidoDOS;
        return s.trim();
    }

    @Override
    public String toString() {
        // Esto es lo que verá el ComboBox
        return getNombreCompleto();
    }



}