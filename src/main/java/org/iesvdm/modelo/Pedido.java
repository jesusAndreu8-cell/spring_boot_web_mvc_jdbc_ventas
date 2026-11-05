package org.iesvdm.modelo;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Pedido tal y como se guarda en la tabla pedido.
 * Las relaciones con cliente y comercial se representan mediante sus identificadores.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Pedido {
	private int id;
	private Double total;
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	private LocalDate fecha;
	private Integer clienteId;
	private Integer comercialId;
}
