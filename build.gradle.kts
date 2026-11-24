// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.parcelize) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.room) apply false
    alias(libs.plugins.spotless)
}

// Higiene de código (imports, espaços, ponto-e-vírgula): o layout é da IDE (scripts/format/format.sh).
// Restrito ao que mudou desde o ref-base da branch (scripts/format/ratchet-base.sh); -PratchetFrom
// sobrescreve. O ratchet só é resolvido quando uma task do Spotless foi pedida, para que
// sync da IDE, run e test não dependam do estado do git.
spotless {
    if (gradle.startParameter.taskNames.any { it.contains("spotless") }) {
        ratchetFrom(
            providers.gradleProperty("ratchetFrom").orElse(
                providers.exec { commandLine("scripts/format/ratchet-base.sh") }
                    .standardOutput.asText.map { it.trim() },
            ).get(),
        )
    }

    kotlin {
        target("app/src/**/*.kt")
        ktlint(libs.versions.ktlint.get())
    }
}
