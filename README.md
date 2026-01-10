# Chat0z 💬

> Um sistema de chat em tempo real com criptografia de ponta a ponta, desenvolvido com Spring Boot e WebSocket.

**Desenvolvido por:** [@vitinh0z](https://github.com/vitinh0z)

---

## 📋 Sobre o Projeto

Chat0z é uma aplicação de mensagens instantâneas que permite aos usuários criar salas de chat privadas e se comunicar em tempo real com segurança. Todas as mensagens são criptografadas usando AES-256, garantindo que apenas membros da sala possam ler o conteúdo das conversas.

### ✨ Funcionalidades Principais

- 🔐 **Autenticação OAuth2** com Google
- 💬 **Chat em tempo real** usando WebSocket (STOMP)
- 🔒 **Criptografia AES-256** de mensagens
- 🚪 **Sistema de salas** com códigos de convite únicos
- 👥 **Sistema de permissões** (Dono, Admin, Membro)
- 📝 **Gerenciamento de mensagens** (enviar, deletar)
- 👤 **Perfis de usuário** com status (ONLINE, OFFLINE, BUSY)
- 🎯 **Sistema de membros** para controle de acesso às salas

---

## 🛠️ Tecnologias Utilizadas

### Backend
- **Java 21** - Linguagem de programação
- **Spring Boot 4.0.1** - Framework principal
- **Spring Security** - Autenticação e autorização
- **Spring WebSocket** - Comunicação em tempo real
- **Spring Data JPA** - Persistência de dados
- **Lombok** - Redução de código boilerplate

### Banco de Dados
- **H2 Database** - Banco em memória (desenvolvimento)
- **PostgreSQL** - Banco de dados (produção - suportado)

### Segurança
- **OAuth2 Client** - Autenticação com Google
- **AES/CBC/PKCS5Padding** - Criptografia de mensagens
- **SHA-256** - Geração de chaves a partir dos códigos de convite

### Build & Testes
- **Maven** - Gerenciamento de dependências
- **JUnit** - Testes unitários

---

## 📦 Pré-requisitos

Antes de começar, certifique-se de ter instalado:

- **Java JDK 21** ou superior
- **Maven 3.8+** (ou use o wrapper incluído: `./mvnw`)
- **Git** para clonar o repositório

Para autenticação OAuth2 com Google, você precisará:
- Criar um projeto no [Google Cloud Console](https://console.cloud.google.com/)
- Configurar credenciais OAuth2
- Obter `Client ID` e `Client Secret`

---

## 🚀 Instalação e Configuração

### 1. Clonar o Repositório

```bash
git clone https://github.com/vitinh0z/Chat0z.git
cd Chat0z
```

### 2. Configurar Variáveis de Ambiente

Crie as seguintes variáveis de ambiente ou edite `application.properties`:

```bash
export GOOGLE_CLIENT_ID=seu-client-id-aqui
export GOOGLE_CLIENT_SECRET=seu-client-secret-aqui
```

**Ou edite `src/main/resources/application.properties`:**

```properties
spring.security.oauth2.client.registration.google.client-id=SEU_CLIENT_ID
spring.security.oauth2.client.registration.google.client-secret=SEU_CLIENT_SECRET
```

### 3. Compilar o Projeto

```bash
./mvnw clean install
```

Ou no Windows:

```cmd
mvnw.cmd clean install
```

### 4. Executar a Aplicação

```bash
./mvnw spring-boot:run
```

A aplicação estará disponível em: `http://localhost:8080`

---

## 🏗️ Estrutura do Projeto

```
Chat0z/
├── src/
│   ├── main/
│   │   ├── java/io/github/vitinh0z/chat/
│   │   │   ├── config/           # Configurações (WebSocket, Security)
│   │   │   ├── controller/       # Controllers REST e WebSocket
│   │   │   │   ├── chat/         # ChatController (WebSocket)
│   │   │   │   ├── message/      # MessageController
│   │   │   │   ├── room/         # RoomController
│   │   │   │   └── user/         # UserController
│   │   │   ├── dto/              # Data Transfer Objects
│   │   │   ├── entities/         # Entidades JPA
│   │   │   │   ├── membership/   # Membership (relacionamento User-Room)
│   │   │   │   ├── message/      # Message
│   │   │   │   ├── room/         # Room
│   │   │   │   └── user/         # User
│   │   │   ├── enums/            # Enumerações (UserStatus, RoomRole)
│   │   │   ├── repository/       # Repositórios JPA
│   │   │   ├── service/          # Lógica de negócio
│   │   │   └── utils/            # Utilitários (CryptoUtils)
│   │   └── resources/
│   │       └── application.properties
│   └── test/                     # Testes unitários
├── pom.xml                       # Configuração Maven
└── README.md
```

---

## 🔌 API e Endpoints

### Endpoints REST

#### 👤 Usuários (`/user`)

| Método | Endpoint | Descrição | Autenticação |
|--------|----------|-----------|--------------|
| GET | `/user/me` | Retorna dados do usuário autenticado | Obrigatória |
| PUT | `/user/update` | Atualiza informações do usuário | Obrigatória |
| PUT | `/user/status` | Atualiza status do usuário | Obrigatória |

#### 🚪 Salas (`/room`)

| Método | Endpoint | Descrição | Autenticação |
|--------|----------|-----------|--------------|
| GET | `/room/me` | Lista todas as salas do usuário | Obrigatória |
| POST | `/room/create` | Cria uma nova sala | Obrigatória |
| POST | `/room/enter` | Entra em uma sala com código de convite | Obrigatória |

**Exemplo de criação de sala:**
```json
POST /room/create
{
  "id": 1,
  "name": "Sala dos Devs",
  "invite": "codigo-secreto-123"
}
```

**Exemplo de entrada em sala:**
```json
POST /room/enter
{
  "inviteCode": "codigo-secreto-123"
}
```

#### 💬 Mensagens (`/message`)

| Método | Endpoint | Descrição | Autenticação |
|--------|----------|-----------|--------------|
| GET | `/message/all/{roomId}` | Busca todas as mensagens de uma sala | Obrigatória |

---

## 🌐 WebSocket

A aplicação usa **STOMP over WebSocket** para comunicação em tempo real.

### Conectar ao WebSocket

```javascript
const socket = new SockJS('http://localhost:8080/connect');
const stompClient = Stomp.over(socket);

stompClient.connect({}, function(frame) {
    console.log('Conectado: ' + frame);
    
    // Inscrever-se em uma sala
    stompClient.subscribe('/topic/room/1', function(message) {
        const msg = JSON.parse(message.body);
        console.log('Nova mensagem:', msg);
    });
});
```

### Enviar Mensagem

```javascript
stompClient.send("/app/chat/1", {}, JSON.stringify({
    'userId': 1,
    'content': 'Olá, mundo!'
}));
```

### Deletar Mensagem

```javascript
stompClient.send("/app/chat/1/delete", {}, JSON.stringify({
    'userId': 1,
    'messageId': 123
}));
```

### Endpoints WebSocket

| Destino | Descrição | Subscrição |
|---------|-----------|------------|
| `/app/chat/{roomId}` | Enviar mensagem para uma sala | `/topic/room/{roomId}` |
| `/app/chat/{roomId}/delete` | Deletar mensagem de uma sala | `/topic/room/{roomId}` |

---

## 🔐 Segurança e Criptografia

### Autenticação

O Chat0z usa **OAuth2** com Google para autenticação. O fluxo é:

1. Usuário clica em "Login com Google"
2. Redirecionado para autorização do Google
3. Após aprovação, retorna com token OAuth2
4. Sistema cria/atualiza o usuário no banco
5. Sessão autenticada é criada

### Criptografia de Mensagens

Todas as mensagens são criptografadas usando **AES-256 em modo CBC**:

1. **Chave de criptografia:** Gerada a partir do código de convite da sala usando SHA-256
2. **Vetor de inicialização (IV):** Gerado aleatoriamente para cada mensagem
3. **Formato:** `Base64(IV + Mensagem Criptografada)`

**Características:**
- Cada sala tem sua própria chave (derivada do código de convite)
- Apenas membros da sala podem descriptografar as mensagens
- IV aleatório garante que mensagens idênticas tenham criptografias diferentes

```java
// Exemplo de uso (já implementado no MessageService)
String encrypted = CryptoUtils.encrypt("Olá!", "codigo-convite");
String decrypted = CryptoUtils.decrypt(encrypted, "codigo-convite");
```

### Sistema de Permissões

Existem 3 níveis de permissão nas salas:

| Papel | Permissões |
|-------|-----------|
| **OWNER** | Pode deletar qualquer mensagem, gerenciar admins, deletar a sala |
| **ADMIN** | Pode deletar qualquer mensagem, gerenciar membros |
| **MEMBER** | Pode apenas deletar suas próprias mensagens |

---

## 🗄️ Modelo de Dados

### Entidades Principais

#### User (Usuário)
```java
- id: Long
- nickname: String
- email: String (único)
- picProfile: String
- userStatus: UserStatus (ONLINE, OFFLINE, BUSY)
- roomsCreated: List<Room>
- participating: List<Membership>
```

#### Room (Sala)
```java
- id: Long
- roomName: String
- password: String
- inviteCode: String (único)
- owner: User
- memberships: List<Membership>
- messages: List<Message>
```

#### Message (Mensagem)
```java
- id: Long
- content: String (criptografado)
- user: User
- room: Room
- timestemp: LocalDateTime
```

#### Membership (Membro)
```java
- id: Long
- user: User
- room: Room
- roomRole: RoomRole (OWNER, ADMIN, MEMBER)
```

---

## 🧪 Executar Testes

```bash
./mvnw test
```

Para executar um teste específico:

```bash
./mvnw test -Dtest=MessageServiceTest
```

Para ver cobertura de testes:

```bash
./mvnw verify
```

---

## 🔧 Configuração Avançada

### Usar PostgreSQL em vez de H2

Edite `application.properties`:

```properties
# Comentar/remover configurações do H2
# spring.datasource.url=jdbc:h2:mem:testdb

# Adicionar configurações do PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/chatoz
spring.datasource.username=seu-usuario
spring.datasource.password=sua-senha
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
```

### Customizar Porta da Aplicação

```properties
server.port=3000
```

### Habilitar Logs de Debug

```properties
logging.level.io.github.vitinh0z.chat=DEBUG
logging.level.org.springframework.security=DEBUG
logging.level.org.springframework.messaging=DEBUG
```

---

## 📱 Exemplo de Uso

### 1. Criar uma Conta
- Acesse `http://localhost:8080`
- Clique em "Login com Google"
- Autorize a aplicação

### 2. Criar uma Sala
```bash
POST /room/create
{
  "id": 1,
  "name": "Minha Sala Privada",
  "invite": "sala-secreta-xyz"
}
```

### 3. Compartilhar o Código de Convite
- Compartilhe o código `sala-secreta-xyz` com seus amigos

### 4. Amigos Entram na Sala
```bash
POST /room/enter
{
  "inviteCode": "sala-secreta-xyz"
}
```

### 5. Conectar via WebSocket e Conversar
```javascript
// Conectar
const socket = new SockJS('http://localhost:8080/connect');
const stompClient = Stomp.over(socket);

// Inscrever-se na sala
stompClient.subscribe('/topic/room/1', function(message) {
    console.log('Mensagem recebida:', JSON.parse(message.body));
});

// Enviar mensagem
stompClient.send("/app/chat/1", {}, JSON.stringify({
    'userId': 1,
    'content': 'Olá, pessoal!'
}));
```

---

## 🐛 Troubleshooting

### Erro: "GOOGLE_CLIENT_ID not found"
**Solução:** Configure as variáveis de ambiente ou edite `application.properties` com suas credenciais OAuth2.

### Erro: "Port 8080 already in use"
**Solução:** Altere a porta em `application.properties`:
```properties
server.port=8081
```

### Mensagens aparecem como "[Mensagem ilegível]"
**Solução:** Isso significa que a chave de descriptografia está incorreta. Verifique se você está usando o mesmo código de convite que foi usado para criar a sala.

### Erro ao conectar WebSocket
**Solução:** Certifique-se de que:
- A aplicação está rodando
- Você está usando o endpoint correto: `/connect`
- O CORS está configurado corretamente se estiver acessando de outro domínio

---

## 🗺️ Roadmap

Funcionalidades planejadas para versões futuras:

- [ ] Interface web completa (frontend)
- [ ] Suporte para arquivos e imagens
- [ ] Mensagens de áudio
- [ ] Notificações em tempo real
- [ ] Indicador de "digitando..."
- [ ] Reações a mensagens
- [ ] Responder mensagens (threads)
- [ ] Busca de mensagens
- [ ] Temas claro/escuro
- [ ] Aplicativo mobile

---

## 🤝 Contribuindo

Contribuições são bem-vindas! Sinta-se livre para:

1. Fazer fork do projeto
2. Criar uma branch para sua feature (`git checkout -b feature/MinhaFeature`)
3. Commit suas mudanças (`git commit -m 'Adiciona MinhaFeature'`)
4. Push para a branch (`git push origin feature/MinhaFeature`)
5. Abrir um Pull Request

**Por favor, mantenha o código limpo e siga as convenções do projeto.**

---

## 📄 Licença

Este projeto é de código aberto e está disponível para uso livre.

**Importante:** Se você usar este código ou partes dele em seus projetos, por favor **mantenha os créditos** ao desenvolvedor original.

---

## 👨‍💻 Créditos

**Desenvolvedor:** Victor Hugo ([@vitinh0z](https://github.com/vitinh0z))

Este projeto foi desenvolvido como um sistema de chat completo e seguro, utilizando as melhores práticas de desenvolvimento Spring Boot.

---

## 📞 Contato

Para dúvidas, sugestões ou reportar problemas:

- **GitHub:** [@vitinh0z](https://github.com/vitinh0z)
- **Issues:** [Reportar um problema](https://github.com/vitinh0z/Chat0z/issues)

---

## ⭐ Mostre seu Apoio

Se este projeto foi útil para você, considere dar uma ⭐ no repositório!

---

**Desenvolvido com ❤️ por [@vitinh0z](https://github.com/vitinh0z)**
