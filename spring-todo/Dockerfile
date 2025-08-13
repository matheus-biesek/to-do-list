# Multi-stage build
FROM eclipse-temurin:17-jdk-alpine AS build

WORKDIR /app

# Copiar arquivos de configuração do Maven
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Dar permissão de execução ao mvnw
RUN chmod +x mvnw

# Baixar dependências (melhor cache de layer)
RUN ./mvnw dependency:go-offline -B

# Copiar código fonte
COPY src ./src

# Compilar a aplicação
RUN ./mvnw clean package -DskipTests -B

# Stage final
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Criar usuário não-root para segurança com UID/GID específicos
RUN addgroup -g 1000 -S spring && adduser -u 1000 -S spring -G spring

# Criar diretório para anexos com permissões corretas
RUN mkdir -p /app/anexos && chown -R spring:spring /app/anexos && chmod -R 755 /app/anexos

# Copiar o JAR da etapa de build
COPY --from=build /app/target/spring-todo-*.jar app.jar

# Mudar ownership do arquivo JAR
RUN chown spring:spring app.jar

# Mudar para usuário não-root
USER spring

# Expor porta da aplicação
EXPOSE 8080

# Configurar JVM para containers
ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

# Comando para executar a aplicação com perfil docker
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dspring.profiles.active=docker -jar app.jar"]