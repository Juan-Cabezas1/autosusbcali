package co.edu.usbcali.autosusbcali.service;

import co.edu.usbcali.autosusbcali.domain.Marca;
import co.edu.usbcali.autosusbcali.domain.Modelo;
import co.edu.usbcali.autosusbcali.dto.request.CrearModeloRequest;
import co.edu.usbcali.autosusbcali.dto.response.ObtenerModeloResponse;
import co.edu.usbcali.autosusbcali.mapper.ModeloMapper;
import co.edu.usbcali.autosusbcali.repository.MarcaRepository;
import co.edu.usbcali.autosusbcali.repository.ModeloRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class ModeloService implements IModeloService {
    private final ModeloRepository modeloRepository;

    // Modelo DEPENDE de Marca: necesitamos su repositorio para validar que la marca exista
    private final MarcaRepository marcaRepository;

    @Override
    public List<ObtenerModeloResponse> obtenerTodos() {
        log.debug("Consultando todos los modelos");
        List<Modelo> modelos = modeloRepository.findAll();
        log.debug("Se encontraron {} modelos", modelos.size());
        return ModeloMapper.listaModelosAListaObtenerModeloResponse(modelos);
    }

    @Override
    public List<ObtenerModeloResponse> buscarModelosPorMarca(Long marcaId) throws Exception {
        log.debug("Consultando modelos de la marca id={}", marcaId);

        // Primero validamos que la marca (la entidad "padre") exista
        validarMarcaExiste(marcaId);

        List<Modelo> modelos = modeloRepository.findByMarcaId(marcaId);
        log.debug("Se encontraron {} modelos para la marca id={}", modelos.size(), marcaId);
        return ModeloMapper.listaModelosAListaObtenerModeloResponse(modelos);
    }

    @Override
    public ObtenerModeloResponse obtenerModeloPorId(Long id) throws Exception {
        log.debug("Buscando modelo por id={}", id);
        Modelo modelo = modeloRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("No se encontro el modelo con id={}", id);
                    return new Exception("No se ha encontrado el modelo con id: " + id);
                });
        return ModeloMapper.modeloAObtenerModeloResponse(modelo);
    }

    @Override
    @Transactional
    public ObtenerModeloResponse crearModelo(CrearModeloRequest crearModeloRequest) throws Exception {
        // Log de ENTRADA
        log.info("Iniciando creacion de modelo: {}", crearModeloRequest);

        // Validaciones del objeto crearModeloRequest
        if (crearModeloRequest == null) {
            log.warn("Intento de crear modelo con request nulo");
            throw new Exception("El objeto crearModeloRequest no puede ser nulo.");
        }
        if (crearModeloRequest.marcaId() == null) {
            log.warn("Intento de crear modelo sin marcaId");
            throw new Exception("El id de la marca no puede ser nulo.");
        }
        if (crearModeloRequest.nombre() == null
                || crearModeloRequest.nombre().isBlank()) {
            log.warn("Intento de crear modelo con nombre vacio");
            throw new Exception("El nombre no puede ser nulo ni estar vacio.");
        }

        // DEPENDENCIA: la marca debe existir y estar activa
        validarMarcaExisteYActiva(crearModeloRequest.marcaId());

        // Validar que no exista otro modelo con el mismo nombre en la misma marca
        if (modeloRepository.findByMarcaIdAndNombre(
                crearModeloRequest.marcaId(), crearModeloRequest.nombre()).isPresent()) {
            log.warn("Intento de crear modelo duplicado: nombre={} en marca id={}",
                    crearModeloRequest.nombre(), crearModeloRequest.marcaId());
            throw new Exception("Ya existe un modelo con el nombre " + crearModeloRequest.nombre()
                    + " para esa marca.");
        }

        try {
            // Usar mapper para convertir el DTO a objeto de dominio
            Modelo modelo = ModeloMapper.crearModeloRequestAModelo(crearModeloRequest);

            // Persistir el Modelo en base de datos
            modelo = modeloRepository.save(modelo);

            // Log de SALIDA (todo salio bien)
            log.info("Modelo creado correctamente: id={}, nombre={}, marcaId={}",
                    modelo.getId(), modelo.getNombre(), modelo.getMarcaId());

            // Retornar el DTO de respuesta
            return ModeloMapper.modeloAObtenerModeloResponse(modelo);
        } catch (Exception e) {
            // La excepcion "e" va como ULTIMO parametro para imprimir el stack trace completo
            log.error("Error al guardar el modelo {} en la base de datos", crearModeloRequest.nombre(), e);
            throw e;
        }
    }

    @Override
    @Transactional
    public ObtenerModeloResponse modificarModelo(Long id, CrearModeloRequest modificarModeloRequest) throws Exception {
        log.info("Iniciando modificacion del modelo id={} con datos {}", id, modificarModeloRequest);

        // Validaciones de los parametros
        if (id == null) {
            log.warn("Intento de modificar modelo con id nulo");
            throw new Exception("El id del modelo no puede ser nulo.");
        }
        if (modificarModeloRequest == null) {
            log.warn("Intento de modificar modelo id={} con request nulo", id);
            throw new Exception("El objeto modificarModeloRequest no puede ser nulo.");
        }
        if (modificarModeloRequest.marcaId() == null) {
            log.warn("Intento de modificar modelo id={} sin marcaId", id);
            throw new Exception("El id de la marca no puede ser nulo.");
        }
        if (modificarModeloRequest.nombre() == null
                || modificarModeloRequest.nombre().isBlank()) {
            log.warn("Intento de modificar modelo id={} con nombre vacio", id);
            throw new Exception("El nombre no puede ser nulo ni estar vacio.");
        }

        // Buscar el modelo que se va a modificar
        Modelo modelo = modeloRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("No se puede modificar: no existe el modelo con id={}", id);
                    return new Exception("No se ha encontrado el modelo con id: " + id);
                });

        // DEPENDENCIA: la marca (nueva o la misma) debe existir y estar activa
        validarMarcaExisteYActiva(modificarModeloRequest.marcaId());

        // Validar que no exista OTRO modelo con el mismo nombre en esa marca
        if (modeloRepository.findByMarcaIdAndNombre(
                        modificarModeloRequest.marcaId(), modificarModeloRequest.nombre())
                .filter(otroModelo -> !otroModelo.getId().equals(id))
                .isPresent()) {
            log.warn("No se puede modificar el modelo id={}: ya existe otro modelo con nombre={} en marca id={}",
                    id, modificarModeloRequest.nombre(), modificarModeloRequest.marcaId());
            throw new Exception("Ya existe otro modelo con el nombre " + modificarModeloRequest.nombre()
                    + " para esa marca.");
        }

        // Guardamos los valores anteriores para el log de auditoria
        String nombreAnterior = modelo.getNombre();
        Long marcaIdAnterior = modelo.getMarcaId();

        // Actualizar los campos del modelo
        modelo.setNombre(modificarModeloRequest.nombre());
        modelo.setMarcaId(modificarModeloRequest.marcaId());

        try {
            // Persistir los cambios
            modelo = modeloRepository.save(modelo);
        } catch (Exception e) {
            log.error("Error al guardar la modificacion del modelo id={}", id, e);
            throw e;
        }

        log.info("Modelo id={} modificado: nombre '{}' -> '{}', marcaId {} -> {}",
                id, nombreAnterior, modelo.getNombre(), marcaIdAnterior, modelo.getMarcaId());

        // Retornar el DTO de respuesta
        return ModeloMapper.modeloAObtenerModeloResponse(modelo);
    }

    @Override
    @Transactional
    public void eliminarModelo(Long id) throws Exception {
        log.info("Iniciando eliminacion fisica del modelo id={}", id);

        if (id == null) {
            log.warn("Intento de eliminar modelo con id nulo");
            throw new Exception("El id del modelo no puede ser nulo.");
        }

        // 1. Verificar que el modelo exista antes de borrarlo
        Modelo modelo = modeloRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("No se puede eliminar: no existe el modelo con id={}", id);
                    return new Exception("No se ha encontrado el modelo con id: " + id);
                });

        try {
            // 2. Eliminacion FISICA: la fila desaparece de la tabla modelos (DELETE)
            modeloRepository.delete(modelo);

            // flush() obliga a ejecutar el DELETE ahora mismo, asi si falla lo atrapamos aqui
            modeloRepository.flush();

        } catch (DataIntegrityViolationException e) {
            // El modelo tiene vehiculos asociados (tabla vehiculos, columna modelo_id)
            log.warn("No se puede eliminar el modelo id={} porque tiene vehiculos asociados", id);
            throw new Exception("No se puede eliminar el modelo " + modelo.getNombre()
                    + " porque tiene vehiculos asociados.");
        } catch (Exception e) {
            log.error("Error al eliminar fisicamente el modelo id={}", id, e);
            throw e;
        }

        // 3. Log de AUDITORIA: como la fila ya no existe, este log es el unico rastro que queda
        log.info("Modelo eliminado FISICAMENTE: id={}, nombre={}, marcaId={}",
                modelo.getId(), modelo.getNombre(), modelo.getMarcaId());
    }

    // ---------- Metodos privados de apoyo (validan la dependencia con Marca) ----------

    // Verifica que la marca exista
    private Marca validarMarcaExiste(Long marcaId) throws Exception {
        return marcaRepository.findById(marcaId)
                .orElseThrow(() -> {
                    log.warn("No existe la marca con id={}", marcaId);
                    return new Exception("No se ha encontrado la marca con id: " + marcaId);
                });
    }

    // Verifica que la marca exista Y que este activa (no se crean modelos en marcas inactivas)
    private void validarMarcaExisteYActiva(Long marcaId) throws Exception {
        Marca marca = validarMarcaExiste(marcaId);
        if (Boolean.FALSE.equals(marca.getActivo())) {
            log.warn("La marca id={} ({}) esta inactiva", marcaId, marca.getNombre());
            throw new Exception("La marca " + marca.getNombre() + " esta inactiva.");
        }
    }
}