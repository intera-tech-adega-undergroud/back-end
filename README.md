# Back-end

Este projeto usa **variáveis de ambiente** para guardar dados importantes fora do código.
Se você nunca usou isso, pense assim: são valores que você define antes de rodar o sistema.

## O que você precisa

- Java 21
- Maven
- MySQL rodando localmente

## Banco de dados

O projeto tenta conectar em:

`jdbc:mysql://localhost:3306/adega_underground`

Então:

1. Crie o banco `adega_underground` no MySQL.
2. Garanta que o usuário e a senha estejam corretos.

## Variáveis de ambiente obrigatórias

Antes de iniciar o back-end, defina:

- `DB_PASSWORD` = senha do seu MySQL
- `JWT_SECRET` = uma chave secreta com **32 caracteres ou mais**
- `OCR_API_KEY` = chave da API de OCR

## Variáveis opcionais

- `DB_URL` = endereço do banco
- `DB_USERNAME` = usuário do banco
- `CORS_ALLOWED_ORIGINS` = origem liberada para o front-end
- `JPA_DDL_AUTO` = modo do Hibernate (`validate` por padrão)
- `SWAGGER_ENABLED` = `true` para ligar o Swagger
- `SWAGGER_PUBLIC` = `true` para deixar o Swagger público

## Como configurar no terminal

### macOS / Linux

```bash
export DB_PASSWORD="sua_senha"
export JWT_SECRET="uma_chave_bem_grande_com_32_ou_mais_caracteres"
export OCR_API_KEY="sua_chave_ocr"

mvn spring-boot:run
```

### Windows PowerShell

```powershell
$env:DB_PASSWORD="sua_senha"
$env:JWT_SECRET="uma_chave_bem_grande_com_32_ou_mais_caracteres"
$env:OCR_API_KEY="sua_chave_ocr"

mvn spring-boot:run
```

## Como rodar

Dentro da pasta do back-end:

```bash
mvn spring-boot:run
```

## Observação importante

O valor padrão de `JPA_DDL_AUTO` agora é `validate`, então o sistema **não cria as tabelas automaticamente**.
Se o banco estiver vazio, será preciso criar as tabelas antes de rodar.

Se for só para desenvolvimento, você pode usar:

```bash
export JPA_DDL_AUTO=update
```

## Se der erro

- Confira se o MySQL está ligado
- Confira se o banco `adega_underground` existe
- Confira se `JWT_SECRET`, `OCR_API_KEY` e `DB_PASSWORD` foram definidos
- Confira se a chave JWT tem pelo menos 32 caracteres
## Jeito mais fácil: usar o arquivo .env.example

Tem um arquivo chamado `.env.example` na pasta do back-end.
Esse arquivo é um "modelo" com todas as variáveis que você precisa.

### Passo a passo:

1. **Copie o arquivo:**

```bash
cp .env.example .env
```

2. **Abra o arquivo `.env` com seu editor de texto** e preencha com seus valores:

```
DB_PASSWORD=sua_senha_do_mysql
JWT_SECRET=uma_chave_bem_grande_com_32_ou_mais_caracteres
OCR_API_KEY=sua_chave_da_api_ocr
```

3. **Salve o arquivo `.env`**

4. **Exporte as variáveis no terminal que iniciará o Maven**, conforme os exemplos acima.
   O Spring Boot deste projeto não carrega `.env` automaticamente. O Docker Compose
   utiliza esse arquivo para o container, mas não configura o processo Java.

   Dentro de `Intera/adegaunderground/adegaunderground`, execute:

```bash
mvn spring-boot:run
```

### O que NÃO fazer:

❌ **Não** coloque o arquivo `.env` no Git/repositório
(Ele já está no `.gitignore` para proteger suas senhas)

### Como gerar uma chave JWT forte

No terminal, rode:

```bash
openssl rand -base64 32
```

Isso gera uma chave aleatória segura com 32 caracteres.
Copie o resultado para a variável `JWT_SECRET` no seu ambiente local.

## Testes no Postman

