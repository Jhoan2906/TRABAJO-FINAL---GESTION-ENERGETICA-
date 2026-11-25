
package gestion_energetica.aplicacion.caso_uso;

import gestion_energetica.aplicacion.puerto.entrada.CapturarDatoUseCase;
import gestion_energetica.aplicacion.puerto.salida.DatoSensorRepository;
import gestion_energetica.aplicacion.puerto.salida.RegistroIngestaErrorRepository;
import gestion_energetica.dominio.modelo.DatoSensor;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Locale;

import org.springframework.stereotype.Service;

@Service
public class CapturarDatoService implements CapturarDatoUseCase {

    private final DatoSensorRepository repository;
    private final RegistroIngestaErrorRepository erroresRepository;

    // Constructor
    public CapturarDatoService(
            DatoSensorRepository repository,
            RegistroIngestaErrorRepository erroresRepository) {

        this.repository = repository;
        this.erroresRepository = erroresRepository;
    }

    @Override
    public DatoSensor capturarDato(DatoSensor dato) {

        // 1. Validar existencia de la medicion
        if (dato == null) {
            rechazar(null, "La medicion no puede ser nula");
        }

        // 2. Validar campos obligatorios
        if (esVacio(dato.getIdDato()) ||
            esVacio(dato.getIdSensor()) ||
            esVacio(dato.getIdTwin()) ||
            esVacio(dato.getIdEquipo()) ||
            esVacio(dato.getVariable()) ||
            esVacio(dato.getUnidad()) ||
            dato.getValor() == null ||
            dato.getTimestamp() == null) {

            rechazar(dato,
                "La medicion contiene campos obligatorios vacios");
        }

        // 3. Validar longitudes
        if (dato.getIdDato().length() > 50 ||
            dato.getIdSensor().length() > 50 ||
            dato.getIdTwin().length() > 50 ||
            dato.getIdEquipo().length() > 50 ||
            dato.getVariable().length() > 50 ||
            dato.getUnidad().length() > 20) {

            rechazar(dato,
                "La medicion supera la longitud permitida");
        }

        // 4. Validar variable, unidad y rango
        validarCalidadMedicion(dato);

        // 5. Validar fecha y hora de la medicion
        validarTimestamp(dato);

        // 6. Validar asociacion entre equipo y gemelo digital
        if (!repository.equipoPerteneceATwin(
                dato.getIdEquipo(),
                dato.getIdTwin())) {

            rechazar(dato,
                "El equipo no existe o no pertenece al gemelo digital indicado");
        }

        // 7. Comprobar duplicados
        if (repository.existePorId(dato.getIdDato())) {

            String motivo =
                "La medicion ya existe: " + dato.getIdDato();

            registrarRechazo(dato, motivo);

            throw new IllegalStateException(motivo);
        }

        // 8. Persistir la medicion valida
        repository.guardar(dato);

        return dato;
    }

    // Validar variable, unidad y rango permitido
    private void validarCalidadMedicion(DatoSensor dato) {

        String variable = dato.getVariable()
                              .trim()
                              .toLowerCase(Locale.ROOT);

        String unidad = dato.getUnidad().trim();
        BigDecimal valor = dato.getValor();

        switch (variable) {

            case "temperatura":
                if (!unidad.equals("C") && !unidad.equals("°C")) {
                    rechazar(dato,
                        "Unidad invalida para Temperatura");
                }
                validarRango(dato, valor, "-20", "60");
                break;

            case "humedad relativa":
                if (!unidad.equals("%")) {
                    rechazar(dato,
                        "Unidad invalida para Humedad relativa");
                }
                validarRango(dato, valor, "0", "100");
                break;

            case "potencia electrica":
            case "potencia eléctrica":
                if (!unidad.equals("kW")) {
                    rechazar(dato,
                        "Unidad invalida para Potencia electrica");
                }
                validarRango(dato, valor, "0", "500");
                break;

            case "energia electrica":
            case "energía eléctrica":
                if (!unidad.equals("kWh")) {
                    rechazar(dato,
                        "Unidad invalida para Energia electrica");
                }
                validarRango(dato, valor, "0", "100000");
                break;

            default:
                rechazar(dato,
                    "Variable de medicion no reconocida: "
                    + dato.getVariable());
        }
    }

    // Validar rango de la medicion
    private void validarRango(
            DatoSensor dato,
            BigDecimal valor,
            String minimo,
            String maximo) {

        BigDecimal min = new BigDecimal(minimo);
        BigDecimal max = new BigDecimal(maximo);

        if (valor.compareTo(min) < 0 ||
            valor.compareTo(max) > 0) {

            rechazar(dato,
                "Valor fuera del rango permitido para "
                + dato.getVariable());
        }
    }

    // Validar timestamp en UTC
    private void validarTimestamp(DatoSensor dato) {

        LocalDateTime ahoraUtc =
            LocalDateTime.now(Clock.systemUTC());

        LocalDateTime fechaMedicion = dato.getTimestamp();

        if (fechaMedicion.isAfter(ahoraUtc)) {
            rechazar(dato,
                "La fecha de medicion no puede ser futura");
        }

        if (fechaMedicion.isBefore(ahoraUtc.minusDays(30))) {
            rechazar(dato,
                "La fecha de medicion supera los 30 dias permitidos");
        }
    }

    // Rechazar una medicion y registrar el motivo
    private void rechazar(DatoSensor dato, String motivo) {
        registrarRechazo(dato, motivo);
        throw new IllegalArgumentException(motivo);
    }

    // Registrar el rechazo mediante el puerto de salida
    private void registrarRechazo(DatoSensor dato, String motivo) {

        DatoSensor datoSeguro = dato;

        if (dato != null) {
            datoSeguro = new DatoSensor(
                limitar(dato.getIdDato(), 50),
                limitar(dato.getIdSensor(), 50),
                dato.getValor(),
                dato.getTimestamp(),
                dato.getIdTwin(),
                limitar(dato.getIdEquipo(), 50),
                limitar(dato.getVariable(), 50),
                limitar(dato.getUnidad(), 20)
            );
        }

        erroresRepository.registrarRechazo(
            datoSeguro,
            limitar(motivo, 400)
        );
    }

    // Limitar longitud de los textos registrados como errores
    private String limitar(String texto, int maximo) {
        if (texto == null) {
            return null;
        }

        return texto.length() <= maximo
                ? texto
                : texto.substring(0, maximo);
    }

    // Verificar campos vacios
    private boolean esVacio(String texto) {
        return texto == null || texto.isBlank();
    }
}
