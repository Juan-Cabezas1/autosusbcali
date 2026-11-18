package co.edu.usbcali.autosusbcali.mapper;

import co.edu.usbcali.autosusbcali.domain.Modelo;
import co.edu.usbcali.autosusbcali.dto.request.CrearModeloRequest;
import co.edu.usbcali.autosusbcali.dto.response.ObtenerModeloResponse;

import java.util.List;

public class ModeloMapper {

    // Convierte un Modelo (entidad de dominio) a un DTO de respuesta
    public static ObtenerModeloResponse modeloAObtenerModeloResponse(Modelo modelo) {
        return new ObtenerModeloResponse(
                modelo.getId(),
                modelo.getNombre(),
                modelo.getMarcaId());
    }

    // Convierte una lista de Modelos a una lista de DTOs de respuesta
    public static List<ObtenerModeloResponse> listaModelosAListaObtenerModeloResponse(List<Modelo> modelos) {
        return modelos.stream().map(ModeloMapper::modeloAObtenerModeloResponse).toList();
    }

    // Convierte un Request de crear Modelo a un Modelo (entidad de dominio)
    public static Modelo crearModeloRequestAModelo(CrearModeloRequest crearModeloRequest) {
        // Orden del constructor de Modelo: id, marcaId, nombre
        return new Modelo(null,
                crearModeloRequest.marcaId(),
                crearModeloRequest.nombre());
    }
}