
package gestion_energetica.infraestructura.adaptador.salida.persistencia;

import gestion_energetica.aplicacion.puerto.salida.RegistroIngestaErrorRepository;
import gestion_energetica.dominio.modelo.DatoSensor;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class RegistroIngestaErrorRepositoryJdbc
        implements RegistroIngestaErrorRepository {

    private final JdbcTemplate jdbcTemplate;

    public RegistroIngestaErrorRepositoryJdbc(
            JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void registrarRechazo(DatoSensor dato, String motivo) {

        jdbcTemplate.update(
            "EXEC dbo.sp_RegistrarIngestaError ?, ?, ?, ?, ?, ?",
            dato != null ? dato.getIdDato() : null,
            dato != null ? dato.getIdSensor() : null,
            dato != null ? dato.getIdEquipo() : null,
            dato != null ? dato.getVariable() : null,
            dato != null ? dato.getUnidad() : null,
            motivo
        );
    }
}
