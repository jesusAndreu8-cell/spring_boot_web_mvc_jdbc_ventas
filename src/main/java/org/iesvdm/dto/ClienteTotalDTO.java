package org.iesvdm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO con un cliente y la suma del total de todos sus pedidos.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClienteTotalDTO {
	private int clienteId;
	private String clienteNombre;
	private double total;
}
