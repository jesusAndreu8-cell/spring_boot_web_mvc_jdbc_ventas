package org.iesvdm.controlador;

import java.util.List;

import org.iesvdm.modelo.Comercial;
import org.iesvdm.servicios.ComercialService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ComercialController {

	private final ComercialService comercialService;

	public ComercialController(ComercialService comercialService) {
		this.comercialService = comercialService;
	}

	@GetMapping("/comerciales")
	public String listar(Model model) {
		List<Comercial> comerciales = comercialService.listAll();
		model.addAttribute("comerciales", comerciales);
		return "comerciales";
	}
}