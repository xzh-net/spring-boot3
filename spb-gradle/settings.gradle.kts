rootProject.name = "gradle-demo"
dependencyResolutionManagement{
    repositories{
        maven{
            setUrl("https://maven.aliyun.com/repository/public")
        }
        mavenCentral()
    }
}
pluginManagement{
    repositories{
        maven{
            setUrl("https://maven.aliyun.com/repository/public")
        }
        mavenCentral()
    }
}