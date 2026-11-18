package co.edu.usbcali.autosusbcali.controller;

import co.edu.usbcali.autosusbcali.dto.request.CrearModeloRequest;
import co.edu.usbcali.autosusbcali.dto.response.ObtenerModeloResponse;
import co.edu.usbcali.autosusbcali.service.IModeloService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/modelos")
@AllArgsConstructor
public class ModeloController {
    private final IModeloService modeloService;

    // GET http://localhost:8080/api/modelos
    @GetMapping
    public List<ObtenerModeloResponse> obtenerTodos() {
        return modeloService.obtenerTodos();
    }

    // GET http://localhost:8080/api/modelos/por-marca/1
    @GetMapping("/por-marca/{marcaId}")
    public ResponseEntity<?> obtenerPorMarca(@PathVariable Long marcaId) {
        try {
            return new ResponseEntity<>(modeloService.buscarModelosPorMarca(marcaId), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    // GET http://localhost:8080/api/modelos/por-id/1
    @GetMapping("/por-id/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        try {
            return new ResponseEntity<>(modeloService.obtenerModeloPorId(id), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    // POST http://localhost:8080/api/modelos   Body: { "marcaId": 1, "nombre": "Corolla" }
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody CrearModeloRequest crearModeloRequest) {
        try {
            return new ResponseEntity<>(modeloService.crearModelo(crearModeloRequest), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // PUT http://localhost:8080/api/modelos/1   Body: { "marcaId": 1, "nombre": "Corolla Cross" }
    @PutMapping("/{id}")
    public ResponseEntity<?> modificar(@PathVariable Long id,
                                       @RequestBody CrearModeloRequest modificarModeloRequest) {
        try {
            return new ResponseEntity<>(modeloService.modificarModelo(id, modificarModeloRequest), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // DELETE http://localhost:8080/api/modelos/1
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            modeloService.eliminarModelo(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204: se elimino
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}