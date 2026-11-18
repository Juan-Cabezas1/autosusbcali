package co.edu.usbcali.autosusbcali.service;

import co.edu.usbcali.autosusbcali.domain.Rol;
import co.edu.usbcali.autosusbcali.dto.request.CrearRolRequest;
import co.edu.usbcali.autosusbcali.dto.response.ObtenerRolResponse;
import co.edu.usbcali.autosusbcali.mapper.RolMapper;
import co.edu.usbcali.autosusbcali.repository.RolRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class RolService implements IRolService {
    private final RolRepository rolRepository;

    @Override
    public List<ObtenerRolResponse> obtenerTodos() {
        log.debug("Consultando todos los roles");
        List<Rol> roles = rolRepository.findAll();
        log.debug("Se encontraron {} roles", roles.size());
        return RolMapper.listaRolesAListaObtenerRolResponse(roles);
    }

    @Override
    public List<ObtenerRolResponse> buscarRolPorEstado(Boolean activo) {
        log.debug("Consultando roles con activo={}", activo);
        List<Rol> rolesPorEstado = rolRepository.findByActivo(activo);
        log.debug("Se encontraron {} roles con activo={}", rolesPorEstado.size(), activo);
        return RolMapper.listaRolesAListaObtenerRolResponse(rolesPorEstado);
    }

    @Override
    public ObtenerRolResponse obtenerRolPorNombre(String nombre) {
        log.debug("Buscando rol por nombre={}", nombre);
        Rol rolPorNombre = rolRepository.findByNombre(nombre)
                .orElseThrow(() -> {
                    // WARN: el sistema funciono bien, solo que el rol no existe
                    log.warn("No se encontro el rol con nombre={}", nombre);
                    return new RuntimeException("No se ha encontrado el rol por nombre: " + nombre);
                });
        return RolMapper.rolAObtenerRolResponse(rolPorNombre);
    }

    @Override
    public ObtenerRolResponse obtenerRolPorId(Long id) {
        log.debug("Buscando rol por id={}", id);
        Rol rolPorId = rolRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("No se encontro el rol con id={}", id);
                    return new RuntimeException("No se ha encontrado el rol por id: " + id);
                });
        return RolMapper.rolAObtenerRolResponse(rolPorId);
    }

    @Override
    @Transactional
    public ObtenerRolResponse crearRol(CrearRolRequest crearRolRequest) throws Exception {
        // Log de ENTRADA
        log.info("Iniciando creacion de rol: {}", crearRolRequest);

        // Validaciones del objeto crearRolRequest
        if (crearRolRequest == null) {
            log.warn("Intento de crear rol con request nulo");
            throw new Exception("El objeto crearRolRequest no puede ser nulo.");
        }
        if (crearRolRequest.nombre() == null
                || crearRolRequest.nombre().isBlank()) {
            log.warn("Intento de crear rol con nombre vacio");
            throw new Exception("El nombre no puede ser nulo ni estar vacío.");
        }
        if (crearRolRequest.descripcion() == null
                || crearRolRequest.descripcion().isBlank()) {
            log.warn("Intento de crear rol {} con descripcion vacia", crearRolRequest.nombre());
            throw new Exception("La descripcion no puede ser nula ni estar vacia.");
        }

        try {
            // Usar mapper para convertir el DTO a objeto de dominio
            Rol rol = RolMapper.crearRolRequestARol(crearRolRequest);

            // Persistir el Rol en base de datos
            rol = rolRepository.save(rol);

            // Log de SALIDA (todo salio bien)
            log.info("Rol creado correctamente: id={}, nombre={}", rol.getId(), rol.getNombre());

            // Retornar el DTO de respuesta
            return RolMapper.rolAObtenerRolResponse(rol);
        } catch (Exception e) {
            // La excepcion "e" va como ULTIMO parametro para imprimir el stack trace completo
            log.error("Error al guardar el rol {} en la base de datos", crearRolRequest.nombre(), e);
            throw e;
        }
    }

    @Override
    @Transactional
    public ObtenerRolResponse modificarRol(Long id, CrearRolRequest modificarRolRequest) throws Exception {
        log.info("Iniciando modificacion del rol id={} con datos {}", id, modificarRolRequest);

        // Validaciones de los parametros
        if (id == null) {
            log.warn("Intento de modificar rol con id nulo");
            throw new Exception("El id del rol no puede ser nulo.");
        }
        if (modificarRolRequest == null) {
            log.warn("Intento de modificar rol id={} con request nulo", id);
            throw new Exception("El objeto modificarRolRequest no puede ser nulo.");
        }
        if (modificarRolRequest.nombre() == null
                || modificarRolRequest.nombre().isBlank()) {
            log.warn("Intento de modificar rol id={} con nombre vacio", id);
            throw new Exception("El nombre no puede ser nulo ni estar vacío.");
        }
        if (modificarRolRequest.descripcion() == null
                || modificarRolRequest.descripcion().isBlank()) {
            log.warn("Intento de modificar rol id={} con descripcion vacia", id);
            throw new Exception("La descripcion no puede ser nula ni estar vacia.");
        }

        // Buscar el rol que se va a modificar
        Rol rol = rolRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("No se puede modificar: no existe el rol con id={}", id);
                    return new Exception("No se ha encontrado el rol con id: " + id);
                });

        // Validar que el nuevo nombre no pertenezca a otro rol (el nombre es unico)
        if (rolRepository.findByNombre(modificarRolRequest.nombre())
                .filter(otroRol -> !otroRol.getId().equals(id))
                .isPresent()) {
            log.warn("No se puede modificar el rol id={}: ya existe otro rol con nombre={}",
                    id, modificarRolRequest.nombre());
            throw new Exception("Ya existe otro rol con el nombre: " + modificarRolRequest.nombre());
        }

        // Guardamos los valores anteriores para el log de auditoria
        String nombreAnterior = rol.getNombre();
        String descripcionAnterior = rol.getDescripcion();

        // Actualizar los campos del rol
        rol.setNombre(modificarRolRequest.nombre());
        rol.setDescripcion(modificarRolRequest.descripcion());

        try {
            // Persistir los cambios
            rol = rolRepository.save(rol);
        } catch (Exception e) {
            log.error("Error al guardar la modificacion del rol id={}", id, e);
            throw e;
        }

        log.info("Rol id={} modificado: nombre '{}' -> '{}', descripcion '{}' -> '{}'",
                id, nombreAnterior, rol.getNombre(), descripcionAnterior, rol.getDescripcion());

        // Retornar el DTO de respuesta
        return RolMapper.rolAObtenerRolResponse(rol);
    }

    @Override
    @Transactional
    public void eliminarRol(Long id) throws Exception {
        log.info("Iniciando eliminacion fisica del rol id={}", id);

        if (id == null) {
            log.warn("Intento de eliminar rol con id nulo");
            throw new Exception("El id del rol no puede ser nulo.");
        }

        // 1. Verificar que el rol exista antes de borrarlo
        Rol rol = rolRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("No se puede eliminar: no existe el rol con id={}", id);
                    return new Exception("No se ha encontrado el rol con id: " + id);
                });

        try {
            // 2. Eliminacion FISICA: la fila desaparece de la tabla roles (DELETE)
            rolRepository.delete(rol);

            // flush() obliga a ejecutar el DELETE ahora mismo, asi si falla lo atrapamos aqui
            rolRepository.flush();

        } catch (DataIntegrityViolationException e) {
            // El rol esta asignado a usuarios (usuarios_roles) o tiene permisos (roles_permisos)
            log.warn("No se puede eliminar el rol id={} porque esta en uso por usuarios o permisos", id);
            throw new Exception("No se puede eliminar el rol " + rol.getNombre()
                    + " porque tiene usuarios o permisos asociados.");
        } catch (Exception e) {
            log.error("Error al eliminar fisicamente el rol id={}", id, e);
            throw e;
        }

        // 3. Log de AUDITORIA: como la fila ya no existe, este log es el unico rastro que queda
        log.info("Rol eliminado FISICAMENTE: id={}, nombre={}, descripcion={}",
                rol.getId(), rol.getNombre(), rol.getDescripcion());
    }
}