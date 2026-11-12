package org.iesvdm.dao;

import java.sql.PreparedStatement;
import java.sql.Types;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.iesvdm.dto.ClienteTotalDTO;
import org.iesvdm.modelo.Pedido;
import org.iesvdm.modelo.PedidoDetalle;
import org.iesvdm.modelo.PersonaOpcion;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class PedidoDAOImpl implements PedidoDAO {

	private final JdbcTemplate jdbcTemplate;

	// Consulta con JOIN: CONCAT_WS ignora los NULL, así que si apellido2 es nulo no queda espacio ni "null"
	private static final String SELECT_DETALLE = """
			SELECT p.id, p.total, p.fecha,
			       p.id_cliente,   CONCAT_WS(' ', c.nombre,  c.apellido1,  c.apellido2)  AS cliente,
			       p.id_comercial, CONCAT_WS(' ', co.nombre, co.apellido1, co.apellido2) AS comercial
			FROM pedido p
			JOIN cliente c    ON p.id_cliente   = c.id
			JOIN comercial co ON p.id_comercial = co.id
			""";

	private static final RowMapper<PedidoDetalle> DETALLE_MAPPER = (rs, rowNum) -> new PedidoDetalle(
			rs.getInt("id"),
			rs.getObject("total", Double.class),
			rs.getObject("fecha", LocalDate.class),
			rs.getInt("id_cliente"),
			rs.getString("cliente"),
			rs.getInt("id_comercial"),
			rs.getString("comercial"));

	private static final RowMapper<Pedido> PEDIDO_MAPPER = (rs, rowNum) -> new Pedido(
			rs.getInt("id"),
			rs.getObject("total", Double.class),
			rs.getObject("fecha", LocalDate.class),
			rs.getInt("id_cliente"),
			rs.getInt("id_comercial"));

	PedidoDAOImpl(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	/**
	 * Inserta en base de datos el nuevo Pedido, actualizando el id en el bean Pedido.
	 */
	@Override
	public synchronized void create(Pedido pedido) {

		String sqlInsert = """
				INSERT INTO pedido (total, fecha, id_cliente, id_comercial)
				VALUES (?, ?, ?, ?)
				""";

		KeyHolder keyHolder = new GeneratedKeyHolder();
		int rows = jdbcTemplate.update(connection -> {
			PreparedStatement ps = connection.prepareStatement(sqlInsert, new String[] { "id" });
			int idx = 1;
			ps.setObject(idx++, pedido.getTotal(), Types.DOUBLE);
			ps.setObject(idx++, pedido.getFecha(), Types.DATE);
			ps.setObject(idx++, pedido.getClienteId(), Types.INTEGER);
			ps.setObject(idx, pedido.getComercialId(), Types.INTEGER);
			return ps;
		}, keyHolder);

		if (keyHolder.getKey() != null) {
			pedido.setId(keyHolder.getKey().intValue());
		}

		log.info("Insertados {} registros.", rows);
	}

	/**
	 * Devuelve lista con todos los Pedidos junto a los nombres de cliente y comercial.
	 */
	@Override
	public List<PedidoDetalle> getAll() {
		List<PedidoDetalle> lista = jdbcTemplate.query(SELECT_DETALLE + " ORDER BY p.id", DETALLE_MAPPER);
		log.info("Devueltos {} registros.", lista.size());
		return lista;
	}

	/**
	 * Devuelve Optional de Pedido (con ids) para edición.
	 */
	@Override
	public Optional<Pedido> find(int id) {
		List<Pedido> pedidos = jdbcTemplate.query("SELECT * FROM pedido WHERE id = ?", PEDIDO_MAPPER, id);
		if (pedidos.isEmpty()) {
			log.info("Pedido con id {} no encontrado.", id);
		}
		return pedidos.stream().findFirst();
	}

	/**
	 * Devuelve Optional de PedidoDetalle (con nombres completos) para mostrar.
	 */
	@Override
	public Optional<PedidoDetalle> findDetalle(int id) {
		List<PedidoDetalle> pedidos = jdbcTemplate.query(SELECT_DETALLE + " WHERE p.id = ?", DETALLE_MAPPER, id);
		if (pedidos.isEmpty()) {
			log.info("Pedido con id {} no encontrado.", id);
		}
		return pedidos.stream().findFirst();
	}

	/**
	 * Devuelve los pedidos de un comercial (con nombres de cliente y comercial).
	 */
	@Override
	public List<PedidoDetalle> findByComercialId(int comercialId) {
		return jdbcTemplate.query(SELECT_DETALLE + " WHERE p.id_comercial = ? ORDER BY p.id", DETALLE_MAPPER, comercialId);
	}

	/**
	 * Devuelve los pedidos de un cliente (con nombres de cliente y comercial).
	 */
	@Override
	public List<PedidoDetalle> findByClienteId(int clienteId) {
		return jdbcTemplate.query(SELECT_DETALLE + " WHERE p.id_cliente = ? ORDER BY p.id", DETALLE_MAPPER, clienteId);
	}

	/**
	 * Clientes que tienen algún pedido con el comercial, ordenados de mayor a menor
	 * por la suma del total de TODOS sus pedidos (con cualquier comercial).
	 */
	@Override
	public List<ClienteTotalDTO> findClientesPorTotalByComercialId(int comercialId) {
		return jdbcTemplate.query("""
				SELECT c.id, CONCAT_WS(' ', c.nombre, c.apellido1, c.apellido2) AS cliente, SUM(p.total) AS total
				FROM cliente c
				JOIN pedido p ON p.id_cliente = c.id
				WHERE c.id IN (SELECT id_cliente FROM pedido WHERE id_comercial = ?)
				GROUP BY c.id, c.nombre, c.apellido1, c.apellido2
				ORDER BY total DESC, c.id
				""",
				(rs, rowNum) -> new ClienteTotalDTO(rs.getInt("id"), rs.getString("cliente"), rs.getDouble("total")),
				comercialId);
	}

	/**
	 * Actualiza Pedido con campos del bean según ID del mismo.
	 */
	@Override
	public void update(Pedido pedido) {

		int rows = jdbcTemplate.update("""
				UPDATE pedido SET
					total = ?,
					fecha = ?,
					id_cliente = ?,
					id_comercial = ?
				WHERE id = ?
				""", pedido.getTotal(),
				pedido.getFecha(),
				pedido.getClienteId(),
				pedido.getComercialId(),
				pedido.getId());

		log.info("Update de Pedido con {} registros actualizados.", rows);
	}

	/**
	 * Borra Pedido con ID proporcionado.
	 */
	@Override
	public void delete(int id) {
		int rows = jdbcTemplate.update("DELETE FROM pedido WHERE id = ?", id);
		log.info("Delete de Pedido con {} registros eliminados.", rows);
	}

	@Override
	public List<PersonaOpcion> getOpcionesClientes() {
		return jdbcTemplate.query("""
				SELECT id, CONCAT_WS(' ', nombre, apellido1, apellido2) AS nombre
				FROM cliente ORDER BY apellido1, apellido2, nombre
				""", (rs, rowNum) -> new PersonaOpcion(rs.getInt("id"), rs.getString("nombre")));
	}

	@Override
	public List<PersonaOpcion> getOpcionesComerciales() {
		return jdbcTemplate.query("""
				SELECT id, CONCAT_WS(' ', nombre, apellido1, apellido2) AS nombre
				FROM comercial ORDER BY apellido1, apellido2, nombre
				""", (rs, rowNum) -> new PersonaOpcion(rs.getInt("id"), rs.getString("nombre")));
	}
}
