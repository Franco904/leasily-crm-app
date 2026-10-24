# Leasily CRM — Domínio

Aplicativo mobile Android para automação de processos de CRM especializado na intermediação de locações de imóveis residenciais por temporada. Aprimora a rotina operacional do corretor de imóveis e fornece ferramentas analíticas para orientar a tomada de decisão. Funciona primariamente offline, sincronizando dados com o back-end quando há conexão disponível.

Este glossário define os termos canônicos do domínio e os contextos delimitados que estruturam o sistema.

Atributos dos cadastros de Imóvel, Cliente, Proprietário, Atendimento e Período Ocupado entram no glossário apenas quando:
- (a) o seu significado deriva da prática de intermediação de locação por temporada (e não do senso comum);
- (b) o nome no código diverge do domínio ou induz a erro de interpretação; ou
- (c) alteram o comportamento de algum desses cadastros ou dos KPIs.

Atributos autoexplicativos ficam de fora, e as definições não enumeram as opções do atributo, exceto quando as opções constituem o próprio conceito.

## Contextos Delimitados

- **Autenticação** — Login do Corretor via conta Google, manutenção da sessão e perfil profissional.
- **Imóveis** — Catálogo de imóveis administrados: características físicas, localização e configuração financeira por imóvel.
- **Clientes** — Gestão de Leads e Inquilinos; ciclo de vida do Cliente e visão centralizada (SCV).
- **Proprietários** — Cadastro dos Proprietários que contratam o Corretor para administrar seus Imóveis.
- **Atendimento** — Registro do episódio de procura de um Lead: preferências de procura, imóveis enviados e interações.
- **Períodos Ocupados** — Ciclo de vida de um intervalo de indisponibilidade de um Imóvel: Estadias e Períodos indisponíveis; inclui controle de overbooking e financeiro da Estadia.
- **Relatórios Analíticos** — KPIs e gráficos de desempenho profissional do Corretor.
- **Sincronização** — Propagação oportunística dos dados registrados no aparelho para o servidor.

## Linguagem

### Autenticação

**Corretor**: Ator autenticado do aplicativo — o corretor de imóveis que intermedeia as locações. Identificado por conta Google. Apenas um dispositivo pode ter sessão ativa por vez.
_Evitar_: Usuário
_Alias de código_: `User`

**Creci**: Número de registro profissional do corretor no Conselho Regional de Corretores de Imóveis (CRECI). Informado pelo Corretor separadamente após o login, como parte da configuração de perfil profissional.

> A autenticação ocorre em dois momentos: (1) login via Google → início da sessão; (2) configuração de perfil → Creci e demais dados profissionais do Corretor.

---

### Imóveis

**Imóvel**: Propriedade imobiliária administrada pelo Corretor, disponível para aluguel por temporada. Possui características físicas (dormitórios, banheiros, camas, eletrodomésticos, estacionamento), dados de condomínio, localização (bairro, proximidade ao mar) e configuração financeira.
_Alias de código_: `Property`

**Captação**: Processo de obtenção e cadastro de um novo Imóvel para administração.

**Diária**: Valor cobrado por dia de estadia no Imóvel, em reais.

**Taxa de limpeza**: Valor fixo cobrado por estadia para limpeza do Imóvel, em reais.

**Comissão**: Percentual ou valor fixo retido pelo Corretor por cada estadia como remuneração pela intermediação.
_Alias de código_: `Commission`

**Sinal** (Imóveis): Valor de reserva estabelecido pelo Proprietário para confirmação de uma estadia naquele Imóvel. Pode ser absoluto ou percentual.

**Calendário de ocupação**: Visualização dos Períodos Ocupados registrados para um Imóvel ao longo do tempo; ferramenta para evitar overbooking.

**Overbooking**: Sobreposição de Estadias ou Períodos indisponíveis registrados para o mesmo Imóvel em períodos conflitantes. Situação a ser prevenida.

---

### Clientes

**Cliente**: Contato na carteira do Corretor. Concentra dados pessoais (nome, telefone, documento, cidade). Lead, Inquilino e Inquilino antigo são estados derivados dos seus Atendimentos e Estadias — não são tipos distintos de cadastro.
_Alias de código_: `LeadTenant`

**Lead**: Cliente com Atendimento em status "Não finalizado" e **sem** Estadia ativa associada.

**Inquilino**: Cliente com Estadia ativa (cadastrada com status diferente de "Finalizada") associada ao seu nome. Pode opcionalmente ter Atendimento em status "Não finalizado".
_Alias de código_: `Tenant`

**Inquilino antigo**: Cliente com ao menos uma Estadia com status "Finalizada" no histórico e **sem** Atendimento em status "Não finalizado" no momento.

**SCV (Single Customer View)**: Visão centralizada do cliente que concentra dados de contato, Atendimento atual, histórico de Atendimentos e Estadias passadas em um único lugar.

---

### Proprietários

**Proprietário**: Pessoa que contratou o Corretor para administrar seus Imóveis. Possui dados de contato e chave de pagamento, e pode ter múltiplos Imóveis associados. Não é um Cliente: uma mesma pessoa que seja Proprietário e também alugue um Imóvel é cadastrada uma vez em cada contexto, sem vínculo entre os cadastros.
_Alias de código_: `PropertyOwner`
_Evitar_: Cliente (para Proprietário)

