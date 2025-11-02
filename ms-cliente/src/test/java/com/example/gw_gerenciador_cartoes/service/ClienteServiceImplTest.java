package com.example.gw_gerenciador_cartoes.service;

import com.example.gw_gerenciador_cartoes.domain.model.Cliente;
import com.example.gw_gerenciador_cartoes.domain.model.Conta;
import com.example.gw_gerenciador_cartoes.domain.model.Endereco;
import com.example.gw_gerenciador_cartoes.domain.ports.CriarCartaoProducerPort;
import com.example.gw_gerenciador_cartoes.domain.ports.EmailNormalQueueProducerPort;
import com.example.gw_gerenciador_cartoes.infra.adapter.ClienteRepositoryAdapter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ClienteServiceImplTest {

    private static final String NOME_PADRAO = "Fulano";
    private static final String CPF_PADRAO = "12345678901";
    private static final String EMAIL_PADRAO = "fulano@email.com";
    private static final String DATA_NASCIMENTO_PADRAO = "2000-01-01";

    @Autowired
    private ClienteRepositoryAdapter clienteRepository;

    @Autowired
    private ClienteServiceImpl clienteService;

    @MockBean
    private CriarCartaoProducerPort criarCartaoProducer;

    @MockBean
    private EmailNormalQueueProducerPort emailNormalQueueProducer;

    private Endereco criarEnderecoPadrao() {
        return new Endereco("São Paulo", "Centro", "Rua das Flores", "01000-000", "Apto 101", "123");
    }

    private Conta criarContaPadrao() {
        return new Conta(null, "0001", "Corrente", new BigDecimal("1000.00"));
    }

    private Cliente criarClientePadrao(String nome, String cpf, String email) {
        return new Cliente(null, nome, email, DATA_NASCIMENTO_PADRAO, cpf, criarEnderecoPadrao(), criarContaPadrao());
    }

    @Test
    void deveCriarClienteContaeEnderecoComSucesso() {
        Cliente cliente = criarClientePadrao(NOME_PADRAO, CPF_PADRAO, EMAIL_PADRAO);
        Cliente clienteSalvo = clienteService.criarCliente(cliente);

        Cliente clienteEncontrado = clienteRepository.findById(clienteSalvo.getId()).orElseThrow();

        // Assert Cliente
        assertEquals(NOME_PADRAO, clienteEncontrado.getNome());
        assertEquals(CPF_PADRAO, clienteEncontrado.getCpf());
        assertEquals(EMAIL_PADRAO, clienteEncontrado.getEmail());
        assertEquals(DATA_NASCIMENTO_PADRAO, clienteEncontrado.getDataNasc());

        // Assert Endereço
        Endereco endereco = clienteEncontrado.getEndereco();
        assertNotNull(endereco);
        assertEquals("São Paulo", endereco.getCidade());
        assertEquals("Centro", endereco.getBairro());
        assertEquals("Rua das Flores", endereco.getRua());
        assertEquals("01000-000", endereco.getCep());
        assertEquals("Apto 101", endereco.getComplemento());
        assertEquals("123", endereco.getNumero());

        // Assert Conta
        Conta conta = clienteEncontrado.getConta();
        assertNotNull(conta);
        assertEquals("0001", conta.getAgencia());
        assertEquals("Corrente", conta.getTipo());
        assertEquals(new BigDecimal("1000.00"), conta.getSaldo());
    }

    @Test
    void deveDeletarClienteQuandoIdForValido() {
        Cliente cliente = criarClientePadrao(NOME_PADRAO, CPF_PADRAO, EMAIL_PADRAO);
        Cliente clienteSalvo = clienteService.criarCliente(cliente);

        clienteService.deletarCliente(clienteSalvo.getId());
        Optional<Cliente> clienteEncontrado = clienteRepository.findById(clienteSalvo.getId());

        assertTrue(clienteEncontrado.isEmpty(), "O cliente deveria estar ausente");
    }

    @Test
    void deveRetornarClientesFiltradosPorNomeComPaginacao() {
        Cliente cliente1 = criarClientePadrao("Fulano", "11111111111", "fulano@gmail.com");
        Cliente cliente2 = criarClientePadrao("Beltrano", "22222222222", "beltrano@gmail.com");

        clienteService.criarCliente(cliente1);
        clienteService.criarCliente(cliente2);

        Page<Cliente> paginaClientes = clienteService.listarClientes(0, 6, "id", "asc", "Fulano", null);

        assertNotNull(paginaClientes);
        assertEquals(1, paginaClientes.getTotalElements());
        assertEquals("Fulano", paginaClientes.getContent().get(0).getNome());
    }

    @Test
    void deveRetornarClientesFiltradosPorCPFComPaginacao() {
        Cliente cliente1 = criarClientePadrao("Fulano", "11111111111", "fulano@gmail.com");
        Cliente cliente2 = criarClientePadrao("Beltrano", "22222222222", "beltrano@gmail.com");

        clienteService.criarCliente(cliente1);
        clienteService.criarCliente(cliente2);

        Page<Cliente> paginaClientes = clienteService.listarClientes(0, 6, "id", "asc", null, "11111111111");

        assertNotNull(paginaClientes);
        assertEquals(1, paginaClientes.getTotalElements());
        assertEquals("11111111111", paginaClientes.getContent().get(0).getCpf());
    }

    @Test
    void deveRetornarClientesFiltradosPorCPFeNomeComPaginacao() {
        Cliente cliente1 = criarClientePadrao("Fulano", "11111111111", "fulano@gmail.com");
        Cliente cliente2 = criarClientePadrao("Beltrano", "22222222222", "beltrano@gmail.com");

        clienteService.criarCliente(cliente1);
        clienteService.criarCliente(cliente2);

        Page<Cliente> paginaClientes = clienteService.listarClientes(0, 6, "id", "asc", "Fulano", "11111111111");

        assertNotNull(paginaClientes);
        assertEquals(1, paginaClientes.getTotalElements());
        assertEquals("11111111111", paginaClientes.getContent().get(0).getCpf());
        assertEquals("Fulano", paginaClientes.getContent().get(0).getNome());

    }

    @Test
    void deveRetornarClientesSemFiltrarComPaginacao() {
        Cliente cliente1 = criarClientePadrao("Fulano", "11111111111", "fulano@gmail.com");
        Cliente cliente2 = criarClientePadrao("Beltrano", "22222222222", "beltrano@gmail.com");

        clienteService.criarCliente(cliente1);
        clienteService.criarCliente(cliente2);

        Page<Cliente> paginaClientes = clienteService.listarClientes(0, 6, "id", "asc", null, null);

        assertNotNull(paginaClientes);
        assertEquals(2, paginaClientes.getTotalElements());
        assertEquals("11111111111", paginaClientes.getContent().get(0).getCpf());
        assertEquals("Fulano", paginaClientes.getContent().get(0).getNome());
        assertEquals("22222222222", paginaClientes.getContent().get(1).getCpf());
        assertEquals("Beltrano", paginaClientes.getContent().get(1).getNome());

    }

}
