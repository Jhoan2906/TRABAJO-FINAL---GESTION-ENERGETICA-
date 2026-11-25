
package gestion_energetica.dominio.modelo;

public class Equipo {

    private String idEquipo;
    private String tipo;
    private String estado;
    private String ubicacion;
    private String idTwin;

    public Equipo(String idEquipo, String tipo, String estado,
                  String ubicacion, String idTwin) {

        this.idEquipo = idEquipo;
        this.tipo = tipo;
        this.estado = estado;
        this.ubicacion = ubicacion;
        this.idTwin = idTwin;
    }

    public String getIdEquipo() {
        return idEquipo;
    }

    public String getTipo() {
        return tipo;
    }

    public String getEstado() {
        return estado;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public String getIdTwin() {
        return idTwin;
    }
}
