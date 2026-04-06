# Sistema de Produção e Comercialização de Veículos

Sistema distribuído em Java que simula uma cadeia completa de produção e comercialização de veículos, composta por uma fábrica, lojas e clientes. Desenvolvido para a disciplina de Programação Concorrente.

**Autores:** Luiz Athar, Pedro Rossi e Samuel Mauli

---

## Arquitetura

O sistema é composto por **3 processos independentes** que se comunicam via **Sockets TCP**:

```
┌─────────────┐       Socket        ┌─────────────┐       Socket        ┌─────────────┐
│   FÁBRICA   │◄────────────────────│    LOJAS     │◄────────────────────│  CLIENTES    │
│  (Servidor) │────────────────────►│  (3 inst.)   │────────────────────►│ (20 threads) │
└─────────────┘                     └─────────────┘                     └─────────────┘
```

### Fábrica
- Estoque de peças limitado a **500 unidades** com reabastecimento contínuo
- Esteira de abastecimento com capacidade para **5 solicitações simultâneas**
- **4 estações de produção**, cada uma com:
  - **5 funcionários** em disposição circular
  - **5 ferramentas** compartilhadas (Problema do Jantar dos Filósofos)
  - **Esteira circular** com 40 posições
- Veículos com ID sequencial, cores alternando (Vermelho/Verde/Azul) e tipos alternando (SUV/Sedan)

### Lojas
- **3 lojas** executando como processos separados
- Cada loja possui esteira circular própria com 10 posições
- Solicitam veículos da fábrica continuamente via socket
- Atendem clientes concorrentemente

### Clientes
- **20 threads** comprando veículos aleatoriamente
- Cada cliente compra de 2 a 5 veículos em lojas aleatórias
- Veículos comprados são armazenados em uma garagem individual

## Sincronização

Toda a sincronização utiliza **exclusivamente semáforos** (`java.util.concurrent.Semaphore`):

| Problema | Solução |
|---|---|
| Jantar dos Filósofos (ferramentas) | Ordenação de recursos — funcionário sempre pega a ferramenta de menor ID primeiro, evitando deadlock |
| Produtor-Consumidor (esteiras) | Semáforos `vazio`, `cheio` e `mutex` no buffer circular |
| Estoque de peças | Semáforo contador (500 permits) |
| Esteira de abastecimento | Semáforo contador (5 permits) |
| Acesso aos logs | Semáforo binário (mutex) |
| Starvation | Todos os semáforos com `fairness = true` |

## Estrutura do Projeto

```
src/
├── comum/                        # Classes compartilhadas
│   ├── Veiculo.java              # Entidade serializada
│   ├── EsteiraCircular.java      # Buffer circular com semáforos
│   └── Configuracao.java         # Constantes do sistema
├── fabrica/                      # Processo da fábrica
│   ├── FabricaMain.java          # Ponto de entrada
│   ├── EstoquePecas.java         # Estoque com semáforo (500 un.)
│   ├── EsteiraAbastecimento.java # Controle de 5 solicitações
│   ├── FornecedorPecas.java      # Thread reabastecedora
│   ├── GeradorVeiculo.java       # Gerador de IDs e atributos
│   ├── Ferramenta.java           # Semáforo binário (filósofo)
│   ├── Funcionario.java          # Thread trabalhador
│   ├── EstacaoProducao.java      # Estação com 5 func. + esteira
│   ├── ServidorFabrica.java      # Socket server para lojas
│   └── AtendenteLoja.java        # Thread por conexão de loja
├── loja/                         # Processo da loja
│   ├── LojaMain.java             # Ponto de entrada
│   ├── Loja.java                 # Lógica da loja
│   ├── SolicitadorVeiculos.java  # Solicita veículos da fábrica
│   ├── ServidorLoja.java         # Socket server para clientes
│   └── AtendenteCliente.java     # Thread por conexão de cliente
└── cliente/                      # Processo dos clientes
    ├── ClienteMain.java          # Ponto de entrada (20 threads)
    ├── Cliente.java              # Thread cliente
    └── Garagem.java              # Armazena veículos comprados
```

## Como Executar

### Compilar
```bash
bash compilar.sh
```

### Executar (tudo em um terminal)
```bash
bash executar.sh
```

### Executar em terminais separados (recomendado para visualização)
```bash
# Terminal 1 - Fábrica
java -cp bin fabrica.FabricaMain

# Terminal 2 - Loja 1
java -cp bin loja.LojaMain 1

# Terminal 3 - Loja 2
java -cp bin loja.LojaMain 2

# Terminal 4 - Loja 3
java -cp bin loja.LojaMain 3

# Terminal 5 - Clientes
java -cp bin cliente.ClienteMain
```

> **Importante:** Iniciar na ordem acima (fábrica primeiro, depois lojas, por último clientes).

## Logs Gerados

| Arquivo | Conteúdo |
|---|---|
| `log_producao.txt` | ID, cor, tipo, estação, funcionário, posição na esteira |
| `log_venda_loja.txt` | Dados de produção + ID da loja + posição na esteira da loja |
| `log_loja_X_recebimento.txt` | Registro de veículos recebidos pela loja X |
| `log_loja_X_venda.txt` | Registro de vendas ao cliente com toda a cadeia produtiva |
