package com.fstengineering.leasilycrm.architecture.parsers

import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ReadmeModulesTest {
    @Nested
    @DisplayName("parse")
    inner class ParseTests {
        @Test
        fun `Deve extrair os módulos quando lê a tabela da seção de estrutura do projeto`() {
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

            result shouldBeEqualTo setOf("core", "occupiedPeriodRegistration")
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

            result shouldBeEqualTo setOf("core")
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

            result shouldBeEqualTo setOf("core")
        }

        @Test
        fun `Deve falhar quando o README não tem a seção de estrutura do projeto`() {
            assertThrows<IllegalStateException> { ReadmeModules.parse("## Outra seção") }
        }
    }
}
