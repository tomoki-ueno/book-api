import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	id("org.springframework.boot") version "3.5.13"
	id("io.spring.dependency-management") version "1.1.7"
	id("nu.studer.jooq") version "9.0"

}

group = "tomoki-ueno"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-jooq")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.flywaydb:flyway-core")
	implementation("org.flywaydb:flyway-database-postgresql")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	developmentOnly("org.springframework.boot:spring-boot-docker-compose")
	runtimeOnly("org.postgresql:postgresql")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	jooqGenerator("org.postgresql:postgresql:42.7.3")
	implementation("org.jooq:jooq:3.19.31")
	implementation("org.springframework.boot:spring-boot-starter-jdbc")
	implementation(kotlin("stdlib-jdk8"))
	testImplementation("org.mockito.kotlin:mockito-kotlin:5.2.1")
	testImplementation("org.mockito:mockito-inline:5.2.0")
	testImplementation("org.mockito:mockito-core:5.12.0")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

jooq {
	version.set("3.19.31")
	configurations {
		create("main") {
			jooqConfiguration.apply {
				logging = org.jooq.meta.jaxb.Logging.WARN

				jdbc = org.jooq.meta.jaxb.Jdbc().apply {
					driver = "org.postgresql.Driver"
					url = "jdbc:postgresql://localhost:5432/bookdb"
					user = "bookuser"
					password = "bookpass"  // 必要に応じて変更
				}

				generator = org.jooq.meta.jaxb.Generator().apply {
					name = "org.jooq.codegen.KotlinGenerator"

					database = org.jooq.meta.jaxb.Database().apply {
						name = "org.jooq.meta.postgres.PostgresDatabase"
						inputSchema = "public"
						includes = ".*"   // 全テーブル対象
						excludes = ""      // 除外なし
					}

					generate = org.jooq.meta.jaxb.Generate().apply {
						isPojos = true
						isDaos = false
						isImmutablePojos = false
						isFluentSetters = true
					}

					target = org.jooq.meta.jaxb.Target().apply {
						packageName = "dev.tomoki.bookapi.jooq"
						directory = "src/main/generated"
					}
				}
			}
		}
	}
}
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
	jvmTarget = "21"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
	jvmTarget = "21"
}

sourceSets {
	main {
		java {
			srcDir("src/main/generated")
		}
	}
}