---

### Atendimento

**Atendimento**: Episódio de procura de um Lead por um Imóvel para estadia de temporada. Contém preferências de procura, Imóveis enviados como opção e registro de interações. Um cliente tem no máximo um Atendimento ativo por vez. Criar um Atendimento move o cliente para o estado Lead; excluí-lo reverte o estado.
_Alias de código_: `Assistance`

**Status do Atendimento**:
- **Não finalizado** — procura ativa; o Cliente é tratado como Lead (se sem Estadia ativa).
- **Finalizado** — encerrado pela conversão em Estadia. Transição irreversível; não é possível reabrir um Atendimento Finalizado.

**Preferências de procura**: Critérios coletados do Lead durante o Atendimento — número de hóspedes (adultos, crianças, bebês, animais de estimação), bairro desejado, período projetado de estadia, diária máxima aceita, distância ao mar, número de dormitórios e vagas de estacionamento.

**Imóveis enviados**: Lista de Imóveis apresentados pelo Corretor ao Lead como opções durante o Atendimento.

**Interação**: Registro de um contato realizado entre o Corretor e o Lead durante o Atendimento (mensagem, ligação etc.).
_Alias de código_: `Interaction`

**Origem do contato**: Meio pelo qual o Lead chegou ao Corretor em um Atendimento. Sua distribuição sobre todos os Atendimentos, finalizados ou não, é um KPI.
_Evitar_: Canal de contato, Origem
_Alias de código_: `contactOrigin`, `ContactChannel`

**Jornada de negociação**: Estado atual do cliente no funil de negociação — derivado da presença de Atendimento "Não finalizado" e/ou Estadia associada.

---

### Períodos Ocupados

**Período Ocupado**: Registro de um intervalo de tempo em que um Imóvel está indisponível para novas reservas. Possui dois subtipos: Estadia e Período indisponível.
_Alias de código_: `OccupiedPeriod`

**Estadia**: Período Ocupado com Inquilino associado. Representa uma locação por temporada. Possui período contratado, configuração financeira, número de hóspedes e status de progresso.
_Alias de código_: `Stay`

**Status da Estadia**:
- **Aguardando check-in** — check-in e check-out ainda não realizados.
- **Em andamento** — check-in realizado; check-out ainda não realizado.
- **Finalizada** — check-in e check-out realizados.

**Período indisponível**: Período Ocupado sem Inquilino associado. Intervalo em que um Imóvel não está disponível para Estadia por razão não relacionada a uma locação (ex: uso pelo Proprietário, manutenção). Considerado no controle de overbooking.
_Alias de código_: `UnavailablePeriod`

**Check-in**: Evento de entrada do Inquilino no Imóvel. Registrado pelo Corretor na Estadia; transiciona o status de "Aguardando check-in" para "Em andamento".

**Check-out**: Evento de saída do Inquilino do Imóvel. Registrado pelo Corretor na Estadia; transiciona o status para "Finalizada" e move o Cliente de Inquilino para Inquilino antigo (se não houver Atendimento "Não finalizado").

**Sinal** (Estadias): Valor transferido pelo Inquilino como confirmação da reserva antes do check-in, conforme definido na configuração do Imóvel.

**Movimentação financeira**: Registro de uma transferência monetária dentro de uma Estadia — data, valor, descrição, forma de pagamento (ex: Pix), titular e chave de pagamento.
_Alias de código_: `StayTransfer`

**Valor total da estadia**: Somatório das diárias do período contratado mais a taxa de limpeza.

**Comissão total da estadia**: Valor retido pelo Corretor como remuneração pela intermediação da Estadia.

**Valor pendente no check-in**: Parcela do aluguel não paga como sinal, devida pelo Inquilino no momento do check-in.

**Notificação de check-in/check-out**: Alerta enviado ao Corretor no início de cada manhã em que há check-ins ou check-outs de Estadias agendados para o mesmo dia.

---

### Relatórios Analíticos

**KPI**: Indicador-chave de desempenho calculado com base nos dados de Leads, Estadias e faturamento do Corretor.

**Faturamento**: Somatório das comissões totais das Estadias em um período.
_Alias de código_: `Invoice`

**Ticket médio**: Faturamento médio por Estadia em um período.

**Grupo de KPIs**: Agrupamento temático dos relatórios — "Leads", "Reservas" e "Faturamento".

---

### Sincronização

**Sincronização**: Envio dos dados registrados pelo Corretor no aparelho para o servidor, feito quando há conexão com a Internet. O Corretor trabalha sem depender de conexão.

---

## Diálogo de Exemplo

> **Dev**: Quando o Corretor cadastra um Atendimento para um Inquilino antigo, o que acontece?
>
> **Especialista**: O cliente passa a ser tratado como Lead — ele voltou a ter uma procura ativa. Se o Atendimento for excluído antes de gerar uma Estadia, ele volta para Inquilino antigo, porque ainda tem estadias passadas.
>
> **Dev**: E se o Corretor cadastrar uma Estadia para esse Lead?
>
> **Especialista**: Aí ele vira Inquilino. Quando o check-out for registrado nessa Estadia, o Atendimento fica como Finalizado. Se o cliente não tiver outro Atendimento "Não finalizado" aberto, volta para Inquilino antigo.
