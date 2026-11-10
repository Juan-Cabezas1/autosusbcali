package co.edu.usbcali.autosusbcali.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "cotizaciones")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data

public class Cotizacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo", length = 30, nullable = false, unique = true)
    private String codigo;

    @Column(name = "vehiculo_id", nullable = false)
    private Long vehiculoId;

    @Column(name = "comprador_id", nullable = false)
    private Long compradorId;

    @Column(name = "asesor_id")
    private Long asesorId;

    @Column(name = "precio_base", precision = 15, scale = 2, nullable = false)
    private BigDecimal precioBase;

    @Column(name = "descuento", precision = 15, scale = 2, nullable = false)
    private BigDecimal descuento;

    @Column(name = "impuestos", precision = 15, scale = 2, nullable = false)
    private BigDecimal impuestos;

    @Column(name = "precio_total", precision = 15, scale = 2, nullable = false)
    private BigDecimal precioTotal;

    @Column(name = "vigencia_hasta", nullable = false)
    private LocalDate vigenciaHasta;

    @Column(name = "estado", length = 20, nullable = false)
    private String estado;

    @Column(name = "fecha_creacion", nullable = false)
    private OffsetDateTime fechaCreacion;
}
