
package gestion_energetica.infraestructura.adaptador.salida.persistencia;

import gestion_energetica.aplicacion.puerto.salida.EquipoRepository;
import gestion_energetica.dominio.modelo.Equipo;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class EquipoRepositoryJdbc implements EquipoRepository {

    private final JdbcTemplate jdbcTemplate;

    public EquipoRepositoryJdbc(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Equipo> obtenerTodos() {

        return jdbcTemplate.query(
            "EXEC dbo.sp_ConsultarEquipos",
            (rs, rowNum) -> new Equipo(
                rs.getString("idEquipo"),
                rs.getString("tipo"),
                rs.getString("estado"),
                rs.getString("ubicacion"),
                rs.getString("idTwin")
            )
        );
    }
}
