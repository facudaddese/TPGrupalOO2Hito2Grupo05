package com.unla.grupo5.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Detalle del canon que paga una unidad de venta (no se persiste)
@Getter
@Setter
@NoArgsConstructor
public class CanonDTO {
    private String nombreFestival;
    private String nombreComercial;
    private String codigo;
    private String tipoUnidad;

    // (superficie * costo por m²) -> lo pagan todas las unidades
    private Integer superficie;
    private double costoSuperficie;
    private double montoSuperficie;

    // Solo Food Truck: se suma el plus si requiere conexión eléctrica
    private boolean esFoodTruck;
    private boolean requiereConexion;
    private double plusElectricidad;

    // Solo Puesto Desarmable: se descuenta (tiempo de montaje * costo de montaje)
    private boolean esPuestoDesarmable;
    private double tiempoMontaje;
    private double costoMontaje;
    private double descuentoMontaje;

    private double canon;
}
