package com.example.gw_gerenciador_cartoes.application.restController;

import com.example.gw_gerenciador_cartoes.application.util.ClienteMapper;
import com.example.gw_gerenciador_cartoes.domain.model.Cliente;
import com.example.gw_gerenciador_cartoes.application.dto.ClienteDTO;
import com.example.gw_gerenciador_cartoes.service.ClienteServiceImpl;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/clientes")
public class ClienteController {

    ClienteServiceImpl clienteService;


    public ClienteController(ClienteServiceImpl clienteService) {
        this.clienteService = clienteService;
    }


    @GetMapping
    public ResponseEntity<Page<Cliente>> listarTodosClientes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "asc") String direction,
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String cpf) {

        return ResponseEntity.ok(
                clienteService.listarClientes(page, size, sort, direction, nome, cpf)
        );
    }


    @PostMapping
    public ResponseEntity<Cliente> salvarCliente(@Valid @RequestBody ClienteDTO clienteDTO) {
        Cliente cliente = ClienteMapper.FromDTOtoEntity(clienteDTO);
        Cliente clienteSalvo = clienteService.criarCliente(cliente);
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteSalvo);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarCliente(@PathVariable Long id){
        clienteService.deletarCliente(id);
        return ResponseEntity.ok().build();
    }

}
