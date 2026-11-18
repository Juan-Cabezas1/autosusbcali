package co.edu.usbcali.autosusbcali.service;

import co.edu.usbcali.autosusbcali.dto.request.CrearModeloRequest;
import co.edu.usbcali.autosusbcali.dto.response.ObtenerModeloResponse;

import java.util.List;

public interface IModeloService {
    // Busca todos los modelos
    List<ObtenerModeloResponse> obtenerTodos();

    // Busca los modelos que pertenecen a una marca
    List<ObtenerModeloResponse> buscarModelosPorMarca(Long marcaId) throws Exception;

    ObtenerModeloResponse obtenerModeloPorId(Long id) throws Exception;

    // Metodo para crear un modelo (debe pertenecer a una marca existente)
    ObtenerModeloResponse crearModelo(CrearModeloRequest crearModeloRequest) throws Exception;

    // Metodo para modificar un modelo existente
    ObtenerModeloResponse modificarModelo(Long id, CrearModeloRequest modificarModeloRequest) throws Exception;

    // Metodo para eliminar un modelo (eliminacion fisica)
    void eliminarModelo(Long id) throws Exception;
}