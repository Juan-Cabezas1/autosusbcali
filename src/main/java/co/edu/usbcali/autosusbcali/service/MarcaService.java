package co.edu.usbcali.autosusbcali.service;

import co.edu.usbcali.autosusbcali.domain.Marca;
import co.edu.usbcali.autosusbcali.dto.request.CrearMarcaRequest;
import co.edu.usbcali.autosusbcali.dto.response.ObtenerMarcaResponse;
import co.edu.usbcali.autosusbcali.mapper.MarcaMapper;
import co.edu.usbcali.autosusbcali.repository.MarcaRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class MarcaService implements IMarcaService {
    private final MarcaRepository marcaRepository;

    @Override
    public List<ObtenerMarcaResponse> obtenerTodas() {
        log.debug("Consultando todas las marcas");
        List<Marca> marcas = marcaRepository.findAll();
        log.debug("Se encontraron {} marcas", marcas.size());
        return MarcaMapper.listaMarcasAListaObtenerMarcaResponse(marcas);
    }

    @Override
    public List<ObtenerMarcaResponse> buscarMarcaPorEstado(Boolean activo) {
        log.debug("Consultando marcas con activo={}", activo);
        List<Marca> marcasPorEstado = marcaRepository.findByActivo(activo);
        log.debug("Se encontraron {} marcas con activo={}", marcasPorEstado.size(), activo);
        return MarcaMapper.listaMarcasAListaObtenerMarcaResponse(marcasPorEstado);
    }

    @Override
    public ObtenerMarcaResponse obtenerMarcaPorNombre(String nombre) throws Exception {
        log.debug("Buscando marca por nombre={}", nombre);
        Marca marca = marcaRepository.findByNombre(nombre)
                .orElseThrow(() -> {
                    // WARN: el sistema funciono bien, solo que la marca no existe
                    log.warn("No se encontro la marca con nombre={}", nombre);
                    return new Exception("No se ha encontrado la marca con nombre: " + nombre);
                });
        return MarcaMapper.marcaAObtenerMarcaResponse(marca);
    }

    @Override
    public ObtenerMarcaResponse obtenerMarcaPorId(Long id) throws Exception {
        log.debug("Buscando marca por id={}", id);
        Marca marca = marcaRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("No se encontro la marca con id={}", id);
                    return new Exception("No se ha encontrado la marca con id: " + id);
                });
        return MarcaMapper.marcaAObtenerMarcaResponse(marca);
    }

    @Override
    @Transactional
    public ObtenerMarcaResponse crearMarca(CrearMarcaRequest crearMarcaRequest) throws Exception {
        // Log de ENTRADA
        log.info("Iniciando creacion de marca: {}", crearMarcaRequest);

        // Validaciones del objeto crearMarcaRequest
        if (crearMarcaRequest == null) {
            log.warn("Intento de crear marca con request nulo");
            throw new Exception("El objeto crearMarcaRequest no puede ser nulo.");
        }
        if (crearMarcaRequest.nombre() == null
                || crearMarcaRequest.nombre().isBlank()) {
            log.warn("Intento de crear marca con nombre vacio");
            throw new Exception("El nombre no puede ser nulo ni estar vacio.");
        }

        // Validar que no exista otra marca con el mismo nombre
        if (marcaRepository.findByNombre(crearMarcaRequest.nombre()).isPresent()) {
            log.warn("Intento de crear marca duplicada con nombre={}", crearMarcaRequest.nombre());
            throw new Exception("Ya existe una marca con el nombre: " + crearMarcaRequest.nombre());
        }

        try {
            // Usar mapper para convertir el DTO a objeto de dominio
            Marca marca = MarcaMapper.crearMarcaRequestAMarca(crearMarcaRequest);

            // Persistir la Marca en base de datos
            marca = marcaRepository.save(marca);

            // Log de SALIDA (todo salio bien)
            log.info("Marca creada correctamente: id={}, nombre={}", marca.getId(), marca.getNombre());

            // Retornar el DTO de respuesta
            return MarcaMapper.marcaAObtenerMarcaResponse(marca);
        } catch (Exception e) {
            // La excepcion "e" va como ULTIMO parametro para imprimir el stack trace completo
            log.error("Error al guardar la marca {} en la base de datos", crearMarcaRequest.nombre(), e);
            throw e;
        }
    }

    @Override
    @Transactional
    public ObtenerMarcaResponse modificarMarca(Long id, CrearMarcaRequest modificarMarcaRequest) throws Exception {
        log.info("Iniciando modificacion de la marca id={} con datos {}", id, modificarMarcaRequest);

        // Validaciones de los parametros
        if (id == null) {
            log.warn("Intento de modificar marca con id nulo");
            throw new Exception("El id de la marca no puede ser nulo.");
        }
        if (modificarMarcaRequest == null) {
            log.warn("Intento de modificar marca id={} con request nulo", id);
            throw new Exception("El objeto modificarMarcaRequest no puede ser nulo.");
        }
        if (modificarMarcaRequest.nombre() == null
                || modificarMarcaRequest.nombre().isBlank()) {
            log.warn("Intento de modificar marca id={} con nombre vacio", id);
            throw new Exception("El nombre no puede ser nulo ni estar vacio.");
        }

        // Buscar la marca que se va a modificar
        Marca marca = marcaRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("No se puede modificar: no existe la marca con id={}", id);
                    return new Exception("No se ha encontrado la marca con id: " + id);
                });

        // Validar que el nuevo nombre no pertenezca a otra marca
        if (marcaRepository.findByNombre(modificarMarcaRequest.nombre())
                .filter(otraMarca -> !otraMarca.getId().equals(id))
                .isPresent()) {
            log.warn("No se puede modificar la marca id={}: ya existe otra marca con nombre={}",
                    id, modificarMarcaRequest.nombre());
            throw new Exception("Ya existe otra marca con el nombre: " + modificarMarcaRequest.nombre());
        }

        // Guardamos el valor anterior para el log de auditoria
        String nombreAnterior = marca.getNombre();

        // Actualizar los campos de la marca
        marca.setNombre(modificarMarcaRequest.nombre());

        try {
            // Persistir los cambios
            marca = marcaRepository.save(marca);
        } catch (Exception e) {
            log.error("Error al guardar la modificacion de la marca id={}", id, e);
            throw e;
        }

        log.info("Marca id={} modificada: nombre '{}' -> '{}'", id, nombreAnterior, marca.getNombre());

        // Retornar el DTO de respuesta
        return MarcaMapper.marcaAObtenerMarcaResponse(marca);
    }

    @Override
    @Transactional
    public void eliminarMarca(Long id) throws Exception {
        log.info("Iniciando eliminacion fisica de la marca id={}", id);

        if (id == null) {
            log.warn("Intento de eliminar marca con id nulo");
            throw new Exception("El id de la marca no puede ser nulo.");
        }

        // 1. Verificar que la marca exista antes de borrarla
        Marca marca = marcaRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("No se puede eliminar: no existe la marca con id={}", id);
                    return new Exception("No se ha encontrado la marca con id: " + id);
                });

        try {
            // 2. Eliminacion FISICA: la fila desaparece de la tabla marcas (DELETE)
            marcaRepository.delete(marca);

            // flush() obliga a ejecutar el DELETE ahora mismo, asi si falla lo atrapamos aqui
            marcaRepository.flush();

        } catch (DataIntegrityViolationException e) {
            // La marca tiene modelos asociados (tabla modelos, columna marca_id)
            log.warn("No se puede eliminar la marca id={} porque tiene modelos asociados", id);
            throw new Exception("No se puede eliminar la marca " + marca.getNombre()
                    + " porque tiene modelos asociados.");
        } catch (Exception e) {
            log.error("Error al eliminar fisicamente la marca id={}", id, e);
            throw e;
        }

        // 3. Log de AUDITORIA: como la fila ya no existe, este log es el unico rastro que queda
        log.info("Marca eliminada FISICAMENTE: id={}, nombre={}", marca.getId(), marca.getNombre());
    }
}