// 插件声明块，用于指定项目构建所需要的插件
plugins {
    // 使用Gradle内置的Java插件，提供Java项目的构建支持
    java
    // 使用Spring Boot插件，并指定其版本为3.4.1，用于支持Spring Boot项目的构建和管理
    id("org.springframework.boot") version "3.4.1"
    // 使用Spring的依赖管理插件，版本为1.1.7，它可以帮助管理项目的依赖关系
    id("io.spring.dependency-management") version "1.1.7"
    id("maven-publish")
}
publishing { // 配置发布插件
    publications { // 定义发布物
        create<MavenPublication>("library") { // 创建一个名为"library"的Maven发布物
            groupId = "net.xzh" // 设置发布物的groupId
            artifactId = "gradle-demo" // 设置发布物的artifactId
            version = "0.0.1-SNAPSHOT" // 设置发布物的版本
            from(components["java"]) // 从java组件（通常是Java库）中获取发布内容
        }
    }
}
// 设置项目的组ID，通常用于表示项目的组织或团队
group = "net.xzh"
// 设置项目的版本号
version = "0.0.1-SNAPSHOT"

// Java配置块
java {
    // 设置Java源代码的兼容性版本为21，即Java 21
    sourceCompatibility = JavaVersion.VERSION_21
}

//让 compileOnly 配置继承 annotationProcessor 配置中的所有依赖，
// 这样，添加到 compileOnly 中的任何库也将被视为注解处理器，但仅在编译时使用
// 不会在运行时包含在内。
configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}


// 依赖声明块，定义项目所依赖的库及其版本
dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    //!!代表强制使用该依赖，不写默认使用高版本
    implementation("com.belerweb:pinyin4j:2.5.1!!"){
        //只要是关联的子依赖以net.xzh开头都会自动排除掉
        exclude("net.xzh")
    }
    /*扫描lib目录下所有jar，并增加到依赖中*/
    implementation(fileTree("lib"))
    // 编译时依赖，Lombok库，用于简化Java类的编写，如getter、setter等
    compileOnly("org.projectlombok:lombok")
    /*
    你在类上使用Lombok提供的注解（如@Getter、@Setter等）时，
    Lombok的注解处理器会自动为你的类生成getter和setter方法，
    这样你就不需要手动编写这些方法了。为了让Gradle知道在编译时要调用Lombok的注解处理器，
    你就需要在Gradle构建脚本中添加Lombok作为annotationProcessor的依赖。
    * */
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    runtimeOnly("mysql:mysql-connector-java:8.0.33")
}

// 任务配置块，针对特定类型的任务进行配置
tasks.withType<Test> {
    // 使用JUnit Platform来执行测试，这是JUnit 5的推荐执行方式
    useJUnitPlatform()
}