# 📋 To-Do List API

Uma API RESTful completa para gerenciamento de tarefas desenvolvida em Java 17 com Spring Boot 3.5.4, oferecendo funcionalidades avançadas como autenticação JWT, upload de anexos, subtarefas e sistema de prioridades.

## 🚀 Tecnologias Utilizadas

- **Java 17**
- **Spring Boot 3.5.4**
- **Spring Security** (Autenticação JWT)
- **Spring Data JPA** (Persistência)
- **Spring Validation** (Validação de dados)
- **PostgreSQL 16** (Banco de dados)
- **Lombok** (Redução de boilerplate)
- **SpringDoc OpenAPI** (Documentação Swagger)
- **Docker & Docker Compose** (Containerização)
- **JUnit & Mockito** (testes)

## 📁 Estrutura do Projeto

```
to-do-list/
├── data-base/                    # Configurações do banco de dados
│   ├── init-db.sql              # Schema e tabelas
│   ├── init-sgbd.sql            # Configurações de usuários e roles
│   └── postgres-data/           # Dados persistentes (criado automaticamente)
├── spring-todo/                 # Aplicação Spring Boot
│   ├── src/main/java/
│   │   └── com/matheusbiesek/todolist/spring_todo/
│   │       ├── config/          # Configurações (CORS, Security, OpenAPI)
│   │       ├── controller/      # Controllers REST
│   │       ├── dto/             # Data Transfer Objects
│   │       ├── entity/          # Entidades JPA
│   │       ├── enums/           # Enums do sistema
│   │       ├── exception/       # Tratamento de exceções
│   │       ├── mapper/          # Conversores DTO ↔ Entity
│   │       ├── repository/      # Repositórios JPA
│   │       ├── security/        # Configurações de segurança
│   │       └── service/         # Lógica de negócio
│   └── src/main/resources/
│       └── application.properties
├── anexos/                      # Diretório para upload de arquivos
├── docker-compose.yml           # Orquestração dos containers
└── README.md
```

## 🛠️ Funcionalidades

### 🔐 Autenticação e Autorização
- Registro de usuários
- Login com JWT (HttpOnly Cookie)
- Logout seguro
- Controle de acesso por usuário

### 📝 Gerenciamento de Tarefas
- **CRUD completo** de tarefas
- **Filtros avançados**: status, prioridade, data de vencimento
- **Paginação** e ordenação
- **Sistema de prioridades**: BAIXA, MEDIA, ALTA
- **Status de tarefas**: PENDENTE, EM_PROGRESSO, CONCLUIDA, CANCELADA

### 📋 Subtarefas
- Criação de subtarefas vinculadas a tarefas
- Gerenciamento independente de subtarefas
- Filtros por status
- Validações de regras de negócio

### 📎 Sistema de Anexos
- **Upload de arquivos** (máximo 10MB)
- **Download de anexos**
- **Listagem** de anexos por tarefa
- **Exclusão** de anexos (arquivo + banco)
- **Validações** de tipo e tamanho
- **Nomes únicos** para evitar conflitos

### 📊 Recursos Adicionais
- **Documentação Swagger/OpenAPI** completa
- **Tratamento de exceções** personalizado
- **Validações** robustas
- **Logs** estruturados
- **Timezone** configurado para Brasil

### 📖 Documentação Swagger
O projeto possui **documentação automática** da API através do **SpringDoc OpenAPI (Swagger)**:

- **URL de acesso**: http://localhost:8080/swagger-ui.html
- **Documentação JSON**: http://localhost:8080/v3/api-docs
- **Especificação YAML**: http://localhost:8080/v3/api-docs.yaml

**Recursos do Swagger:**
- ✨ **Interface interativa** para testar endpoints
- 📝 **Documentação automática** de todos os endpoints
- 🔍 **Exemplos de requisição** e resposta
- 🛡️ **Autenticação JWT** integrada
- 📋 **Schemas** de todos os DTOs
- 🚀 **Teste direto** dos endpoints da API

## 🚀 Como Executar

### Pré-requisitos
- Docker e Docker Compose instalados
- Java 17 (para desenvolvimento local)
- Maven (para desenvolvimento local)

### 1. Clone o repositório
```bash
git clone <url-do-repositorio>
cd to-do-list
```

### 2. Crie o diretório para anexos
```bash
mkdir anexos
```

### 3. Execute com Docker Compose
```bash
docker-compose up -d
```

### 4. Acesse os serviços
- **API**: http://localhost:8080
- **Documentação Swagger**: http://localhost:8080/swagger-ui.html
- **PgAdmin**: http://localhost:5050
  - Email: `admin@admin.com`
  - Senha: `admin`

### 5. Teste a API com Swagger
1. Acesse http://localhost:8080/swagger-ui.html
2. **Faça login** primeiro usando o endpoint `/api/auth/login`
3. **Copie o token JWT** do cookie retornado
4. **Clique no botão "Authorize"** no Swagger
5. **Cole o token** no campo de autorização
6. **Teste todos os endpoints** diretamente pela interface

**💡 Dica**: O Swagger permite testar toda a API sem precisar de ferramentas externas como Postman ou Insomnia!

### 6. Configure o banco no PgAdmin
1. Acesse http://localhost:5050
2. Faça login com as credenciais acima
3. Adicione novo servidor:
   - **Host**: `postgres_sgbd`
   - **Port**: `5432`
   - **Database**: `to_do_list_db`
   - **Username**: `to_do_list_app`
   - **Password**: `1234`

