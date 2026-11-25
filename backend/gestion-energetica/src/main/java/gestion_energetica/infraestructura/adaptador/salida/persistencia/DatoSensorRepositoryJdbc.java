
package gestion_energetica.infraestructura.adaptador.salida.persistencia;

import gestion_energetica.aplicacion.puerto.salida.DatoSensorRepository;
import gestion_energetica.dominio.modelo.DatoSensor;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public class DatoSensorRepositoryJdbc implements DatoSensorRepository {

    private final JdbcTemplate jdbcTemplate;

    public DatoSensorRepositoryJdbc(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean existePorId(String idDato) {

        Integer cantidad = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM dbo.DatoSensor WHERE idDato = ?",
            Integer.class,
            idDato
        );

        return cantidad != null && cantidad > 0;
    }


@Override
public boolean equipoPerteneceATwin(
        String idEquipo,
        String idTwin) {

    Integer cantidad = jdbcTemplate.queryForObject(
        """
        SELECT COUNT(*)
        FROM dbo.Equipo
        WHERE idEquipo = ?
          AND idTwin = ?
        """,
        Integer.class,
        idEquipo,
        idTwin
    );

    return cantidad != null && cantidad > 0;
}



@Override
public List<DatoSensor> consultarUltimasMediciones(int limite) {

    return jdbcTemplate.query(
        "EXEC dbo.sp_ConsultarUltimasMediciones @limite = ?",
        (rs, rowNum) -> new DatoSensor(
            rs.getString("idDato"),
            rs.getString("idSensor"),
            rs.getBigDecimal("valor"),
            rs.getTimestamp("timestamp").toLocalDateTime(),
            rs.getString("idTwin"),
            rs.getString("idEquipo"),
            rs.getString("variable"),
            rs.getString("unidad")
        ),
        limite
    );
}


    @Override
    public void guardar(DatoSensor dato) {

        jdbcTemplate.update(
            "EXEC dbo.sp_GuardarDatoSensor ?, ?, ?, ?, ?, ?, ?, ?",
            dato.getIdDato(),
            dato.getIdSensor(),
            dato.getValor(),
            Timestamp.valueOf(dato.getTimestamp()),
            dato.getIdTwin(),
            dato.getIdEquipo(),
            dato.getVariable(),
            dato.getUnidad()
        );
    }
}
