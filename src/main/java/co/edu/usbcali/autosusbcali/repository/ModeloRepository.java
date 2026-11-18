package co.edu.usbcali.autosusbcali.repository;

import co.edu.usbcali.autosusbcali.domain.Modelo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ModeloRepository extends JpaRepository<Modelo, Long> {
    // Busca todos los modelos que pertenecen a una marca
    List<Modelo> findByMarcaId(Long marcaId);

    // Busca un modelo por nombre dentro de una marca (para evitar duplicados en la misma marca)
    Optional<Modelo> findByMarcaIdAndNombre(Long marcaId, String nombre);
}