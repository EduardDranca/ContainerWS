import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "3.2.1"
	id("io.spring.dependency-management") version "1.1.4"
	id("org.openapi.generator") version "6.3.0"
	kotlin("jvm") version "1.9.21"
	kotlin("plugin.spring") version "1.9.21"
}

group = "com.eddranca"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
	mavenCentral()
}

sourceSets {
	main {
		java {
			srcDir(layout.buildDirectory.dir("generated/src/main/java"))
		}
		resources {
			srcDir("$rootDir/src/resources")
		}
	}
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("io.swagger.core.v3:swagger-annotations:2.2.11")
	implementation("javax.servlet:servlet-api:2.5")
	implementation("javax.validation:validation-api:2.0.1.Final")
	implementation("javax.annotation:javax.annotation-api:1.3.2")
	implementation("org.openapitools:jackson-databind-nullable:0.2.2")

	implementation("org.jetbrains.kotlin:kotlin-reflect")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs += "-Xjsr305=strict"
		jvmTarget = "17"
	}
	dependsOn(tasks.openApiGenerate)
}

tasks.withType<Test> {
	useJUnitPlatform()
}

openApiGenerate {
	generatorName = "spring"
	inputSpec = "${projectDir}/src/main/resources/api/api.yml"
	outputDir = layout.buildDirectory.dir("generated").get().toString()
	apiPackage = "com.eddranca.api"
	modelPackage = "com.eddranca.model"
	configOptions = mapOf(
			"interfaceOnly" to "true",
			"useBeanValidation" to "true"
	)
}
