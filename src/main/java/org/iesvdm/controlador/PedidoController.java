package org.iesvdm.controlador;

import org.iesvdm.modelo.Pedido;
import org.iesvdm.servicios.PedidoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class PedidoController {

	private final PedidoService pedidoService;

	public PedidoController(PedidoService pedidoService) {
		this.pedidoService = pedidoService;
	}

	@GetMapping("/pedidos")
	public String listar(Model model) {
		model.addAttribute("pedidos", pedidoService.listAll());
		return "pedidos";
	}

	@GetMapping("/pedidos/{id}")
	public String detalle(Model model, @PathVariable int id) {
		return pedidoService.findDetalle(id)
				.map(pedido -> {
					model.addAttribute("pedido", pedido);
					return "detalle-pedido";
				})
				.orElse("redirect:/pedidos");
	}

	@GetMapping("/pedidos/crear")
	public String crear(Model model) {
		return formulario(model, new Pedido(), "crear-pedido");
	}

	@PostMapping("/pedidos/crear")
	public String submitCrear(Model model, @ModelAttribute("pedido") Pedido pedido, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return formularioConError(model, pedido, "crear-pedido", "Algún dato del formulario no es válido (revisa la fecha y el total).");
		}
		try {
			pedidoService.create(pedido);
		} catch (IllegalArgumentException e) {
			return formularioConError(model, pedido, "crear-pedido", e.getMessage());
		}
		return "redirect:/pedidos";
	}

	@GetMapping("/pedidos/editar/{id}")
	public String editar(Model model, @PathVariable int id) {
		return pedidoService.find(id)
				.map(pedido -> formulario(model, pedido, "editar-pedido"))
				.orElse("redirect:/pedidos");
	}

	@PostMapping("/pedidos/editar/{id}")
	public String submitEditar(Model model, @PathVariable int id, @ModelAttribute("pedido") Pedido pedido, BindingResult bindingResult) {
		if (pedidoService.find(id).isEmpty()) {
			return "redirect:/pedidos";
		}
		pedido.setId(id);
		if (bindingResult.hasErrors()) {
			return formularioConError(model, pedido, "editar-pedido", "Algún dato del formulario no es válido (revisa la fecha y el total).");
		}
		try {
			pedidoService.update(pedido);
		} catch (IllegalArgumentException e) {
			return formularioConError(model, pedido, "editar-pedido", e.getMessage());
		}
		return "redirect:/pedidos";
	}

	@PostMapping("/pedidos/borrar/{id}")
	public RedirectView submitBorrar(@PathVariable int id) {
		pedidoService.delete(id);
		return new RedirectView("/pedidos");
	}

	// Carga en el modelo el pedido y las listas de clientes y comerciales para los <select>
	private String formulario(Model model, Pedido pedido, String vista) {
		model.addAttribute("pedido", pedido);
		model.addAttribute("clientes", pedidoService.opcionesClientes());
		model.addAttribute("comerciales", pedidoService.opcionesComerciales());
		return vista;
	}

	private String formularioConError(Model model, Pedido pedido, String vista, String error) {
		model.addAttribute("error", error);
		return formulario(model, pedido, vista);
	}
}
