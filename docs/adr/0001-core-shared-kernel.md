# Critério de inclusão no Shared Kernel (módulo `core`)

O módulo `core` aceita apenas artefatos invocados por dois ou mais contextos delimitados sem
conflito nem adaptação semântica. Exemplo: um mesmo Use Case e/ou Repository que realiza a exclusão
de um mesmo agregado e com equivalência semântica de intenção.

A alternativa — usar `core` como atalho para qualquer código compartilhado — gradualmente erodiria
as fronteiras de contexto e tornaria `core` um repositório de
código acumulado sem dono claro. Código que é conveniente para mais de um contexto mas que carrega
semântica (ou razão para mudar) de apenas um deles permanece no contexto de origem.

## Exceção: `AppDatabase`

O `AppDatabase` do Room permanece em `core/` mesmo declarando os `@Dao` (DataSources) que
pertencem a módulos específicos — o Room exige que todo DAO seja método abstrato do `@Database`. É a
única dependência de `core/` para os módulos; nenhum outro artefato de `core/` pode importá-los.

## Exceção: schema do banco

As `@Entity` (Orm) ficam em `core/infra/dataSources/database/entities/`, sem dono: a tabela é
compartilhada por todos os módulos, e cada um lê ou escreve nela pelo seu próprio DataSource `@Dao`.
Mover o Orm para o módulo que escreve obrigaria `core/` (FKs entre Orms, `entities = [...]`) a
importar módulos, e tabelas escritas por mais de um módulo não teriam dono único.
