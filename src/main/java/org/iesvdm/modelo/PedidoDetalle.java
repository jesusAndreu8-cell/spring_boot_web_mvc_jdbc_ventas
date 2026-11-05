package org.iesvdm.modelo;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Objeto de lectura: pedido junto con los nombres completos de su cliente y su comercial
 * (resultado de un JOIN). Se usa en el listado y en el detalle.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PedidoDetalle {
	private int id;
	private Double total;
	private LocalDate fecha;
	private int clienteId;
	private String clienteNombre;
	private int comercialId;
	private String comercialNombre;
}
