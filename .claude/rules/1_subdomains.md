# Subdomínios — DDD Estratégico

> **Finalidade:** Classificar os subdomínios do aplicativo segundo o valor de negócio que entregam (
> principal, suporte ou genérico). Esta classificação orienta decisões táticas de modelagem da lógica
> de negócio e de arquitetura: onde investir em maior complexidade de design e onde usar soluções mais
> simples ou disponíveis no mercado.
>
> **Instrução para o Agente de IA:** Antes de sugerir padrões de modelagem (entidades ricas, VOs,
> casos de uso complexos) ou de arquitetura (Clean Architecture completa, inversão de dependências),
> verifique a classificação do subdomínio em questão. Aplique o padrão tático correspondente da
> seção "Guia de Padrões Táticos". Trate o mapeamento como heurística, não como regra absoluta — a
> complexidade real do código deve prevalecer sobre a categoria.

---

## Classificação dos Subdomínios

### Principal (Core)

> Diferencial competitivo do produto. Contém as regras de negócio mais complexas e exclusivas.
> Justifica o maior investimento em modelagem e arquitetura.

| Subdomínio                           | Módulo / Área                                                            | Descrição                                                                                                                                                                                                                                      |
|--------------------------------------|--------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Gestão de Clientes e Atendimento** | `leadTenantRegistration`, `leadTenantDetails`, `assistanceRegistration`  | Ciclo de vida derivado Lead/Inquilino/Inquilino antigo, SCV centralizada e Atendimento com preferências de busca. A lógica de transição de estado e o matching de imóveis com preferências são as regras de negócio mais exclusivas do produto |
| **Gestão de Períodos Ocupados**      | `occupiedPeriodRegistration`, `occupiedPeriods`, `occupiedPeriodDetails` | Ciclo completo de um Período Ocupado (Estadia e Período indisponível): do cadastro ao check-out, com prevenção de overbooking, rastreamento de movimentações financeiras e disparo de notificações de check-in/check-out                       |

---

### Suporte (Supporting)

> Necessário para o produto funcionar e apresenta alguma personalização de marca, mas não é o
> diferencial competitivo. Padrões mais simples são aceitáveis; modelagem rica só se a complexidade
> real exigir.

| Subdomínio                      | Módulo(s)                                                         | Observação                                                                                                                                                                                                       |
|---------------------------------|-------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Relatórios Analíticos**       | `dashboard`                                                       | Classificado como Suporte, mas próximo do Core: a monografia identifica ausência de analytics como principal lacuna dos concorrentes. Elevar para Core se a lógica de cômputo de KPIs acumular complexidade real |
| **Gestão de Imóveis**           | `propertyRegistration`, `propertyDashboard`, `propertyOccupation` | CRUD com estrutura de atributos rica (físicos, condomínio, financeiro por imóvel)                                                                                                                                |
| **Gestão de Proprietários**     | `propertyOwnerRegistration`                                       | Gestão de contatos; CRUD simples                                                                                                                                                                                 |
| **Autenticação e Login**        | `signin`, `profile`                                               | Google OAuth + token de sessão criptografado com AES-GCM; perfil profissional (Creci) configurado separadamente                                                                                                  |
| **Sincronização Offline-First** | distribuído nos módulos de cadastro                               | Lazy Writes com espera exponencial; controle de sessão única por usuário elimina conflitos; sem módulo dedicado                                                                                                  |

---

### Genérico (Generic)

> Resolvido por bibliotecas de mercado. Não deve receber modelagem de domínio própria. O papel do
> código interno é apenas encapsular a biblioteca (Adapter) para permitir substituição futura, se
> necessário.

| Subdomínio                        | Biblioteca utilizada                               |
|-----------------------------------|----------------------------------------------------|
| **Criptografia local de token**   | Android Keystore + AES-GCM (nativo do Android SDK) |
| **Armazenamento de mídia remota** | Firebase Cloud Storage                             |
| **Log e monitoramento de falhas** | Firebase Crashlytics                               |
| **Analytics de uso**              | Firebase Analytics                                 |

---

## Guia de Padrões Táticos

A classificação do subdomínio é a principal heurística para escolher entre padrões de lógica de
negócio e de arquitetura.

### Padrão de Lógica de Negócio

| Classificação | Padrão indicado                                   | Como aplicar                                                                                                                                                              |
|---------------|---------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Principal** | **Modelo de Domínio**                             | Entidades ricas com comportamento, Value Objects com invariantes, erros tipados via `AppResult`/`DomainError`/`BusinessRuleError`, casos de uso que orquestram agregados  |
| **Suporte**   | **Script de Transação** ou Modelo de Domínio leve | Avalie a complexidade real: se há ramificações de negócio relevantes, prefira Modelo de Domínio; se é recuperação e persistência direta, Script de Transação é suficiente |
| **Genérico**  | **Nenhum** — delegar à biblioteca                 | Criar um domínio próprio seria reimplementar o que a biblioteca já resolve; use apenas um Adapter para encapsulá-la                                                       |

### Padrão de Arquitetura

| Classificação | Padrão indicado                                   | Como aplicar                                                                                                                             |
|---------------|---------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------|
| **Principal** | **Clean Architecture**                            | Camadas completas: `presentation/application/domain/infra`. Inversão de dependências obrigatória: `domain` não depende de nada externo   |
| **Suporte**   | **Script de Transação** ou estrutura mais simples | `presentation/application/infra`, `presentation/infra` ou apenas `presentation/` — grau proporcional à complexidade real da orquestração |
| **Genérico**  | **Adapter simples**                               | Uma classe que encapsula a biblioteca. Sem camadas de domínio, sem repositórios — apenas a interface necessária para o restante do app   |

---

## Heurística, não Dogma

O mapeamento acima é um ponto de partida estratégico. As seguintes situações justificam revisar ou
ignorar a categoria:

- **Suporte mais complexo que o esperado:** um subdomínio classificado como suporte pode desenvolver
  regras de negócio sofisticadas ao longo do tempo. Se o código começa a acumular condicionais e
  lógica de estado relevante, eleve o padrão para Modelo de Domínio independentemente da categoria.
- **Principal mais simples que o esperado:** nem toda funcionalidade de Períodos Ocupados ou
  Atendimento é igualmente complexa. Um método simples de leitura dentro de um subdomínio principal
  não precisa de agregado — use o julgamento devido.
- **Genérico que cresce:** se uma biblioteca de mercado não atende mais ao caso de uso e lógica
  própria começa a surgir, o subdomínio pode ter migrado para suporte. Sinalize para revisão da
  classificação.

**Regra prática:** quando o código e a categoria divergirem, confie no código. A classificação
existe para orientar decisões, não para engessá-las.
