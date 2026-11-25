package gestion_energetica.infraestructura.adaptador.entrada.web;

import gestion_energetica.aplicacion.puerto.entrada.ConsultarEquiposUseCase;
import gestion_energetica.dominio.modelo.Equipo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/equipos")
public class EquipoController {

    private final ConsultarEquiposUseCase consultarEquiposUseCase;

    public EquipoController(ConsultarEquiposUseCase consultarEquiposUseCase) {
        this.consultarEquiposUseCase = consultarEquiposUseCase;
    }

    @GetMapping
    public List<Equipo> consultarEquipos() {
        return consultarEquiposUseCase.consultarEquipos();
    }
}