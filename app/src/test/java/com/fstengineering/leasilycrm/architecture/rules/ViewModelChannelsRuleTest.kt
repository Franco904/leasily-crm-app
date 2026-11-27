package com.fstengineering.leasilycrm.architecture.rules

import com.lemonappdev.konsist.api.Konsist
import org.amshove.kluent.shouldBeEmpty
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

class ViewModelChannelsRuleTest {
    @Nested
    @DisplayName("violations")
    inner class ViolationsTests {
        @Test
        fun `Deve aceitar a tela quando o descarte fecha o canal`(@TempDir dir: File) {
            plant(dir, "FooViewModel", viewModel("$CHANNEL\n${onCleared("events.close()")}"))

            val result = ViewModelChannelsRule.violations(scopeOf(dir))

            result.shouldBeEmpty()
        }

        @Test
        fun `Deve reportar o canal quando não há descarte`(@TempDir dir: File) {
            plant(dir, "FooViewModel", viewModel(CHANNEL))

            val result = ViewModelChannelsRule.violations(scopeOf(dir))

            result shouldBeEqualTo listOf(unclosed("events"))
        }

        @Test
        fun `Deve reportar o canal quando o fechamento fica fora do descarte`(@TempDir dir: File) {
            plant(
                dir,
                "FooViewModel",
                viewModel("$CHANNEL\n fun stop() { events.close() }\n${onCleared("")}"),
            )

            val result = ViewModelChannelsRule.violations(scopeOf(dir))

            result shouldBeEqualTo listOf(unclosed("events"))
        }

        @Test
        fun `Deve reportar o canal quando o fechamento está comentado`(@TempDir dir: File) {
            plant(
                dir,
                "FooViewModel",
                viewModel("$CHANNEL\n${onCleared("// events.close()\n")}"),
            )

            val result = ViewModelChannelsRule.violations(scopeOf(dir))

            result shouldBeEqualTo listOf(unclosed("events"))
        }

        @Test
        fun `Deve reportar o canal quando o descarte fecha outro de nome parecido`(
            @TempDir dir: File,
        ) {
            plant(
                dir,
                "FooViewModel",
                viewModel("$CHANNEL\n${onCleared("otherevents.close()")}"),
            )

            val result = ViewModelChannelsRule.violations(scopeOf(dir))

            result shouldBeEqualTo listOf(unclosed("events"))
        }

        @Test
        fun `Deve reportar cada canal aberto em ordem alfabética quando há mais de um`(
            @TempDir dir: File,
        ) {
            plant(
                dir,
                "FooViewModel",
                viewModel(
                    "private val zeta = Channel<List<Int>>(Channel.BUFFERED)\n" +
                        "private val closed = Channel<Int>()\n" +
                        "private val alfa: Channel<Int> = Channel()\n" +
                        onCleared("closed.close()"),
                ),
            )

            val result = ViewModelChannelsRule.violations(scopeOf(dir))

            result shouldBeEqualTo listOf(unclosed("alfa"), unclosed("zeta"))
        }

        @Test
        fun `Deve reportar o canal quando a classe herda de uma tela sem o sufixo no nome`(
            @TempDir dir: File,
        ) {
            plant(dir, "FooHolder", "class FooHolder : ViewModel() {\n $CHANNEL\n }")

            val result = ViewModelChannelsRule.violations(scopeOf(dir))

            result shouldBeEqualTo listOf(
                "FooHolder.kt: o canal `events` de `FooHolder` deve ser fechado em `onCleared` " +
                    "com `events.close()`",
            )
        }

        @Test
        fun `Deve reportar o canal quando a classe só indica a tela pelo sufixo do nome`(
            @TempDir dir: File,
        ) {
            plant(dir, "FooViewModel", "class FooViewModel : Base() {\n $CHANNEL\n }")

            val result = ViewModelChannelsRule.violations(scopeOf(dir))

            result shouldBeEqualTo listOf(unclosed("events"))
        }

        @Test
        fun `Deve reportar o canal quando ele é exposto só como ponta de envio`(@TempDir dir: File) {
            plant(
                dir,
                "FooViewModel",
                viewModel("private val events: SendChannel<Int> = Channel()"),
            )

            val result = ViewModelChannelsRule.violations(scopeOf(dir))

            result shouldBeEqualTo listOf(unclosed("events"))
        }

        @Test
        fun `Deve reportar o canal quando há comentário antes da criação`(@TempDir dir: File) {
            plant(
                dir,
                "FooViewModel",
                viewModel("private val events = /* fila */ Channel<Int>()"),
            )

            val result = ViewModelChannelsRule.violations(scopeOf(dir))

            result shouldBeEqualTo listOf(unclosed("events"))
        }

        @Test
        fun `Deve aceitar o canal quando ele não pertence a uma tela`(@TempDir dir: File) {
            plant(
                dir,
                "FooScreen",
                "class FooState { $CHANNEL }\n fun FooScreen() { val local = Channel<Int>() }",
            )

            val result = ViewModelChannelsRule.violations(scopeOf(dir))

            result.shouldBeEmpty()
        }

        @Test
        fun `Deve aceitar a tela quando a propriedade só cita um canal`(@TempDir dir: File) {
            plant(
                dir,
                "FooViewModel",
                viewModel(
                    "private val notification = NotificationChannel()\n" +
                        "private val size = Channel.BUFFERED\n" +
                        "private val text = \"Channel()\"\n" +
                        "fun load() { val local = Channel<Int>() }",
                ),
            )

            val result = ViewModelChannelsRule.violations(scopeOf(dir))

            result.shouldBeEmpty()
        }

        @Test
        fun `Deve reportar o canal quando ele é atribuído na inicialização`(@TempDir dir: File) {
            plant(
                dir,
                "FooViewModel",
                viewModel("private val events: Channel<Int>\n init { events = Channel() }"),
            )

            val result = ViewModelChannelsRule.violations(scopeOf(dir))

            result shouldBeEqualTo listOf(unclosed("events"))
        }

        @Test
        fun `Deve reportar o canal quando o tipo dele quebra linha`(@TempDir dir: File) {
            plant(
                dir,
                "FooViewModel",
                viewModel("private val events = Channel<\n List<Int>,\n >()"),
            )

            val result = ViewModelChannelsRule.violations(scopeOf(dir))

            result shouldBeEqualTo listOf(unclosed("events"))
        }

        @Test
        fun `Deve reportar o canal quando ele é criado sob demanda`(@TempDir dir: File) {
            plant(
                dir,
                "FooViewModel",
                viewModel("private val events by lazy { Channel<Int>() }"),
            )

            val result = ViewModelChannelsRule.violations(scopeOf(dir))

            result shouldBeEqualTo listOf(unclosed("events"))
        }

        @Test
        fun `Deve reportar o canal quando ele é declarado no construtor`(@TempDir dir: File) {
            plant(
                dir,
                "FooViewModel",
                "class FooViewModel(private val events: Channel<Int>) : ViewModel()",
            )

            val result = ViewModelChannelsRule.violations(scopeOf(dir))

            result shouldBeEqualTo listOf(unclosed("events"))
        }

        @Test
        fun `Deve reportar o canal quando o descarte fecha o de outro objeto`(@TempDir dir: File) {
            plant(
                dir,
                "FooViewModel",
                viewModel("$CHANNEL\n${onCleared("other.events.close()")}"),
            )

            val result = ViewModelChannelsRule.violations(scopeOf(dir))

            result shouldBeEqualTo listOf(unclosed("events"))
        }

        @Test
        fun `Deve aceitar a tela quando o descarte fecha o canal com chamada segura`(@TempDir dir: File) {
            plant(
                dir,
                "FooViewModel",
                viewModel("$CHANNEL\n${onCleared("events?.close()")}"),
            )

            val result = ViewModelChannelsRule.violations(scopeOf(dir))

            result.shouldBeEmpty()
        }

        @Test
        fun `Deve aceitar a tela quando o descarte fecha o canal informando a causa`(@TempDir dir: File) {
            plant(
                dir,
                "FooViewModel",
                viewModel("$CHANNEL\n${onCleared("this.events.close(cause)")}"),
            )

            val result = ViewModelChannelsRule.violations(scopeOf(dir))

            result.shouldBeEmpty()
        }

        @Test
        fun `Deve aceitar a tela quando o canal é local de um fluxo`(@TempDir dir: File) {
            plant(
                dir,
                "FooViewModel",
                viewModel("val items = flow { val local = Channel<Int>() }"),
            )

            val result = ViewModelChannelsRule.violations(scopeOf(dir))

            result.shouldBeEmpty()
        }

        @Test
        fun `Deve ignorar a tela quando ela não é de produção`(@TempDir dir: File) {
            plant(dir, "FooViewModel", viewModel(CHANNEL), sourceSet = "test")

            val result = ViewModelChannelsRule.violations(scopeOf(dir))

            result.shouldBeEmpty()
        }
    }

    private fun viewModel(body: String) = "class FooViewModel : ViewModel() {\n$body\n}"

    private fun onCleared(body: String) = "override fun onCleared() {\n$body\n}"

    private fun unclosed(name: String) =
        "FooViewModel.kt: o canal `$name` de `FooViewModel` deve ser fechado em `onCleared` " +
            "com `$name.close()`"

    private fun scopeOf(dir: File) = Konsist.scopeFromExternalDirectory(dir.absolutePath)

    private fun plant(dir: File, fileName: String, body: String, sourceSet: String = "main") {
        File(dir, "src/$sourceSet/a/b/$fileName.kt")
            .also { it.parentFile.mkdirs() }
            .writeText("package a.b\n\n$body\n")
    }

    private companion object {
        const val CHANNEL = "private val events = Channel<Int>(Channel.BUFFERED)"
    }
}
