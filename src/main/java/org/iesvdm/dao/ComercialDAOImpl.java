package org.iesvdm.dao;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Types;
import java.util.List;
import java.util.Optional;

import org.iesvdm.modelo.Comercial;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class ComercialDAOImpl implements ComercialDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void create(Comercial comercial) {
        String sql = "INSERT INTO comercial (nombre, apellido1, apellido2, comisión) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        int rows = jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            int idx = 1;
            ps.setString(idx++, comercial.getNombre());
            ps.setString(idx++, comercial.getApellido1());
            if (comercial.getApellido2() == null) {
                ps.setNull(idx++, Types.VARCHAR);
            } else {
                ps.setString(idx++, comercial.getApellido2());
            }
            ps.setFloat(idx, comercial.getComision());
            return ps;
        }, keyHolder);

        comercial.setId(keyHolder.getKey().longValue());
        log.info("Insertados {} registros. Id generado: {}", rows, comercial.getId());
    }

    @Override
    public List<Comercial> getAll() {
        List<Comercial> lista = jdbcTemplate.query(
                "SELECT * FROM comercial",
                (rs, rowNum) -> new Comercial(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("apellido1"),
                        rs.getString("apellido2"),
                        rs.getFloat("comisión")));

        log.info("Devueltos {} registros.", lista.size());
        return lista;
    }

    @Override
    public Optional<Comercial> find(int id) {
        try {
            Comercial comercial = jdbcTemplate.queryForObject(
                    "SELECT * FROM comercial WHERE id = ?",
                    (rs, rowNum) -> new Comercial(
                            rs.getInt("id"),
                            rs.getString("nombre"),
                            rs.getString("apellido1"),
                            rs.getString("apellido2"),
                            rs.getFloat("comisión")),
                    id);
            return Optional.ofNullable(comercial);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public void update(Comercial comercial) {
        int rows = jdbcTemplate.update(
                "UPDATE comercial SET nombre = ?, apellido1 = ?, apellido2 = ?, comisión = ? WHERE id = ?",
                comercial.getNombre(),
                comercial.getApellido1(),
                comercial.getApellido2(),
                comercial.getComision(),
                comercial.getId());

        log.info("Update de Comercial con {} registros actualizados.", rows);
    }

    @Override
    public void delete(long id) {
        int rows = jdbcTemplate.update("DELETE FROM comercial WHERE id = ?", id);
        log.info("Delete de Comercial con {} registros eliminados.", rows);
    }
}
