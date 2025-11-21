package consultorio.model;

public class Usuario {
    private int idUsuario;
    private String nombre;
    private String apellidoUNO;
    private String apellidoDOS;
    private String correo;
    private String telefono;
    private String contrasena;
    private int rol;

    public Usuario() {}

    @Override
    public String toString() {
        return getNombreCompleto() + " (" + correo + ")";
    }


    public String getNombreCompleto() {
        String s = nombre == null ? "" : nombre;
        if (apellidoUNO != null && !apellidoUNO.isEmpty()) s += " " + apellidoUNO;
        if (apellidoDOS != null && !apellidoDOS.isEmpty()) s += " " + apellidoDOS;
        return s.trim();
    }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellidoUNO() { return apellidoUNO; }
    public void setApellidoUNO(String apellidoUNO) { this.apellidoUNO = apellidoUNO; }

    public String getApellidoDOS() { return apellidoDOS; }
    public void setApellidoDOS(String apellidoDOS) { this.apellidoDOS = apellidoDOS; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }

    public int getRol() { return rol; }
    public void setRol(int rol) { this.rol = rol; }
}