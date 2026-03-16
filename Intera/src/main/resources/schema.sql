-- ============================
-- USUARIO
-- ============================
CREATE TABLE IF NOT EXISTS tbl_usuario (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    cpf VARCHAR(20) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    email VARCHAR(100),
    celular VARCHAR(45)
);

-- ============================
-- FUNCIONARIO
-- ============================
CREATE TABLE IF NOT EXISTS tbl_funcionario (
    id_funcionario INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL,
    nivel_acesso INT,
    FOREIGN KEY (id_usuario) REFERENCES tbl_usuario(id_usuario)
);

-- ============================
-- CLIENTE
-- ============================
CREATE TABLE IF NOT EXISTS tbl_cliente (
    id_cliente INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT,
    compra_fiado TINYINT DEFAULT 0,
    FOREIGN KEY (id_usuario) REFERENCES tbl_usuario(id_usuario)
);

-- ============================
-- ITEM GENERICO
-- ============================
CREATE TABLE IF NOT EXISTS tbl_item (
    id_item INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    ativo TINYINT DEFAULT 1
);

-- ============================
-- PRODUTO
-- ============================
CREATE TABLE IF NOT EXISTS tbl_produto (
    id_produto INT AUTO_INCREMENT PRIMARY KEY,
    id_item INT NOT NULL,
    categoria VARCHAR(50),
    FOREIGN KEY (id_item) REFERENCES tbl_item(id_item)
);

-- ============================
-- BEBIDA
-- ============================
CREATE TABLE IF NOT EXISTS tbl_bebida (
    id_bebida INT AUTO_INCREMENT PRIMARY KEY,
    id_item INT NOT NULL,
    tipo_embalagem ENUM('garrafa','lata','longneck','pet'),
    volume_total_ml INT,
    FOREIGN KEY (id_item) REFERENCES tbl_item(id_item)
);

-- ============================
-- COMBO
-- ============================
CREATE TABLE IF NOT EXISTS tbl_combo (
    id_combo INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100),
    preco DECIMAL(10,2),
    ativo TINYINT DEFAULT 1
);

-- ============================
-- COMBO ITENS
-- ============================
CREATE TABLE IF NOT EXISTS tbl_combo_item (
    id_combo INT,
    id_item INT,
    quantidade INT,
    PRIMARY KEY(id_combo,id_item),
    FOREIGN KEY (id_combo) REFERENCES tbl_combo(id_combo),
    FOREIGN KEY (id_item) REFERENCES tbl_item(id_item)
);

-- ============================
-- FORMAS DE VENDA
-- ============================
CREATE TABLE IF NOT EXISTS tbl_forma_venda (
    id_forma_venda INT AUTO_INCREMENT PRIMARY KEY,
    id_item INT NOT NULL,
    tipo ENUM('unidade','dose','copo','garrafa','lata','fardo'),
    volume_ml INT,
    preco DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (id_item) REFERENCES tbl_item(id_item)
);

-- ============================
-- EVENTO DE RECEBIMENTO
-- ============================
CREATE TABLE IF NOT EXISTS evt_recebimento (
    id_recebimento INT AUTO_INCREMENT PRIMARY KEY,
    id_funcionario INT NOT NULL,
    data_hora TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_funcionario) REFERENCES tbl_funcionario(id_funcionario)
);

-- ============================
-- ITENS RECEBIDOS
-- ============================
CREATE TABLE IF NOT EXISTS item_recebimento (
    id_recebimento INT,
    id_item INT,
    quantidade INT,
    PRIMARY KEY(id_recebimento,id_item),
    FOREIGN KEY (id_recebimento) REFERENCES evt_recebimento(id_recebimento),
    FOREIGN KEY (id_item) REFERENCES tbl_item(id_item)
);

-- ============================
-- ESTOQUE UNIDADE (controle ML)
-- ============================
CREATE TABLE IF NOT EXISTS stock_unit (
    id_unit BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_bebida INT NOT NULL,
    lote VARCHAR(100),
    initial_volume_ml INT NOT NULL,
    remaining_volume_ml INT NOT NULL,
    status ENUM('available','open','empty','reserved') DEFAULT 'available',
    id_recebimento INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_bebida) REFERENCES tbl_bebida(id_bebida),
    FOREIGN KEY (id_recebimento) REFERENCES evt_recebimento(id_recebimento)
);

-- ============================
-- EVENTO DE VENDA
-- ============================
CREATE TABLE IF NOT EXISTS evt_venda (
    id_venda INT AUTO_INCREMENT PRIMARY KEY,
    id_cliente INT,
    id_funcionario INT,
    data_hora TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    forma_pagamento VARCHAR(45),
    status VARCHAR(45),
    FOREIGN KEY (id_cliente) REFERENCES tbl_cliente(id_cliente),
    FOREIGN KEY (id_funcionario) REFERENCES tbl_funcionario(id_funcionario)
);

-- ============================
-- ITENS DA VENDA
-- ============================
CREATE TABLE IF NOT EXISTS item_venda (
    id_venda INT,
    id_item INT,
    id_forma_venda INT,
    quantidade INT,
    preco_unitario DECIMAL(10,2),
    PRIMARY KEY(id_venda,id_item,id_forma_venda),
    FOREIGN KEY (id_venda) REFERENCES evt_venda(id_venda),
    FOREIGN KEY (id_item) REFERENCES tbl_item(id_item),
    FOREIGN KEY (id_forma_venda) REFERENCES tbl_forma_venda(id_forma_venda)
);

-- ============================
-- EVENTO DE PERDA
-- ============================
CREATE TABLE IF NOT EXISTS evt_perda (
    id_perda INT AUTO_INCREMENT PRIMARY KEY,
    id_funcionario INT,
    motivo VARCHAR(255),
    data_hora TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_funcionario) REFERENCES tbl_funcionario(id_funcionario)
);

-- ============================
-- ITENS PERDIDOS
-- ============================
CREATE TABLE IF NOT EXISTS item_perda (
    id_perda INT,
    id_item INT,
    quantidade INT,
    volume_ml INT,
    PRIMARY KEY(id_perda,id_item),
    FOREIGN KEY (id_perda) REFERENCES evt_perda(id_perda),
    FOREIGN KEY (id_item) REFERENCES tbl_item(id_item)
);

-- ============================
-- MOVIMENTAÇÃO DE ESTOQUE
-- ============================
CREATE TABLE IF NOT EXISTS evt_movimentacao_estoque (
    id_movimentacao INT AUTO_INCREMENT PRIMARY KEY,
    tipo ENUM('entrada','saida','ajuste','perda','venda'),
    id_item INT,
    quantidade INT,
    origem ENUM('recebimento','venda','perda','ajuste'),
    id_origem INT,
    data_hora TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_item) REFERENCES tbl_item(id_item)
);