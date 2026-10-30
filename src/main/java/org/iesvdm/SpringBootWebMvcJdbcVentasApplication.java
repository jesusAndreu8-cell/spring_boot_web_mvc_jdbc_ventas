package org.iesvdm;

import java.util.Optional;

import org.iesvdm.dao.ClienteDAO;
import org.iesvdm.dao.ComercialDAO;
import org.iesvdm.modelo.Cliente;
import org.iesvdm.modelo.Comercial;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
public class SpringBootWebMvcJdbcVentasApplication implements CommandLineRunner {

    // Inyección automática de los DAO
    @Autowired
    private ClienteDAO clienteDAO;

    @Autowired
    private ComercialDAO comercialDAO;

    public static void main(String[] args) {
        SpringApplication.run(SpringBootWebMvcJdbcVentasApplication.class, args);
    }

    // run SOLO se ejecuta en el arranque de la aplicación cargada desde esta clase
    @Override
    public void run(String... args) throws Exception {

        // ---------------------------------------------------------------
        // PRUEBAS DE CLIENTEDAO
        // ---------------------------------------------------------------
        log.info("*******************************");
        log.info("*Prueba de arranque ClienteDAO*");
        log.info("*******************************");

        clienteDAO.getAll().forEach(c -> log.info("Cliente: {}", c));

        int id = 1;
        Optional<Cliente> cliente = clienteDAO.find(id);

        if (cliente.isPresent()) {
            log.info("Cliente {}: {}", id, cliente.get());

            String nombreOld = cliente.get().getNombre();
            cliente.get().setNombre("Jose M");
            clienteDAO.update(cliente.get());
            log.info("Cliente {} actualizado: {}", id, clienteDAO.find(id).get());

            // Se restaura el nombre original para no alterar los datos
            cliente.get().setNombre(nombreOld);
            clienteDAO.update(cliente.get());
        }

        // ---------------------------------------------------------------
        // PRUEBAS DE COMERCIALDAO
        // ---------------------------------------------------------------
        log.info("*********************************");
        log.info("*Prueba de arranque ComercialDAO*");
        log.info("*********************************");

        // getAll
        comercialDAO.getAll().forEach(c -> log.info("Comercial: {}", c));

        // create
        Comercial nuevo = new Comercial();
        nuevo.setNombre("Lucía");
        nuevo.setApellido1("Pérez");
        nuevo.setApellido2(null);
        nuevo.setComision(0.10f);
        comercialDAO.create(nuevo);
        log.info("Comercial creado: {}", nuevo);

        // find
        Optional<Comercial> comercial = comercialDAO.find((int) nuevo.getId());
        if (comercial.isPresent()) {
            log.info("Comercial {}: {}", nuevo.getId(), comercial.get());

            // update
            String nombreOld = comercial.get().getNombre();
            comercial.get().setNombre("Lucía M");
            comercial.get().setComision(0.12f);
            comercialDAO.update(comercial.get());
            log.info("Comercial actualizado: {} -> {}",
                    nombreOld, comercialDAO.find((int) nuevo.getId()).get());
        }

        // delete (solo el recién creado; los originales tienen pedidos y fallaría por la FK)
        comercialDAO.delete(nuevo.getId());
        log.info("Comercial {} borrado. ¿Existe? {}",
                nuevo.getId(), comercialDAO.find((int) nuevo.getId()).isPresent());
    }
}
