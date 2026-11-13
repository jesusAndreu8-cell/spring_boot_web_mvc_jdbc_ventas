package org.iesvdm.servicios;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.iesvdm.dao.ClienteDAO;
import org.iesvdm.dao.ClienteComercialDAO;
import org.iesvdm.dao.PedidoDAO;
import org.iesvdm.dto.ComercialVinculadoDTO;
import org.iesvdm.modelo.Cliente;
import org.iesvdm.modelo.PedidoDetalle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service 
public class ClienteService {

    @Autowired
    private ClienteDAO clienteDAO;

    @Autowired
    private PedidoDAO pedidoDAO;

    @Autowired
    private ClienteComercialDAO clienteComercialDAO;

    public List<ComercialVinculadoDTO> listComercialesVinculados(int clienteId) {
        return clienteComercialDAO.findComercialesByClienteId(clienteId);
    }

    public void addComercial(int clienteId, int comercialId, LocalDate fecha, int prioridad) {
        clienteComercialDAO.create(clienteId, comercialId, fecha, prioridad);
    }

    public void removeComercial(int clienteId, int comercialId) {
        clienteComercialDAO.delete(clienteId, comercialId);
    }

    public List<PedidoDetalle> listPedidosByCliente(int clienteId) {
        return pedidoDAO.findByClienteId(clienteId);
    }

    public List<Cliente> listAll() {
        return clienteDAO.getAll();
    }

    public Optional<Cliente> find(int id) {
        return clienteDAO.find(id);
    }

    public void create(Cliente cliente) {
        clienteDAO.create(cliente);
    }

    public void update(Cliente cliente) {
        clienteDAO.update(cliente);
    }

    // Transaccional: si el cliente tiene pedidos el borrado falla y se deshace también el de sus vínculos
    @Transactional
    public void delete(int id) {
        clienteComercialDAO.deleteByClienteId(id);
        clienteDAO.delete(id);
    }
}
