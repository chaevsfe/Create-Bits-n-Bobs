import java.util.zip.ZipEntry
import java.util.zip.ZipFile

plugins {
    id("net.fabricmc.fabric-loom") version "1.16-SNAPSHOT"
    `maven-publish`
}

group = property("maven_group") as String
version = "${property("mod_version")}+fabric-mc${property("minecraft_version")}"

base {
    archivesName.set(property("archives_base_name") as String)
}

repositories {
    mavenCentral()
    maven("https://maven.fabricmc.net/")
    maven("https://api.modrinth.com/maven") {
        content { includeGroup("maven.modrinth") }
    }
}

loom {
    mods {
        create("bits_n_bobs") {
            sourceSet(sourceSets.main.get())
        }
    }
}

sourceSets {
    main {
        resources.srcDir("src/generated/resources")
    }
}

repositories {
    flatDir {
        dirs("libs", "../../create-struts/StrutYourStuff-Fly/build/libs")
    }
}
val struts = ":StrutYourStuff:${property("struts_version")}+fabric-mc${property("minecraft_version")}"

dependencies {
    minecraft("com.mojang:minecraft:${property("minecraft_version")}")
    implementation("net.fabricmc:fabric-loader:${property("fabric_loader_version")}")
    implementation("net.fabricmc.fabric-api:fabric-api:${property("fabric_api_version")}")
    implementation("maven.modrinth:create-fly:${property("create_fabric_version")}")
    compileOnly(struts)

    compileOnly("com.google.code.findbugs:jsr305:3.0.2")
}

java {
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
    withSourcesJar()
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(25)
    options.compilerArgs.addAll(listOf("-Xmaxerrs", "10000", "-Xmaxwarns", "1000"))
}

tasks.withType<AbstractCopyTask>().configureEach {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.processResources {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    inputs.property("version", version)
    filesMatching("fabric.mod.json") {
        expand(
            "version" to project.version.toString(),
            "minecraft_dependency_version" to project.property("minecraft_dependency_version") as String,
            "fabric_loader_version" to project.property("fabric_loader_version") as String,
            "create_fabric_version_range" to project.property("create_fabric_version_range") as String,
        )
    }
}

tasks.jar {
    from("LICENSE")
    from("NOTICE")
}

tasks.named<Jar>("sourcesJar") {
    from("LICENSE")
    from("NOTICE")
}

val ownCodePrefix = "com/kipti/bnb/"

val allowedResourcePrefixes = listOf(
    "assets/bits_n_bobs/",
    "data/bits_n_bobs/",
    "META-INF/",
    "LICENSE",
    "NOTICE",
    "bits_n_bobs.mixins.json",
    "bits_n_bobs.client.mixins.json",
    "fabric.mod.json",
    "data/c/tags/",
    "data/create/tags/",
    "data/create/advancement/",
    "data/create/recipe/",
    "data/minecraft/tags/",
    "data/railways/tags/",
    "assets/create/models/",
    "assets/create_connected/models/",
    "assets/create_hypertube/models/",
    "assets/createcasing/models/",
    "assets/dndecor/models/",
)

afterEvaluate {
    val checkForeignNamespaces = tasks.register("checkForeignNamespaces") {
        val jarNames = listOf("remapJar", "remapSourcesJar", "jar", "sourcesJar")
                .filter { tasks.names.contains(it) }
                .let { names -> if (names.contains("remapJar")) names.filter { it.startsWith("remap") } else names }
        require(jarNames.isNotEmpty()) { "checkForeignNamespaces found no jar task to inspect" }
        val jarTasks = jarNames.map { tasks.named<Jar>(it).get() }
        dependsOn(jarTasks)
        val archives: List<Provider<RegularFile>> = jarTasks.map { it.archiveFile }
        val codePrefix = ownCodePrefix
        val prefixes = allowedResourcePrefixes
        doLast {
            val bad = mutableListOf<String>()
            var checked = 0
            for (provider in archives) {
                val jar: File = provider.get().asFile
                if (!jar.exists()) continue
                checked++
                val zip = ZipFile(jar)
                try {
                    val entries = zip.entries()
                    while (entries.hasMoreElements()) {
                        val entry: ZipEntry = entries.nextElement()
                        if (entry.isDirectory) continue
                        val name: String = entry.name
                        if (name.endsWith(".class") || name.endsWith(".java")) {
                            if (!name.startsWith(codePrefix)) bad.add(jar.name + "!" + name)
                        } else if (prefixes.none { p -> name.startsWith(p) }) {
                            bad.add(jar.name + "!" + name)
                        }
                    }
                } finally {
                    zip.close()
                }
            }
            if (checked == 0) {
                throw GradleException("checkForeignNamespaces inspected no archives")
            }
            if (bad.isNotEmpty()) {
                throw GradleException("Foreign namespace entries in published artifacts:\n" + bad.joinToString("\n"))
            }
        }
    }
    tasks.named("check") { dependsOn(checkForeignNamespaces) }
}

tasks.register("printCompileClasspath") {
    val cp = sourceSets.main.get().compileClasspath
    val out = layout.projectDirectory.file(".classpath.txt")
    doLast {
        out.asFile.writeText(cp.files.joinToString("\n") { it.absolutePath } + "\n")
    }
}
