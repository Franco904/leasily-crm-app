package com.fstengineering.leasilycrm.architecture.parsers

import org.amshove.kluent.invoking
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldThrow
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ReadmeModulesTest {
    @Nested
    @DisplayName("parse")
    inner class ParseTests {
        @Test
        fun `Deve extrair os módulos e seus contextos quando lê a tabela da seção de estrutura do projeto`() {
            val readme = """
                ## Estrutura do Projeto

                | Módulo                       | Contexto delimitado | Responsabilidade |
                |------------------------------|---------------------|------------------|
                | `core`                       | Compartilhado       | Shared kernel    |
                | `occupiedPeriodRegistration` | Períodos Ocupados   | Cadastro         |

                ---

                ## Arquitetura
            """.trimIndent()

            val result = ReadmeModules.parse(readme)

            result shouldBeEqualTo mapOf(
                "core" to "Compartilhado",
                "occupiedPeriodRegistration" to "Períodos Ocupados",
            )
        }

        @Test
        fun `Deve deixar o módulo sem contexto quando a coluna de contexto começa com travessão`() {
            val readme = """
                ## Estrutura do Projeto

                | `root` | —         | Inicialização |
                | `home` | — (shell) | Tela inicial  |
            """.trimIndent()

            val result = ReadmeModules.parse(readme)

            result shouldBeEqualTo mapOf("root" to null, "home" to null)
        }

        @Test
        fun `Deve ignorar as tabelas de outras seções quando lê a tabela de estrutura do projeto`() {
            val readme = """
                ## Estrutura do Projeto

                | `core` | Compartilhado | Shared kernel |

                ## Arquitetura

                | `presentation` | Guideline |
                | `infra`        | Guideline |
            """.trimIndent()

            val result = ReadmeModules.parse(readme)

            result shouldBeEqualTo mapOf("core" to "Compartilhado")
        }

        @Test
        fun `Deve ignorar linhas que não são módulo quando o primeiro campo não é um nome entre crases`() {
            val readme = """
                ## Estrutura do Projeto

                | Módulo | Contexto delimitado |
                |--------|---------------------|
                | `core` | Compartilhado       |
                | `docs/adr/` | Decisões       |
            """.trimIndent()

            val result = ReadmeModules.parse(readme)

            result shouldBeEqualTo mapOf("core" to "Compartilhado")
        }

        @Test
        fun `Deve falhar quando o README não tem a seção de estrutura do projeto`() {
            invoking { ReadmeModules.parse("## Outra seção") } shouldThrow
                IllegalStateException::class
        }

        @Test
        fun `Deve falhar quando a linha do módulo não tem a coluna de contexto`() {
            val readme = """
                ## Estrutura do Projeto

                | `core` |
            """.trimIndent()

            invoking { ReadmeModules.parse(readme) } shouldThrow IllegalStateException::class
        }
    }
}
