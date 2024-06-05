package proyecto.SistemaPago.controladores;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import proyecto.SistemaPago.entidades.Transaccion;
import proyecto.SistemaPago.servicios.ServicioTransaccion;

import java.util.List;


@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class ControladorTransaccion {

    @Autowired
    ServicioTransaccion serviciotransaccion;

    @PostMapping("/transaccion")
    public ResponseEntity<Transaccion> realizarCargo(Transaccion nuevatransaccion) {
        var transaccionRealizada = serviciotransaccion.realizarCargo(nuevatransaccion);
        return ResponseEntity.status(HttpStatus.CREATED).body(transaccionRealizada);
    }

    @GetMapping("/transaccion")
    public ResponseEntity<List<Transaccion>> recuperarTransacciones() {
        var detallesTransaccionesDto = serviciotransaccion.recuperarTransacciones();
        return ResponseEntity.status(HttpStatus.OK).body(detallesTransaccionesDto);
    }

    @GetMapping("/transaccion/{id}")
    public ResponseEntity<Transaccion> recuperarTransaccion(String transactionId) {
        var detallesTransaccionDto = serviciotransaccion.recuperarTransaccion(transactionId);
        return ResponseEntity.status(HttpStatus.OK).body(detallesTransaccionDto);
    }

}
