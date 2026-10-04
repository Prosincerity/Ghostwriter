import groovy.json.JsonSlurper
import java.math.BigDecimal
import java.math.RoundingMode

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    jacoco
}

jacoco {
    toolVersion = libs.versions.jacoco.get()
}

abstract class VerifyAggregatedCoverage : DefaultTask() {
    @get:InputFile
    @get:PathSensitive(PathSensitivity.NONE)
    abstract val coverageReport: RegularFileProperty

    @get:Input
    abstract val minimumInstruction: Property<Int>

    @get:Input
    abstract val minimumBranch: Property<Int>

    @TaskAction
    fun verify() {
        // AGP embeds JSON in its dashboard. Kotlin can generate multiple classes
        // from one source file; combine their aggregated counters per variant.
        val script = coverageReport.get().asFile.readText().trim()
        val prefix = "const fullReport = "
        check(script.startsWith(prefix) && script.endsWith(";")) {
            "Unrecognized aggregated coverage report format"
        }
        val report = JsonSlurper().parseText(script.removePrefix(prefix).removeSuffix(";")) as Map<*, *>
        val modules = report["modules"] as List<*>
        check(modules.isNotEmpty()) { "Aggregated coverage report has no modules" }
        val files = linkedMapOf<String, MutableList<Map<*, *>>>()
        for (module in modules.map { it as Map<*, *> }) {
            for (pkg in (module["packages"] as List<*>).map { it as Map<*, *> }) {
                for (cls in (pkg["classes"] as List<*>).map { it as Map<*, *> }) {
                    val source = cls["sourceFileName"] as? String
                    check(!source.isNullOrBlank()) { "Missing source file name for ${cls["name"]}" }
                    val suites = (cls["testSuiteCoverages"] as List<*>).map { it as Map<*, *> }
                    val aggregate = suites.single { it["name"] == "Aggregated" }
                    val variants = aggregate["variantCoverages"] as List<*>
                    check(variants.isNotEmpty()) { "No aggregated coverage variants for $source" }
                    for (variant in variants.map { it as Map<*, *> }) {
                        val file = "${module["name"]}/${pkg["name"]}/$source [${variant["name"]}]"
                        files.getOrPut(file) { mutableListOf() }.add(variant)
                    }
                }
            }
        }
        check(files.isNotEmpty()) { "Aggregated coverage report has no source files" }
        val failures = mutableListOf<String>()
        val minimums = listOf("instruction" to minimumInstruction.get(), "branch" to minimumBranch.get())
        for ((_, minimum) in minimums) {
            require(minimum in 0..100) { "Coverage minimum must be between 0 and 100" }
        }
        for ((file, variants) in files.toSortedMap()) {
            for ((metric, minimum) in minimums) {
                val counters = variants.map { it[metric] as Map<*, *> }
                check(counters.all {
                    val total = (it["total"] as Number).toLong()
                    total >= 0 && (it["covered"] as Number).toLong() in 0..total
                }) { "Invalid $metric coverage counters for $file" }
                val covered = counters.sumOf { (it["covered"] as Number).toLong() }
                val total = counters.sumOf { (it["total"] as Number).toLong() }
                // A metric with no executable instructions/branches is N/A.
                if (total == 0L) continue
                val percent = BigDecimal.valueOf(covered * 100)
                    .divide(BigDecimal.valueOf(total), 2, RoundingMode.HALF_UP)
                if (covered * 100 < total * minimum) {
                    failures += "$file: $metric coverage $percent% is below $minimum% ($covered/$total)"
                }
            }
        }
        logger.lifecycle("Checked ${files.size} source files/variants against per-file coverage minimums")
        if (failures.isNotEmpty()) throw GradleException(failures.joinToString("\n"))
    }
}

val verifyAggregatedCoverage = tasks.register<VerifyAggregatedCoverage>("verifyAggregatedCoverage") {
    group = "verification"
    description = "Checks each source file in the saved aggregated report against coverage minimums."
    coverageReport.set(layout.buildDirectory.file("reports/aggregated_code_coverage_html_report/global/data/report-data.js"))
    minimumInstruction.set(90)
    minimumBranch.set(80)
    mustRunAfter("createAggregatedCoverageReport")
}

tasks.matching { it.name == "createAggregatedCoverageReport" }.configureEach {
    finalizedBy(verifyAggregatedCoverage)
}

android {
    namespace = "com.prosincerity.ghostwriter"
    ndkVersion = "30.0.16248370"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.prosincerity.ghostwriter"
        minSdk = 24
        targetSdk = 37
        versionCode = 4
        versionName = "1.2.1-studio-polish.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        ndk {
            abiFilters += listOf("arm64-v8a", "x86_64")
        }
        externalNativeBuild {
            cmake {
                targets += "ghostwriter_ipa"
            }
        }
    }

    buildTypes {
        debug {
            enableUnitTestCoverage = true
            enableAndroidTestCoverage = true
        }
        release {
            optimization {
                enable = false
            }
        }
    }
    testCoverage {
        // Aggregated reports require the same JaCoCo version for JVM and Android tests.
        jacocoVersion = libs.versions.jacoco.get()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "4.1.2"
        }
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.core)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    testImplementation(libs.junit)
    testImplementation(libs.json)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
