package org.iesvdm.servicios;

import java.util.List;
import java.util.Optional;

import org.iesvdm.dao.PedidoDAO;
import org.iesvdm.modelo.Pedido;
import org.iesvdm.modelo.PedidoDetalle;
import org.iesvdm.modelo.PersonaOpcion;
import org.springframework.stereotype.Service;

@Service
public class PedidoService {

	private final PedidoDAO pedidoDAO;
	private final ClienteService clienteService;
	private final ComercialService comercialService;

	public PedidoService(PedidoDAO pedidoDAO, ClienteService clienteService, ComercialService comercialService) {
		this.pedidoDAO = pedidoDAO;
		this.clienteService = clienteService;
		this.comercialService = comercialService;
	}

	public List<PedidoDetalle> listAll() {
		return pedidoDAO.getAll();
	}

	public Optional<Pedido> find(int id) {
		return pedidoDAO.find(id);
	}

	public Optional<PedidoDetalle> findDetalle(int id) {
		return pedidoDAO.findDetalle(id);
	}

	public List<PersonaOpcion> opcionesClientes() {
		return pedidoDAO.getOpcionesClientes();
	}

	public List<PersonaOpcion> opcionesComerciales() {
		return pedidoDAO.getOpcionesComerciales();
	}

	/**
	 * @throws IllegalArgumentException si los datos no son válidos o el cliente/comercial no existen.
	 */
	public void create(Pedido pedido) {
		validar(pedido);
		pedidoDAO.create(pedido);
	}

	/**
	 * @throws IllegalArgumentException si los datos no son válidos o el cliente/comercial no existen.
	 */
	public void update(Pedido pedido) {
		validar(pedido);
		pedidoDAO.update(pedido);
	}

	public void delete(int id) {
		pedidoDAO.delete(id);
	}

	private void validar(Pedido pedido) {
		if (pedido.getTotal() == null || pedido.getTotal().isNaN() || pedido.getTotal().isInfinite()
				|| pedido.getTotal() < 0) {
			throw new IllegalArgumentException("El total debe ser un número mayor o igual que 0.");
		}
		if (pedido.getClienteId() == null || clienteService.find(pedido.getClienteId()).isEmpty()) {
			throw new IllegalArgumentException("El cliente seleccionado no existe.");
		}
		if (pedido.getComercialId() == null || comercialService.find(pedido.getComercialId()).isEmpty()) {
			throw new IllegalArgumentException("El comercial seleccionado no existe.");
		}
	}
}
