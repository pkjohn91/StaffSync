plugins {
	java
	id("org.springframework.boot") version "4.0.0"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
description = "prtc project for Spring Boot"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(25)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-h2console")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-webmvc")
	compileOnly("org.projectlombok:lombok")
	runtimeOnly("com.h2database:h2")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-data-jpa-test")
	testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	// 1. 유효성 검사 (Validation)
    implementation("org.springframework.boot:spring-boot-starter-validation")
    // 2. 이메일 발송 (Mail)
    implementation("org.springframework.boot:spring-boot-starter-mail")
}

tasks.withType<Test> {
    useJUnitPlatform()
    // ▼ Java 21 이상에서 Mockito 경고 없애기 (허용 옵션 추가)
    jvmArgs("-XX:+EnableDynamicAgentLoading") 
}
