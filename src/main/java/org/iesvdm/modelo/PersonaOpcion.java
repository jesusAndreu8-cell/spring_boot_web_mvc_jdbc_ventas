package org.iesvdm.modelo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Opción para un &lt;select&gt;: el valor es el id y el texto es el nombre completo.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonaOpcion {
	private int id;
	private String nombre;
}
