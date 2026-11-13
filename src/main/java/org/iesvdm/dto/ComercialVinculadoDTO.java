package org.iesvdm.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Comercial vinculado a un cliente (tabla N:M cliente_has_comercial),
 * con la fecha de asociación y la prioridad del vínculo.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ComercialVinculadoDTO {
	private int comercialId;
	private String comercialNombre;
	private LocalDate fechaAsociacion;
	private int prioridad;
}
