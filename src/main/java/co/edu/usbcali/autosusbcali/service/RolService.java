package co.edu.usbcali.autosusbcali.service;

import co.edu.usbcali.autosusbcali.domain.Rol;
import co.edu.usbcali.autosusbcali.dto.request.CrearRolRequest;
import co.edu.usbcali.autosusbcali.dto.response.ObtenerRolResponse;
import co.edu.usbcali.autosusbcali.mapper.RolMapper;
import co.edu.usbcali.autosusbcali.repository.RolRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class RolService implements IRolService{
    private final RolRepository rolRepository;

    @Override
    public List<ObtenerRolResponse> obtenerTodos() {
        log.info("Iniciando obtenerTodos()");
        log.warn("Hola soy juan camilo");
        return RolMapper.listaRolesAListaObtenerRolResponse(rolRepository.findAll());
    }

    @Override
    public List<ObtenerRolResponse> buscarRolPorEstado(Boolean activo) {
        List<Rol> rolesPorEstado = rolRepository.findByActivo(activo);
        return RolMapper.listaRolesAListaObtenerRolResponse(rolesPorEstado);
    }

    @Override
    public ObtenerRolResponse obtenerRolPorNombre(String nombre) {
        Rol rolPorNombre = rolRepository.findByNombre(nombre)
                .orElseThrow(()-> new RuntimeException("No se ha encontrado el rol por nombre: "));
        return RolMapper.rolAObtenerRolResponse(rolPorNombre);
    }

    @Override
    public ObtenerRolResponse obtenerRolPorId(Long id) {
        Rol rolPorId = rolRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("No se ha encontrado el rol por id: "));
        return RolMapper.rolAObtenerRolResponse(rolPorId);
    }

    @Override
    public ObtenerRolResponse crearRol(CrearRolRequest crearRolRequest) throws Exception {
        // Validaciones del objeto #crearRolRequest
        if (crearRolRequest == null) {
            throw new Exception("El obejto crearRolRquest no puede ser nulo.");
        }
        if (crearRolRequest.nombre() == null
                || crearRolRequest.nombre().isBlank()) {
            throw new Exception("El nombre no puede ser nulo ni estar vacío.");
        }
        if (crearRolRequest.descripcion() == null
                || crearRolRequest.descripcion().isBlank()) {
            throw new Exception("La descripcion no puede ser nulo ni estar vacia.");
        }

        // Usar mapper para convertir el DTO a objeto de Domain
        Rol rol = RolMapper.crearRolRequestARol(crearRolRequest);

        // Persistir el Rol en base de datos
        rol = rolRepository.save(rol);

        //Crear DTO response obtenerRolResponse
        ObtenerRolResponse rolResponse = RolMapper.rolAObtenerRolResponse(rol);

        // Retomar rolResponse
        return rolResponse;
    }

    @Override
    @Transactional
    public ObtenerRolResponse modificarRol(Long id, CrearRolRequest modificarRolRequest) throws Exception {
        // Validaciones de los parametros
        if (id == null) {
            throw new Exception("El id del rol no puede ser nulo.");
        }
        if (modificarRolRequest == null) {
            throw new Exception("El objeto modificarRolRequest no puede ser nulo.");
        }
        if (modificarRolRequest.nombre() == null
                || modificarRolRequest.nombre().isBlank()) {
            throw new Exception("El nombre no puede ser nulo ni estar vacío.");
        }
        if (modificarRolRequest.descripcion() == null
                || modificarRolRequest.descripcion().isBlank()) {
            throw new Exception("La descripcion no puede ser nula ni estar vacia.");
        }

        // Buscar el rol que se va a modificar
        Rol rol = rolRepository.findById(id)
                .orElseThrow(() -> new Exception("No se ha encontrado el rol con id: " + id));

        // Validar que el nuevo nombre no pertenezca a otro rol (el nombre es unico)
        if (rolRepository.findByNombre(modificarRolRequest.nombre())
                .filter(otroRol -> !otroRol.getId().equals(id))
                .isPresent()) {
            throw new Exception("Ya existe otro rol con el nombre: " + modificarRolRequest.nombre());
        }

        // Actualizar los campos del rol
        rol.setNombre(modificarRolRequest.nombre());
        rol.setDescripcion(modificarRolRequest.descripcion());

        // Persistir los cambios
        rol = rolRepository.save(rol);

        // Retornar el DTO de respuesta
        return RolMapper.rolAObtenerRolResponse(rol);
    }

    @Override
    @Transactional
    public void eliminarRol(Long id) throws Exception {
        if (id == null) {
            throw new Exception("El id del rol no puede ser nulo.");
        }

        // Buscar el rol que se va a eliminar
        Rol rol = rolRepository.findById(id)
                .orElseThrow(() -> new Exception("No se ha encontrado el rol con id: " + id));

        if (Boolean.FALSE.equals(rol.getActivo())) {
            throw new Exception("El rol con id " + id + " ya se encuentra inactivo.");
        }

        // Eliminacion logica: se marca el rol como inactivo
        rol.setActivo(false);
        rolRepository.save(rol);

        // Si se requiere eliminacion fisica, usar en su lugar:
        // rolRepository.delete(rol);
    }
}