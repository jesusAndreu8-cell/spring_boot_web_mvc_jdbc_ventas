package org.iesvdm.dao;

import java.util.List;
import java.util.Optional;

import org.iesvdm.modelo.Pedido;
import org.iesvdm.modelo.PedidoDetalle;
import org.iesvdm.modelo.PersonaOpcion;
import org.springframework.stereotype.Repository;

@Repository
public interface PedidoDAO {
	public void create(Pedido pedido);
	public List<PedidoDetalle> getAll();
	public Optional<Pedido> find(int id);
	public Optional<PedidoDetalle> findDetalle(int id);
	public void update(Pedido pedido);
	public void delete(int id);

	// Pedidos de un comercial / de un cliente (con nombres) para las vistas de detalle
	public List<PedidoDetalle> findByComercialId(int comercialId);
	public List<PedidoDetalle> findByClienteId(int clienteId);

	// Apoyo a los formularios: clientes y comerciales con nombre completo
	public List<PersonaOpcion> getOpcionesClientes();
	public List<PersonaOpcion> getOpcionesComerciales();
}
