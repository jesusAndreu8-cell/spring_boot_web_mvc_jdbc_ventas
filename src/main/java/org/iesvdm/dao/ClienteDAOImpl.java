package org.iesvdm.dao;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Types;
import java.util.List;
import java.util.Optional;

import org.iesvdm.modelo.Cliente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class ClienteDAOImpl implements ClienteDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void create(Cliente cliente) {
        String sql = "INSERT INTO cliente (nombre, apellido1, apellido2, ciudad, categoría) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        int rows = jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            int idx = 1;
            ps.setString(idx++, cliente.getNombre());
            ps.setString(idx++, cliente.getApellido1());
            if (cliente.getApellido2() == null) {
                ps.setNull(idx++, Types.VARCHAR);
            } else {
                ps.setString(idx++, cliente.getApellido2());
            }
            if (cliente.getCiudad() == null) {
                ps.setNull(idx++, Types.VARCHAR);
            } else {
                ps.setString(idx++, cliente.getCiudad());
            }
            if (cliente.getCategoria() == null) {
                ps.setNull(idx, Types.INTEGER);
            } else {
                ps.setInt(idx, cliente.getCategoria());
            }
            return ps;
        }, keyHolder);

        cliente.setId(keyHolder.getKey().longValue());
        log.info("Insertados {} registros. Id generado: {}", rows, cliente.getId());
    }

    @Override
    public List<Cliente> getAll() {
        List<Cliente> lista = jdbcTemplate.query(
                "SELECT * FROM cliente",
                (rs, rowNum) -> new Cliente(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("apellido1"),
                        rs.getString("apellido2"),
                        rs.getString("ciudad"),
                        rs.getObject("categoría", Integer.class)));

        log.info("Devueltos {} registros.", lista.size());
        return lista;
    }

    @Override
    public Optional<Cliente> find(int id) {
        try {
            Cliente cliente = jdbcTemplate.queryForObject(
                    "SELECT * FROM cliente WHERE id = ?",
                    (rs, rowNum) -> new Cliente(
                            rs.getInt("id"),
                            rs.getString("nombre"),
                            rs.getString("apellido1"),
                            rs.getString("apellido2"),
                            rs.getString("ciudad"),
                            rs.getObject("categoría", Integer.class)),
                    id);
            return Optional.ofNullable(cliente);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public void update(Cliente cliente) {
        int rows = jdbcTemplate.update(
                "UPDATE cliente SET nombre = ?, apellido1 = ?, apellido2 = ?, ciudad = ?, categoría = ? WHERE id = ?",
                cliente.getNombre(),
                cliente.getApellido1(),
                cliente.getApellido2(),
                cliente.getCiudad(),
                cliente.getCategoria(),
                cliente.getId());

        log.info("Update de Cliente con {} registros actualizados.", rows);
    }

    @Override
    public void delete(long id) {
        int rows = jdbcTemplate.update("DELETE FROM cliente WHERE id = ?", id);
        log.info("Delete de Cliente con {} registros eliminados.", rows);
    }
}
