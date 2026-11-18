package co.edu.usbcali.autosusbcali.service;

import co.edu.usbcali.autosusbcali.dto.request.CrearMarcaRequest;
import co.edu.usbcali.autosusbcali.dto.response.ObtenerMarcaResponse;

import java.util.List;

public interface IMarcaService {
    // Busca todas las marcas sin importar su estado
    List<ObtenerMarcaResponse> obtenerTodas();

    // Busca las marcas por estado (activas o inactivas)
    List<ObtenerMarcaResponse> buscarMarcaPorEstado(Boolean activo);

    ObtenerMarcaResponse obtenerMarcaPorNombre(String nombre) throws Exception;

    ObtenerMarcaResponse obtenerMarcaPorId(Long id) throws Exception;

    // Metodo para crear una marca
    ObtenerMarcaResponse crearMarca(CrearMarcaRequest crearMarcaRequest) throws Exception;

    // Metodo para modificar una marca existente
    ObtenerMarcaResponse modificarMarca(Long id, CrearMarcaRequest modificarMarcaRequest) throws Exception;

    // Metodo para eliminar una marca (eliminacion fisica)
    void eliminarMarca(Long id) throws Exception;
}