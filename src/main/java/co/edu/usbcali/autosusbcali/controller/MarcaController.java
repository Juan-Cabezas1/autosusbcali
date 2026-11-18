package co.edu.usbcali.autosusbcali.controller;

import co.edu.usbcali.autosusbcali.dto.request.CrearMarcaRequest;
import co.edu.usbcali.autosusbcali.dto.response.ObtenerMarcaResponse;
import co.edu.usbcali.autosusbcali.service.IMarcaService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/marcas")
@AllArgsConstructor
public class MarcaController {
    private final IMarcaService marcaService;

    // GET http://localhost:8080/api/marcas
    @GetMapping
    public List<ObtenerMarcaResponse> obtenerTodas() {
        return marcaService.obtenerTodas();
    }

    // GET http://localhost:8080/api/marcas/activas
    @GetMapping("/activas")
    public List<ObtenerMarcaResponse> obtenerActivas() {
        return marcaService.buscarMarcaPorEstado(true);
    }

    // GET http://localhost:8080/api/marcas/inactivas
    @GetMapping("/inactivas")
    public List<ObtenerMarcaResponse> obtenerInactivas() {
        return marcaService.buscarMarcaPorEstado(false);
    }

    // GET http://localhost:8080/api/marcas/por-nombre/Toyota
    @GetMapping("/por-nombre/{nombre}")
    public ResponseEntity<?> obtenerPorNombre(@PathVariable String nombre) {
        try {
            return new ResponseEntity<>(marcaService.obtenerMarcaPorNombre(nombre), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    // GET http://localhost:8080/api/marcas/por-id/1
    @GetMapping("/por-id/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        try {
            return new ResponseEntity<>(marcaService.obtenerMarcaPorId(id), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    // POST http://localhost:8080/api/marcas   Body: { "nombre": "Toyota" }
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody CrearMarcaRequest crearMarcaRequest) {
        try {
            return new ResponseEntity<>(marcaService.crearMarca(crearMarcaRequest), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // PUT http://localhost:8080/api/marcas/1   Body: { "nombre": "Toyota Motor" }
    @PutMapping("/{id}")
    public ResponseEntity<?> modificar(@PathVariable Long id,
                                       @RequestBody CrearMarcaRequest modificarMarcaRequest) {
        try {
            return new ResponseEntity<>(marcaService.modificarMarca(id, modificarMarcaRequest), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // DELETE http://localhost:8080/api/marcas/1
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            marcaService.eliminarMarca(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204: se elimino
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}