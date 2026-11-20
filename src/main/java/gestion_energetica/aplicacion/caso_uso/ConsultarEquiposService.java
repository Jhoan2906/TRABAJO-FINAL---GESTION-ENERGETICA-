package gestion_energetica.aplicacion.caso_uso;

import gestion_energetica.aplicacion.puerto.entrada.ConsultarEquiposUseCase;
import gestion_energetica.aplicacion.puerto.salida.EquipoRepository;
import gestion_energetica.dominio.modelo.Equipo;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ConsultarEquiposService implements ConsultarEquiposUseCase {

    private final EquipoRepository equipoRepository;

    public ConsultarEquiposService(EquipoRepository equipoRepository) {
        this.equipoRepository = equipoRepository;
    }

    @Override
    public List<Equipo> consultarEquipos() {
        return equipoRepository.obtenerTodos();
    }
}