package co.edu.usbcali.autosusbcali.mapper;

import co.edu.usbcali.autosusbcali.domain.Marca;
import co.edu.usbcali.autosusbcali.dto.request.CrearMarcaRequest;
import co.edu.usbcali.autosusbcali.dto.response.ObtenerMarcaResponse;

import java.util.List;

public class MarcaMapper {

    // Convierte una Marca (entidad de dominio) a un DTO de respuesta
    public static ObtenerMarcaResponse marcaAObtenerMarcaResponse(Marca marca) {
        return new ObtenerMarcaResponse(
                marca.getId(),
                marca.getNombre(),
                marca.getActivo());
    }

    // Convierte una lista de Marcas a una lista de DTOs de respuesta
    public static List<ObtenerMarcaResponse> listaMarcasAListaObtenerMarcaResponse(List<Marca> marcas) {
        return marcas.stream().map(MarcaMapper::marcaAObtenerMarcaResponse).toList();
    }

    // Convierte un Request de crear Marca a una Marca (entidad de dominio)
    public static Marca crearMarcaRequestAMarca(CrearMarcaRequest crearMarcaRequest) {
        return new Marca(null,
                crearMarcaRequest.nombre(),
                true);
    }
}