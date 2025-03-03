## 开发AI智能体调用DeepSeek本地模型
如何在本地部署 DeepSeek 模型，并通过 Spring AI开发应用，调用大模型能力。

1. 下载 Ollama 并安装运行 DeepSeek 本地模型
2. 使用 Spring AI开发应用，调用 DeepSeek 模型
3. 无需联网、私有数据完全本地存储，为 Java 应用赋予 AI 智能

### 本地部署 DeepSeek Qwen 蒸馏版模型

#### MacOS & Windows 安装
进入 [Ollama 官方网站](https://ollama.com/) 后，可以看到 Ollama 已经支持 DeepSeek-R1 模型部署：

![](download.png)

点击 DeepSeek-R1 的链接可以看到有关 deepseek-r1 的详细介绍。

点击 `Download` 按钮下载并安装 Ollama，安装完成后，按照提示使用 `Command + Space` 打开终端，运行如下命令：

```shell
# 运行安装 DeepSeek-R1-Distill-Qwen-1.5B 蒸馏模型
ollama run deepseek-r1:1.5b
```

#### Linux 安装

```bash
# 安装Ollama
curl -fsSL https://ollama.com/install.sh | sh

# 运行安装 DeepSeek-R1-Distill-Qwen-1.5B 蒸馏模型
ollama run deepseek-r1:1.5b
```

> 目前 deepseek-r1 模型大小提供了多个选择，包括 1.5b、7b、8b、14b、32b、70b、671b。
> 请根据你机器的显卡配置进行选择，这里只选择最小的 1.5b 模型来做演示。通常来说，8G 显存可以部署 8B 级别模型；24G 显存可以刚好适配到 32B 的模型。

### Spring AI 创建应用，调用本地模型

配置模型地址，在 application.yml 中配置模型的 baseUrl 与 model 名称。

```plain
spring:
  application:
    name: ollama-deepseek-chat
  ai:
    ollama:
      base-url: http://localhost:11434
      chat:
        model: qwen:14b
```

### ChatModel 和 ChatClient

ChatClient是SpringAI早期版本，采用了Fluent API的风格，可以进行链式调用，与ChatModel相比屏蔽了底层与AI交互的复杂性，到1.0.0的时候ChatModel放出，因为它更灵活，当需要接入多个模型并且有个性化配置的情况下，使用ChatModel更方便。

ChatModel类似于JDK中的核心JDBC库，ChatClient比作JdbcClient。具体差异请参考各自的实现代码。