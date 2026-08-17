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

4. **Agora quando você rodar o Maven, ele vai ler essas variáveis automaticamente:**

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
Copie o resultado e cole em `JWT_SECRET=` no arquivo `.env`.