## 🔧 Configurações

### Variáveis de Ambiente
As configurações principais estão em `spring-todo/src/main/resources/application.properties`:

```properties
# Banco de dados
spring.datasource.url=jdbc:postgresql://localhost:5432/to_do_list_db
spring.datasource.username=to_do_list_app
spring.datasource.password=1234

# JWT
app.jwt.secret=MySuperSecretKeyForJWTTokenGenerationThatIsVeryLongAndSecure123456789

# Upload de arquivos
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
app.anexos.diretorio=/home/biesek/projetos/to-do-list/anexos
```

### Docker Compose
O `docker-compose.yml` configura:
- **PostgreSQL 16** na porta 5432
- **PgAdmin** na porta 5050
- **Volumes persistentes** para dados
- **Rede isolada** para comunicação entre serviços

## 📚 Documentação da API

### Endpoints Principais

#### 🔐 Autenticação
- `POST /api/auth/register` - Registrar usuário
- `POST /api/auth/login` - Fazer login
- `POST /api/auth/logout` - Fazer logout

#### 📝 Tarefas
- `GET /api/tarefas` - Listar tarefas (com filtros e paginação)
- `GET /api/tarefas/{id}` - Buscar tarefa por ID
- `POST /api/tarefas` - Criar nova tarefa
- `PUT /api/tarefas/{id}` - Atualizar tarefa
- `DELETE /api/tarefas/{id}` - Deletar tarefa
- `PATCH /api/tarefas/{id}/status` - Atualizar status

#### 📋 Subtarefas
- `GET /api/subtarefas/tarefa/{tarefaId}` - Listar subtarefas de uma tarefa
- `GET /api/subtarefas/{id}` - Buscar subtarefa por ID
- `POST /api/subtarefas/tarefa/{tarefaId}` - Criar subtarefa
- `PUT /api/subtarefas/{id}` - Atualizar subtarefa
- `DELETE /api/subtarefas/{id}` - Deletar subtarefa
- `PATCH /api/subtarefas/{id}/status` - Atualizar status

#### 📎 Anexos
- `GET /api/tarefas/{tarefaId}/anexos` - Listar anexos da tarefa
- `POST /api/tarefas/{tarefaId}/anexos` - Upload de anexo
- `GET /api/tarefas/{tarefaId}/anexos/{anexoId}/download` - Download de anexo
- `DELETE /api/tarefas/{tarefaId}/anexos/{anexoId}` - Deletar anexo

### Exemplos de Uso

#### Criar uma tarefa
```bash
curl -X POST http://localhost:8080/api/tarefas \
  -H "Content-Type: application/json" \
  -H "Cookie: access-token=seu-jwt-token" \
  -d '{
    "titulo": "Implementar API REST",
    "descricao": "Criar endpoints para CRUD de tarefas",
    "dataVencimento": "2024-12-31",
    "prioridade": "ALTA",
    "status": "PENDENTE"
  }'
```

#### Upload de anexo
```bash
curl -X POST http://localhost:8080/api/tarefas/1/anexos \
  -H "Cookie: access-token=seu-jwt-token" \
  -F "arquivo=@documento.pdf"
```

## 🏗️ Arquitetura

### Padrões Utilizados
- **Service Layer Pattern** - Separação de responsabilidades
- **Repository Pattern** - Abstração da camada de dados
- **DTO Pattern** - Transferência de dados
- **Mapper Pattern** - Conversão entre objetos
- **Exception Handler** - Tratamento centralizado de erros

### Princípios SOLID
- **Single Responsibility** - Cada classe tem uma responsabilidade
- **Open/Closed** - Extensível sem modificação
- **Liskov Substitution** - Substituição de implementações
- **Interface Segregation** - Interfaces específicas
- **Dependency Inversion** - Inversão de dependências

### Clean Code
- Nomes descritivos
- Funções pequenas e focadas
- Comentários apenas quando necessário
- Código auto-documentado
- **Testes unitários** implementados para services

## 🔒 Segurança

- **JWT** com HttpOnly Cookies
- **BCrypt** para hash de senhas
- **CORS** configurado
- **Validação** de entrada de dados
- **Controle de acesso** por usuário
- **Sanitização** de arquivos uploadados

## 📈 Performance

- **Índices** otimizados no banco
- **Paginação** para grandes volumes
- **Lazy Loading** em relacionamentos
- **Connection Pool** configurado
- **Cache** preparado para implementação

## 🧪 Testes

O projeto possui testes unitários implementados para os services principais:

### Testes Implementados
- **TarefaServiceTest** - Testes para `TarefaService`
  - Teste de criação de tarefa
  - Teste de busca de tarefas por usuário
  - Teste de validação de conclusão de tarefa com subtarefas pendentes
  
- **SubtarefaServiceTest** - Testes para `SubtarefaService`
  - Teste de criação de subtarefa
  - Teste de busca de subtarefas por tarefa
  - Teste de atualização de status de subtarefa

### Tecnologias de Teste
- **Spring Boot Test**
- **JUnit 5**
- **Mockito** para mocks
- **AssertJ** para assertions

## 🤝 Contribuição

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

## 👨‍💻 Autor

**Matheus Biesek**
- GitHub: [@matheusbiesek](https://github.com/matheus-biesek)

## 🙏 Agradecimentos

- Spring Boot Team
- PostgreSQL Community
- Docker Team
- Todos os contribuidores do projeto

---

⭐ Se este projeto te ajudou, considere dar uma estrela no repositório! 
