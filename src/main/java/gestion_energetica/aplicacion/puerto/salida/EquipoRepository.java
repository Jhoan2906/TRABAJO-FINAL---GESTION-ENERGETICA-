package gestion_energetica.aplicacion.puerto.salida;

import gestion_energetica.dominio.modelo.Equipo;
import java.util.List;

public interface EquipoRepository {

    List<Equipo> obtenerTodos();

}