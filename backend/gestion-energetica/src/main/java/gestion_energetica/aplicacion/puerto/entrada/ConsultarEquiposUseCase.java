package gestion_energetica.aplicacion.puerto.entrada;

import gestion_energetica.dominio.modelo.Equipo;
import java.util.List;

public interface ConsultarEquiposUseCase {

    List<Equipo> consultarEquipos();

}