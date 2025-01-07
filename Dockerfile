# Utilizando uma imagem base do OpenJDK para rodar aplicações em Java
FROM eclipse-temurin:17-jdk-alpine

# Define o diretório de trabalho no container
WORKDIR /app

# Copia o arquivo JAR gerado pelo build do Spring Boot para dentro do container
COPY target/orderCreation-0.0.1-SNAPSHOT.jar app.jar

# Define variáveis de ambiente para otimizar o uso de memória da JVM no container
ENV JAVA_OPTS="-Xms64m -Xmx128m"

# Comando para rodar o JAR, usando as opções extras da JVM
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]

# Expõe a porta padrão do Spring Boot (alterar se sua aplicação usar outra porta)
EXPOSE 8080