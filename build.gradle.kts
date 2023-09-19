import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.ir.backend.js.compile

buildscript {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("java")
    id("org.springframework.boot") version "3.0.9"
    id("io.spring.dependency-management") version "1.1.2"
    kotlin("jvm") version "1.9.0"
    kotlin("plugin.spring") version "1.9.0"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

val mybatisPlusVersion = "3.5.3.2"
val mysqlConnectJavaVersion = "8.1.0"
val jjwtVersion = "0.9.1"
val javaxXMLVersion = "2.3.1"
val jacksonVersion = "2.14.2"
val lombokVersion = "1.18.28"
//val activiti7Version = "7.11.0"
val activiti7Version = "7.1.0.M6"
val weixinVersion = "4.5.0"


allprojects {
    repositories {
        mavenLocal()
        mavenCentral()
    }

}

subprojects {
    apply {
        plugin("java")
        plugin("org.jetbrains.kotlin.jvm")
        plugin("org.jetbrains.kotlin.plugin.spring")
        plugin("org.springframework.boot")
        plugin("io.spring.dependency-management")
    }

    sourceSets {
        main {
            java {
                srcDirs("src/main/java", "src/main/kotlin")
            }
            kotlin {
                srcDirs("src/main/java", "src/main/kotlin")
            }
        }
    }
    dependencies {
        compileOnly("org.projectlombok:lombok:${lombokVersion}")
        implementation("org.jetbrains.kotlin:kotlin-stdlib")
        // https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-logging
        implementation("org.springframework.boot:spring-boot-starter-logging")
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs += "-Xjsr305=strict"
            jvmTarget = "17"
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}

project(":OA-common:OA-common-utils") {
    dependencies {
        //jwt所需依赖
        implementation("javax.xml.bind:jaxb-api:${javaxXMLVersion}")
        // https://mvnrepository.com/artifact/com.baomidou/mybatis-plus-boot-starter
        implementation("com.baomidou:mybatis-plus-boot-starter:${mybatisPlusVersion}")
        implementation("org.springframework.boot:spring-boot-starter-web")
        // https://mvnrepository.com/artifact/com.alibaba.fastjson2/fastjson2
        api("com.fasterxml.jackson.module:jackson-module-kotlin:${jacksonVersion}")
        // https://mvnrepository.com/artifact/io.jsonwebtoken/jjwt
        api("io.jsonwebtoken:jjwt:${jjwtVersion}")
        // https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-test

    }
}

project(":OA-common:OA-service-utils") {
    dependencies {
        implementation("com.baomidou:mybatis-plus-boot-starter:${mybatisPlusVersion}")
        implementation("org.springframework.boot:spring-boot-starter-web")
        // https://mvnrepository.com/artifact/mysql/mysql-connector-java
        implementation(project(":OA-common:OA-common-utils"))
        implementation(project(":OA-model"))
        implementation("com.mysql:mysql-connector-j:${mysqlConnectJavaVersion}")
        // https://mvnrepository.com/artifact/io.jsonwebtoken/jjwt
        compileOnly("io.jsonwebtoken:jjwt:${jjwtVersion}")
        compileOnly("org.springframework.boot:spring-boot-starter-security")
    }
}
project("OA-common:OA-security") {
    dependencies {
        implementation(project(":OA-model"))
        implementation(project(":OA-common:OA-common-utils"))
        implementation(project(":OA-common:OA-service-utils"))
        api("org.springframework.boot:spring-boot-starter-security")
        implementation("org.springframework.boot:spring-boot-starter-web")
        implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
        implementation("org.jetbrains.kotlin:kotlin-reflect")
    }
}

project(":OA-model") {
    dependencies {
        api("com.baomidou:mybatis-plus-boot-starter:${mybatisPlusVersion}")
        api("com.fasterxml.jackson.module:jackson-module-kotlin:${jacksonVersion}")
    }

}
project(":OA-service") {
    dependencies {
        // https://mvnrepository.com/artifact/com.github.ben-manes.caffeine/caffeine
        implementation("com.github.ben-manes.caffeine:caffeine:3.1.8")
        // https://mvnrepository.com/artifact/com.alibaba.fastjson2/fastjson2
        implementation("com.alibaba.fastjson2:fastjson2:2.0.39")
        implementation("org.springframework.boot:spring-boot-starter-web")
        // https://mvnrepository.com/artifact/com.mysql/mysql-connector-j
        implementation("com.mysql:mysql-connector-j:${mysqlConnectJavaVersion}")
        implementation(project(":OA-model"))
        implementation(project(":OA-common:OA-common-utils"))
        implementation(project(":OA-common:OA-service-utils"))
        implementation(project(":OA-common:OA-security"))
        // https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-test
        testImplementation("org.springframework.boot:spring-boot-starter-test")
        // https://mvnrepository.com/artifact/org.activiti/activiti-engine
        implementation("org.activiti:activiti-spring-boot-starter:${activiti7Version}")
        // https://mvnrepository.com/artifact/com.github.binarywang/weixin-java-mp
        implementation("com.github.binarywang:weixin-java-mp:${weixinVersion}")
    }
}