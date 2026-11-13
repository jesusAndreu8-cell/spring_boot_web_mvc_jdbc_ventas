package org.iesvdm.dao;

import java.time.LocalDate;
import java.util.List;

import org.iesvdm.dto.ComercialVinculadoDTO;

public interface ClienteComercialDAO {
	List<ComercialVinculadoDTO> findComercialesByClienteId(int clienteId);
	void create(int clienteId, int comercialId, LocalDate fechaAsociacion, int prioridad);
	void delete(int clienteId, int comercialId);
	void deleteByClienteId(int clienteId);
	void deleteByComercialId(int comercialId);
}
