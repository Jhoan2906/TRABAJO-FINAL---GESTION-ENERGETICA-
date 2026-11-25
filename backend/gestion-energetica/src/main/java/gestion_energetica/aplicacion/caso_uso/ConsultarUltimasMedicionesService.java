
package gestion_energetica.aplicacion.caso_uso;

import gestion_energetica.aplicacion.puerto.entrada.ConsultarUltimasMedicionesUseCase;
import gestion_energetica.aplicacion.puerto.salida.DatoSensorRepository;
import gestion_energetica.dominio.modelo.DatoSensor;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class ConsultarUltimasMedicionesService
        implements ConsultarUltimasMedicionesUseCase {

    private final DatoSensorRepository repository;

    public ConsultarUltimasMedicionesService(
            DatoSensorRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<DatoSensor> consultarUltimasMediciones(int limite) {

        if (limite < 1 || limite > 100) {
            throw new IllegalArgumentException(
                "El limite debe estar entre 1 y 100"
            );
        }

        return repository.consultarUltimasMediciones(limite);
    }
}
