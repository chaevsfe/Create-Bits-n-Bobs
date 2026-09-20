import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import javax.imageio.ImageIO

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
        dirs(
            "libs",
            "../../create-struts/StrutYourStuff-Fly/build/libs",
            "../../create-rei/CreateReiViewer-Fly/build/libs",
        )
    }
}
val struts = ":StrutYourStuff:${property("struts_version")}+fabric-mc${property("minecraft_version")}"
val recipeViewer = ":CreateReiViewer:${property("createreiviewer_version")}+fabric-mc${property("minecraft_version")}"

dependencies {
    minecraft("com.mojang:minecraft:${property("minecraft_version")}")
    implementation("net.fabricmc:fabric-loader:${property("fabric_loader_version")}")
    implementation("net.fabricmc.fabric-api:fabric-api:${property("fabric_api_version")}")
    implementation("maven.modrinth:create-fly:${property("create_fabric_version")}")
    compileOnly(struts)
    include(struts)
    include(recipeViewer)

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

val generatedConnectedTextureResources = layout.buildDirectory.dir("generated/connected-texture-resources")

val omniTileIndexes = listOf(
    1, 2, 3, 8, 9, 10, 11, 12, 13, 16, 17, 18, 19, 20, 21, 24, 25, 26, 27, 28, 29, 30,
    32, 33, 34, 35, 36, 37, 38, 40, 41, 42, 43, 44, 45, 46, 48, 49, 50, 51, 52, 53, 54, 56, 57, 58,
)
val rectangleTileIndexes = (0..11).toList() + (13..15).toList()
val verticalTileIndexes = listOf(1, 2, 3)

val connectedTextureSheets = mapOf(
    "assets/bits_n_bobs/textures/block/industrial_grating_connected.png" to (8 to omniTileIndexes),
    "assets/bits_n_bobs/textures/block/industrial_grating_cutout_connected.png" to (8 to omniTileIndexes),
    "assets/bits_n_bobs/textures/block/industrial_grating_pipe_cutout_connected.png" to (8 to omniTileIndexes),
    "assets/bits_n_bobs/textures/block/weathered_girder_pole_side_connected.png" to (2 to verticalTileIndexes),
)

val generateConnectedTextureSprites = tasks.register("generateConnectedTextureSprites") {
    val resourceRoot = file("src/main/resources")
    inputs.files(fileTree(resourceRoot) { include(connectedTextureSheets.keys) })
        .withPropertyName("connectedTextureSheets")
        .withPathSensitivity(PathSensitivity.RELATIVE)
    inputs.property("connectedTextureLayout", "create-fly-26.2-v1")
    outputs.dir(generatedConnectedTextureResources)
    doLast {
        val outputRoot = generatedConnectedTextureResources.get().asFile
        delete(outputRoot)
        var sheetCount = 0
        var spriteCount = 0
        connectedTextureSheets.forEach { (pattern, layout) ->
            val (gridSize, tileIndexes) = layout
            fileTree(resourceRoot) { include(pattern) }.files.sortedBy { it.invariantSeparatorsPath }.forEach { sheetFile ->
                val sheet = ImageIO.read(sheetFile) ?: throw GradleException("Could not decode $sheetFile")
                if (sheet.width != sheet.height || sheet.width % gridSize != 0) {
                    throw GradleException("$sheetFile must be a $gridSize x $gridSize grid of square tiles, but is ${sheet.width} x ${sheet.height}")
                }
                val tileSize = sheet.width / gridSize
                val relativeSheet = resourceRoot.toPath().relativize(sheetFile.toPath()).toString()
                val spriteDirectory = outputRoot.resolve(relativeSheet.removeSuffix(".png"))
                spriteDirectory.mkdirs()
                tileIndexes.forEachIndexed { index, sourceTileIndex ->
                    val tile = sheet.getSubimage(sourceTileIndex % gridSize * tileSize, sourceTileIndex / gridSize * tileSize, tileSize, tileSize)
                    if (!ImageIO.write(tile, "png", spriteDirectory.resolve("${index + 1}.png"))) {
                        throw GradleException("No PNG writer is available for $spriteDirectory")
                    }
                    spriteCount++
                }
                sheetCount++
            }
        }
        logger.lifecycle("Generated $spriteCount connected-texture sprites from $sheetCount sheets")
    }
}

tasks.processResources {
    dependsOn(generateConnectedTextureSprites)
    from(generatedConnectedTextureResources)
    exclude(connectedTextureSheets.keys)
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    val modMetadata = mapOf(
        "version" to project.version.toString(),
        "minecraft_dependency_version" to project.property("minecraft_dependency_version") as String,
        "fabric_loader_version" to project.property("fabric_loader_version") as String,
        "create_fabric_version_range" to project.property("create_fabric_version_range") as String,
    )
    inputs.properties(modMetadata)
    filesMatching("fabric.mod.json") {
        expand(modMetadata)
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
    "assets/create/blockstates/",
    "assets/create/models/",
    "assets/create_connected/models/",
    "assets/create_hypertube/models/",
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
