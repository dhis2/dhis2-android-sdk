tasks.register("installGitHooks") {
    group = "git hooks"

    doFirst {
        val hooks = project.fileTree("scripts/hooks").files.map { it.name }
        description = "Installs the following git hooks: ${hooks.joinToString(", ")}."
    }

    doLast {
        val hooksDir = File(rootDir, ".git/hooks")
        if (!hooksDir.exists()) {
            hooksDir.mkdirs()
        }

        val hooks = project.fileTree("scripts/hooks")

        hooks.forEach { file ->
            val destination = File(hooksDir, file.name)
            file.copyTo(destination, overwrite = true)
            destination.setExecutable(true)
            println("Installed hook: ${file.name}")
        }

        println("All hooks installed successfully.")
    }
}

// Dokka Gradle plugin (v2) applies Gradle's base plugin, which registers "clean" itself,
// so reconfigure the existing task instead of registering a new one.
tasks.maybeCreate("clean", Delete::class.java).apply {
    delete(rootProject.layout.buildDirectory)
}

tasks.register("runChecks") {
    group = "verification"
    description = "Cleans the project, runs lint checks, and code quality checks."

    dependsOn(
        ":core:clean",
        ":core:ktlintCheck",
        ":core:detekt",
        ":core:lintDebug"
    )
}