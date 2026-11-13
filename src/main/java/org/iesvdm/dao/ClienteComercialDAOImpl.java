package org.iesvdm.dao;

import java.time.LocalDate;
import java.util.List;

import org.iesvdm.dto.ComercialVinculadoDTO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class ClienteComercialDAOImpl implements ClienteComercialDAO {

	private final JdbcTemplate jdbcTemplate;

	ClienteComercialDAOImpl(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public List<ComercialVinculadoDTO> findComercialesByClienteId(int clienteId) {
		return jdbcTemplate.query("""
				SELECT co.id, CONCAT_WS(' ', co.nombre, co.apellido1, co.apellido2) AS comercial,
				       cc.fecha_asociacion, cc.prioridad
				FROM cliente_has_comercial cc
				JOIN comercial co ON co.id = cc.comercial_id
				WHERE cc.cliente_id = ?
				ORDER BY cc.prioridad, cc.fecha_asociacion, co.id
				""",
				(rs, rowNum) -> new ComercialVinculadoDTO(
						rs.getInt("id"),
						rs.getString("comercial"),
						rs.getObject("fecha_asociacion", LocalDate.class),
						rs.getInt("prioridad")),
				clienteId);
	}

	@Override
	public void create(int clienteId, int comercialId, LocalDate fechaAsociacion, int prioridad) {
		int filas = jdbcTemplate.update(
				"INSERT INTO cliente_has_comercial (cliente_id, comercial_id, fecha_asociacion, prioridad) VALUES (?, ?, ?, ?)",
				clienteId, comercialId, fechaAsociacion, prioridad);
		log.info("Vinculado comercial {} al cliente {} ({} fila).", comercialId, clienteId, filas);
	}

	@Override
	public void delete(int clienteId, int comercialId) {
		int filas = jdbcTemplate.update(
				"DELETE FROM cliente_has_comercial WHERE cliente_id = ? AND comercial_id = ?",
				clienteId, comercialId);
		log.info("Desvinculado comercial {} del cliente {} ({} fila).", comercialId, clienteId, filas);
	}

	@Override
	public void deleteByClienteId(int clienteId) {
		jdbcTemplate.update("DELETE FROM cliente_has_comercial WHERE cliente_id = ?", clienteId);
	}

	@Override
	public void deleteByComercialId(int comercialId) {
		jdbcTemplate.update("DELETE FROM cliente_has_comercial WHERE comercial_id = ?", comercialId);
	}
}
