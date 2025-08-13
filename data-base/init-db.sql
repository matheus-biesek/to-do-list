-- =====================================================
-- CONFIGURAÇÃO INICIAL DO BANCO DE DADOS - TO-DO LIST
-- =====================================================

-- Habilita a extensão UUID para geração de identificadores únicos
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Cria o schema da aplicação para organizar as tabelas
CREATE SCHEMA IF NOT EXISTS app;

-- =====================================================
-- TABELAS PRINCIPAIS
-- =====================================================

-- Tabela de usuários do sistema
CREATE TABLE app.usuarios (
    usuario_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    nome_usuario VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    senha_hash VARCHAR(255) NOT NULL,
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabela principal de tarefas
CREATE TABLE app.tarefas (
    tarefa_id BIGSERIAL PRIMARY KEY,
    usuario_id UUID NOT NULL REFERENCES app.usuarios(usuario_id) ON DELETE CASCADE,
    titulo VARCHAR(255) NOT NULL,
    descricao TEXT,
    data_vencimento DATE,
    status VARCHAR(20) NOT NULL,
    prioridade VARCHAR(10) NOT NULL,
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabela de subtarefas (tarefas filhas)
CREATE TABLE app.subtarefas (
    subtarefa_id BIGSERIAL PRIMARY KEY,
    tarefa_id BIGINT NOT NULL REFERENCES app.tarefas(tarefa_id) ON DELETE CASCADE,
    titulo VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL,
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Remove tabela de anexos se existir (para recriação)
DROP TABLE IF EXISTS app.anexos;

-- Tabela de anexos/arquivos das tarefas
CREATE TABLE app.anexos (
    anexo_id BIGSERIAL PRIMARY KEY,
    tarefa_id BIGINT NOT NULL REFERENCES app.tarefas(tarefa_id) ON DELETE CASCADE,
    nome_original VARCHAR(255) NOT NULL,
    nome_arquivo VARCHAR(255) NOT NULL,
    tipo_mime VARCHAR(100) NOT NULL,
    tamanho BIGINT NOT NULL,
    caminho_arquivo VARCHAR(500) NOT NULL,
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- ÍNDICES PARA OTIMIZAÇÃO DE PERFORMANCE
-- =====================================================

-- Índices para consultas frequentes por status, prioridade e data de vencimento
CREATE INDEX idx_tarefas_status ON app.tarefas(status);
CREATE INDEX idx_tarefas_prioridade ON app.tarefas(prioridade);
CREATE INDEX idx_tarefas_data_vencimento ON app.tarefas(data_vencimento);

-- =====================================================
-- FUNÇÕES E TRIGGERS PARA AUTOMAÇÃO
-- =====================================================

-- Função para atualizar automaticamente o campo 'atualizado_em'
CREATE OR REPLACE FUNCTION app.atualiza_data_atualizacao()
RETURNS TRIGGER AS $$
BEGIN
    NEW.atualizado_em = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Triggers para atualizar automaticamente a data de modificação
CREATE TRIGGER atualiza_data_atualizacao_tarefas
BEFORE UPDATE ON app.tarefas
FOR EACH ROW
EXECUTE FUNCTION app.atualiza_data_atualizacao();

CREATE TRIGGER atualiza_data_atualizacao_subtarefas
BEFORE UPDATE ON app.subtarefas
FOR EACH ROW
EXECUTE FUNCTION app.atualiza_data_atualizacao();

-- =====================================================
-- CONFIGURAÇÃO DE PERMISSÕES
-- =====================================================

-- Define o proprietário das tabelas e sequências para o role orchestrator
ALTER TABLE app.usuarios OWNER TO app_orchestrator;
ALTER TABLE app.tarefas OWNER TO app_orchestrator;
ALTER TABLE app.subtarefas OWNER TO app_orchestrator;
ALTER TABLE app.anexos OWNER TO app_orchestrator;
ALTER SEQUENCE app.tarefas_tarefa_id_seq OWNER TO app_orchestrator;
ALTER SEQUENCE app.subtarefas_subtarefa_id_seq OWNER TO app_orchestrator;
ALTER SEQUENCE app.anexos_anexo_id_seq OWNER TO app_orchestrator;
