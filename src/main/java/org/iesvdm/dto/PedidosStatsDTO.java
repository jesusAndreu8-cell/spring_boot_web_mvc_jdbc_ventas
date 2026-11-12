package org.iesvdm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO con la "inteligencia de pedidos" de un comercial: estadísticas que se
 * calculan en el servicio y se transfieren a la vista.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PedidosStatsDTO {
	private int numPedidos;
	private double total;
	private double media;
	private double maximo;
	private double minimo;
}
