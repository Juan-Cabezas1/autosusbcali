package co.edu.usbcali.autosusbcali.mapper;

import co.edu.usbcali.autosusbcali.domain.Rol;
import co.edu.usbcali.autosusbcali.dto.request.CrearRolRequest;
import co.edu.usbcali.autosusbcali.dto.response.ObtenerRolResponse;

import java.util.List;

public class RolMapper {
    public static ObtenerRolResponse rolAObtenerRolResponse(Rol rol) {
        ObtenerRolResponse obtenerRolResponse =
                new ObtenerRolResponse(rol.getId(), rol.getNombre());
        return obtenerRolResponse;
    }

    public static List<ObtenerRolResponse> listaRolesAListaObtenerRolResponse(List<Rol> roles) {
        return roles.stream().map(RolMapper::rolAObtenerRolResponse).toList();
    }

    //Metodo que convierte un Request de crear Rol a un Rol (Entidad de dominio)
    public static Rol crearRolRequestARol(CrearRolRequest crearRolRequest) {
        //Forma 1 usando el constructor
        return new Rol(null,
                crearRolRequest.nombre(),
                crearRolRequest.descripcion(),
                true);

        // Forma 2 usando el constructor vacio
        /*Rol rol = new Rol();
        rol.setNombre(crearRolRequest.nombre());
        rol.setDescripcion(crearRolRequest.descripcion());
        rol.setActivo(true);
        return rol;
         */
    }
}
