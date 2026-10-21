import groovy.json.JsonSlurper
import org.gradle.testing.jacoco.tasks.JacocoReport

// Apply in each Android module. These paths are the uninstrumented outputs
// of the pinned AGP's built-in Kotlin compiler and javac, not jacocoDebug/dirs.
val policyFile = rootProject.file("gradle/coverage-policy.json")
val policy = JsonSlurper().parse(policyFile) as Map<*, *>
val exclusions = (policy["classExclusions"] as List<*>).map { it as String }
val kotlinClasses = fileTree(layout.buildDirectory.dir("intermediates/built_in_kotlinc/debug/compileDebugKotlin/classes")) {
    exclude(exclusions)
}
val javaClasses = fileTree(layout.buildDirectory.dir("intermediates/javac/debug/compileDebugJavaWithJavac/classes")) {
    exclude(exclusions)
}
val unitData = layout.buildDirectory.file("outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec")
val androidData = fileTree(layout.buildDirectory.dir("outputs/code_coverage/debugAndroidTest/connected")) {
    include("**/*.ec")
}

fun registerCoverageReport(name: String, suite: String, unit: Boolean, instrumented: Boolean) =
    tasks.register<JacocoReport>(name) {
        group = "verification"
        description = "Generates filtered $suite JaCoCo XML and HTML coverage."
        if (unit) dependsOn("testDebugUnitTest")
        if (instrumented) dependsOn("connectedDebugAndroidTest")
        classDirectories.setFrom(kotlinClasses, javaClasses)
        sourceDirectories.setFrom(files("src/main/java", "src/main/kotlin"))
        if (unit) executionData.from(unitData)
        if (instrumented) executionData.from(androidData)
        inputs.file(policyFile)
        reports {
            xml.required.set(true)
            xml.outputLocation.set(layout.buildDirectory.file("reports/jacoco/$suite/coverage.xml"))
            html.required.set(true)
            html.outputLocation.set(layout.buildDirectory.dir("reports/jacoco/$suite/html"))
            csv.required.set(false)
        }
    }

val unitReport = registerCoverageReport("jacocoDebugUnitReport", "unit", true, false)
val androidReport = registerCoverageReport("jacocoDebugAndroidReport", "android", false, true)
val combinedReport = registerCoverageReport("jacocoDebugCombinedReport", "combined", true, true)

tasks.register<Exec>("verifyAggregatedCoverage") {
    group = "verification"
    description = "Checks weighted module/variant minimums in the saved aggregated dashboard."
    val reportFile = layout.buildDirectory.file("reports/aggregated_code_coverage_html_report/global/data/report-data.js")
    inputs.files(reportFile, policyFile, rootProject.file("scripts/coverage_policy.py"))
    commandLine("python3", rootProject.file("scripts/coverage_policy.py"),
        "--aggregated-report", reportFile.get().asFile)
    mustRunAfter("createAggregatedCoverageReport")
}

tasks.matching { it.name == "createAggregatedCoverageReport" }.configureEach {
    finalizedBy("verifyAggregatedCoverage")
}

tasks.register<Exec>("verifyDebugCoverage") {
    group = "verification"
    description = "Runs both suites, emits separate and aggregated reports, and checks module minimums."
    dependsOn(unitReport, androidReport, combinedReport, "createAggregatedCoverageReport")
    val reportFile = layout.buildDirectory.file("reports/jacoco/combined/coverage.xml")
    inputs.files(reportFile, policyFile, rootProject.file("scripts/coverage_policy.py"))
    commandLine("python3", rootProject.file("scripts/coverage_policy.py"),
        "--report", "${project.name}=${reportFile.get().asFile}")
}
