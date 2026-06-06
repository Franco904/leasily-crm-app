# Domínio independente e centralizado

A camada de domínio fica no núcleo da arquitetura e desconhece as demais camadas (Application,
Infra, Presentation). Agregados protegem invariantes via métodos factory de parsing ("Parse, don't
validate") e são mantidos pequenos — sem composição entre agregados distintos — para preservar a
unidade de consistência lógica. Quando uma invariante atravessa mais de um Agregado, um Domain
Service recebe os dados necessários via Use Case; a alternativa de compor agregados maiores tornaria
as fronteiras de consistência ambíguas e os testes de domínio acoplados a grafos de objetos
desnecessariamente complexos.
