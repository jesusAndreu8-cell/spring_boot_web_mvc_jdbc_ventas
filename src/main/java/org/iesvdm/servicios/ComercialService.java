package org.iesvdm.servicios;

import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Optional;

import org.iesvdm.dao.ComercialDAO;
import org.iesvdm.dao.PedidoDAO;
import org.iesvdm.dto.ClienteTotalDTO;
import org.iesvdm.dto.PedidosStatsDTO;
import org.iesvdm.modelo.Comercial;
import org.iesvdm.modelo.PedidoDetalle;
import org.springframework.stereotype.Service;

@Service
public class ComercialService {

	private final ComercialDAO comercialDAO;
	private final PedidoDAO pedidoDAO;

	public ComercialService(ComercialDAO comercialDAO, PedidoDAO pedidoDAO) {
		this.comercialDAO = comercialDAO;
		this.pedidoDAO = pedidoDAO;
	}

	public List<PedidoDetalle> listPedidosByComercial(int comercialId) {
		return pedidoDAO.findByComercialId(comercialId);
	}

	/** Estadísticas (nº, total, media, máximo y mínimo) a partir de la lista de pedidos del comercial. */
	public PedidosStatsDTO statsPedidos(List<PedidoDetalle> pedidos) {
		DoubleSummaryStatistics s = pedidos.stream()
				.filter(p -> p.getTotal() != null)
				.mapToDouble(PedidoDetalle::getTotal)
				.summaryStatistics();
		if (s.getCount() == 0) {
			return new PedidosStatsDTO(0, 0, 0, 0, 0);
		}
		return new PedidosStatsDTO((int) s.getCount(), s.getSum(), s.getAverage(), s.getMax(), s.getMin());
	}

	public List<ClienteTotalDTO> listClientesPorTotal(int comercialId) {
		return pedidoDAO.findClientesPorTotalByComercialId(comercialId);
	}

	public void create(Comercial comercial) {
		comercialDAO.create(comercial);
	}

	public List<Comercial> listAll() {
		return comercialDAO.getAll();
	}

	public Optional<Comercial> find(int id) {
		return comercialDAO.find(id);
	}

	public void update(Comercial comercial) {
		comercialDAO.update(comercial);
	}

	public void delete(int id) {
		comercialDAO.delete(id);
	}
}