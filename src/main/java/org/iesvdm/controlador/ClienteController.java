package org.iesvdm.controlador;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.iesvdm.dto.ComercialVinculadoDTO;
import org.iesvdm.modelo.Cliente;
import org.iesvdm.servicios.ClienteService;
import org.iesvdm.servicios.ComercialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;


@Controller 
public class ClienteController {

    @Autowired 
    private ClienteService clienteService;

    @Autowired
    private ComercialService comercialService;

    @GetMapping("/clientes")
    public String listar(Model model) {
        List<Cliente> clientes = clienteService.listAll();
        model.addAttribute("clientes", clientes);
        return "clientes";
    }

    @GetMapping("/clientes/{id}")
    public String detalle(Model model, @PathVariable int id) {
        return clienteService.find(id)
                .map(cliente -> {
                    model.addAttribute("cliente", cliente);
                    model.addAttribute("pedidos", clienteService.listPedidosByCliente(id));
                    return "detalle-cliente";
                })
                .orElse("redirect:/clientes");
    }

    @GetMapping("/clientes/crear")
    public String crear(Model model) {
        model.addAttribute("cliente", new Cliente());
        return "crear-cliente";
    }

    @PostMapping("/clientes/crear")
    public RedirectView submitCrear(@ModelAttribute("cliente") Cliente cliente) {
        clienteService.create(cliente);
        return new RedirectView("/clientes");
    }

    @GetMapping("/clientes/editar/{id}")
    public String editar(Model model, @PathVariable int id) {
        return clienteService.find(id)
                .map(cliente -> {
                    model.addAttribute("cliente", cliente);
                    List<ComercialVinculadoDTO> vinculados = clienteService.listComercialesVinculados(id);
                    Set<Integer> idsVinculados = vinculados.stream()
                            .map(ComercialVinculadoDTO::getComercialId)
                            .collect(Collectors.toSet());
                    model.addAttribute("vinculados", vinculados);
                    // en el select solo los comerciales que aún no están vinculados al cliente
                    model.addAttribute("comerciales", comercialService.listAll().stream()
                            .filter(c -> !idsVinculados.contains(c.getId()))
                            .toList());
                    model.addAttribute("hoy", LocalDate.now());
                    return "editar-cliente";
                })
                .orElse("redirect:/clientes");
    }

    @PostMapping("/clientes/editar/{id}")
    public RedirectView submitEditar(@PathVariable int id, @ModelAttribute("cliente") Cliente cliente) {
        cliente.setId(id);
        clienteService.update(cliente);
        return new RedirectView("/clientes");
    }

    @PostMapping("/clientes/editar/{id}/comerciales")
    public RedirectView submitAddComercial(@PathVariable int id,
            @RequestParam int comercialId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam int prioridad,
            RedirectAttributes redirectAttributes) {
        try {
            clienteService.addComercial(id, comercialId, fecha, prioridad);
        } catch (DuplicateKeyException e) {
            redirectAttributes.addFlashAttribute("error", "Ese comercial ya está vinculado al cliente.");
        }
        return new RedirectView("/clientes/editar/" + id);
    }

    @PostMapping("/clientes/editar/{id}/comerciales/borrar/{comercialId}")
    public RedirectView submitBorrarComercial(@PathVariable int id, @PathVariable int comercialId) {
        clienteService.removeComercial(id, comercialId);
        return new RedirectView("/clientes/editar/" + id);
    }

    @PostMapping("/clientes/borrar/{id}")
    public RedirectView submitBorrar(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            clienteService.delete(id);
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("error",
                    "No se puede eliminar el cliente porque tiene pedidos asociados. Elimina o reasigna antes sus pedidos.");
        }
        return new RedirectView("/clientes");
    }
}