Para exemplos preenchidos e nomes mais simples, importe
`postman/Understock-facil.postman_collection.json` (**Understock - Fácil**).
Comece por **Login e Funcionários → Entrar (faça primeiro)** e preencha a senha
no Body. O token é automático. Todos os endpoints estão incluídos.
Os IDs `1` são ilustrativos: substitua pelos IDs das consultas/cadastros.
Datas opcionais ficam desmarcadas em Params; para filtrar, marque início e fim.
Em Produtos, `categoria` recebe o nome do tipo (por exemplo, `"DRINK"`),
não um objeto com ID. O tipo precisa existir no banco.
As senhas ficam em branco para preenchimento local.
Envie as requisições individualmente: executar a collection inteira também
executa cadastros, vendas, atualizações e exclusões.

### Versão no formato Swagger

Importe `postman/Understock.postman_collection.json` como uma **nova collection**.
A collection anteriormente importada pelo Swagger não é atualizada automaticamente.

As pastas seguem os controllers, como na importação do Swagger, sem bloqueios
ou scripts extras de teste.

1. Abra **funcionario-controller → Realizar login do funcionário** e preencha
   `email` e `senhaCripto` em **Body → raw → JSON**, com uma conta existente.
   Não exporte senhas nem tokens preenchidos para o Git.
2. Envie o login. O script salva `token` automaticamente na collection.
   As requisições protegidas usam **Inherit auth from parent**, herdando
   **Bearer Token `{{token}}`**. Login e cadastro de cliente usam **No Auth**.
   Evite uma variável `token` no environment ativo, pois ela sobrepõe a da collection.
3. Escolha a requisição na pasta do controller e substitua os exemplos e
   parâmetros pelos seus dados. Copie os IDs retornados pelos cadastros para
   os parâmetros das consultas, atualizações e exclusões.

**Atenção:** não há bloqueio de gravação ou exclusão na collection. Use um banco
de desenvolvimento e envie somente as requisições que deseja executar.
Produtos e clientes vinculados a vendas retornam `409` ao tentar excluí-los.
O cadastro de cliente não cria uma conta de login.

**Pré-requisitos dos dados:** consulte `/categorias` e utilize uma categoria
existente no cadastro de produto. Sem categorias, solicite a carga oficial do
banco; a API não possui rotas para criar categorias nem componentes de combos.
Não envie IDs gerados pelo banco nos cadastros. Enviar `{}` para produto ou
unidade é inválido.

`dataInicio` e `dataFim` usam `AAAA-MM-DD`. Nos fiados, o período seleciona clientes
com vendas naquele intervalo; o saldo exibido é a **dívida atual**, não um saldo
histórico reconstruído. Indicadores sem vendas retornam `204` sem corpo.
No PUT de produto, a quantidade não é alterada; use a entrada de estoque.

**Integrações:** o OCR em **nota-fiscal-controller** exige chave OCR válida
no servidor e um JPG, PNG ou PDF selecionado em **Body → form-data → arquivo (File)**.
Não configure `Content-Type` manualmente nesse upload. Alertas de estoque exigem
RabbitMQ e o consumidor configurados; uma venda confirmada não garante que o
e-mail foi enviado. Estoque negativo continua permitido.

O schema precisa corresponder às entidades: `tbl_cliente.saldo_devedor` usa
`DECIMAL(10,2)` e `tbl_produto.preco`, `DECIMAL(7,2)`. Um banco legado com `DOUBLE`
precisa da migração correspondente, com backup e revisão dos dados. Não há migração
automática nesta correção. `JPA_DDL_AUTO=none` serve apenas para diagnosticar um
ambiente legado, não comprova que o schema está correto.

## Mensageria (RabbitMQ)

O RabbitMQ roda via Docker e é compartilhado pelo back-end e pelos microsserviços
(`notification-service` e `daily-report-service`).

1. Preencha `RABBITMQ_USER` e `RABBITMQ_PASSWORD` no seu `.env` (veja `.env.example`).
2. Na raiz deste repositório, rode:

```powershell
docker compose up -d
docker compose ps   # aguarde o status (healthy)
```

3. Painel de gerenciamento: http://localhost:15672 (vhost `adega`).

| Comando | Efeito |
|---|---|
| `docker compose stop` | Para o broker e mantém os dados |
| `docker compose down` | Remove o container e mantém os dados (volume) |
| `docker compose down -v` | Remove tudo, **inclusive filas e mensagens** |

O usuário e a senha só são aplicados na primeira criação do volume. Para trocá-los, use `down -v`.
