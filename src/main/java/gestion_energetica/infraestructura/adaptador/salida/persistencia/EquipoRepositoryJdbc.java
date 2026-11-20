package gestion_energetica.infraestructura.adaptador.salida.persistencia;

import gestion_energetica.aplicacion.puerto.salida.EquipoRepository;
import gestion_energetica.dominio.modelo.Equipo;

import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class EquipoRepositoryJdbc implements EquipoRepository {

    @Override
    public List<Equipo> obtenerTodos() {
        return List.of();
    }

}