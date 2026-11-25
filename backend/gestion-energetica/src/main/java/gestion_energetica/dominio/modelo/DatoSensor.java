
package gestion_energetica.dominio.modelo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class DatoSensor {

    private String idDato;
    private String idSensor;
    private BigDecimal valor;
    private LocalDateTime timestamp;
    private String idTwin;
    private String idEquipo;
    private String variable;
    private String unidad;

    public DatoSensor(
            String idDato,
            String idSensor,
            BigDecimal valor,
            LocalDateTime timestamp,
            String idTwin,
            String idEquipo,
            String variable,
            String unidad) {

        this.idDato = idDato;
        this.idSensor = idSensor;
        this.valor = valor;
        this.timestamp = timestamp;
        this.idTwin = idTwin;
        this.idEquipo = idEquipo;
        this.variable = variable;
        this.unidad = unidad;
    }

    public String getIdDato() {
        return idDato;
    }

    public String getIdSensor() {
        return idSensor;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getIdTwin() {
        return idTwin;
    }

    public String getIdEquipo() {
        return idEquipo;
    }

    public String getVariable() {
        return variable;
    }

    public String getUnidad() {
        return unidad;
    }
}
