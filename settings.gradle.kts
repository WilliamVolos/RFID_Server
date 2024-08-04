rootProject.name = "RFID_Server"
//include("server_main")

pluginManagement {
    val jgitver: String by settings
    val dependencyManagement: String by settings
    val springframeworkBoot: String by settings
    val johnrengelmanShadow: String by settings
    val jib: String by settings
    val protobufVer: String by settings
    val sonarlint: String by settings
    val spotless: String by settings

    plugins {
        id("fr.brouillard.oss.gradle.jgitver") version jgitver
        id("io.spring.dependency-management") version dependencyManagement // Управление зависимостями
        id("org.springframework.boot") version springframeworkBoot
        id("com.github.johnrengelman.shadow") version johnrengelmanShadow // Толстый Jar-ник
        id("com.google.cloud.tools.jib") version jib  // Для сборки докер образа
        id("com.google.protobuf") version protobufVer // Для компиляции и автогенерации файлов
        id("name.remal.sonarlint") version sonarlint // Проверяльщик качества кода, если что-то не нравится проект падает
        id("com.diffplug.spotless") version spotless // Для форматирования кода
    }
}
