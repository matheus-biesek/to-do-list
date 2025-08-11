CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE SCHEMA IF NOT EXISTS app;

CREATE TABLE app.usuarios (
    usuario_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    nome_usuario VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    senha_hash VARCHAR(255) NOT NULL,
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE app.tarefas (
    tarefa_id SERIAL PRIMARY KEY,
    usuario_id UUID NOT NULL REFERENCES app.usuarios(usuario_id) ON DELETE CASCADE,
    titulo VARCHAR(255) NOT NULL,
    descricao TEXT,
    data_vencimento DATE,
    status VARCHAR(20) NOT NULL,
    prioridade VARCHAR(10) NOT NULL,
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE app.subtarefas (
    subtarefa_id SERIAL PRIMARY KEY,
    tarefa_id INT NOT NULL REFERENCES app.tarefas(tarefa_id) ON DELETE CASCADE,
    titulo VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL,
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_tarefas_status ON app.tarefas(status);
CREATE INDEX idx_tarefas_prioridade ON app.tarefas(prioridade);
CREATE INDEX idx_tarefas_data_vencimento ON app.tarefas(data_vencimento);

CREATE OR REPLACE FUNCTION app.atualiza_data_atualizacao()
RETURNS TRIGGER AS $$
BEGIN
    NEW.atualizado_em = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER atualiza_data_atualizacao_tarefas
BEFORE UPDATE ON app.tarefas
FOR EACH ROW
EXECUTE FUNCTION app.atualiza_data_atualizacao();

CREATE TRIGGER atualiza_data_atualizacao_subtarefas
BEFORE UPDATE ON app.subtarefas
FOR EACH ROW
EXECUTE FUNCTION app.atualiza_data_atualizacao();

GRANT SELECT, INSERT, UPDATE, DELETE ON app.usuarios TO app_orchestrator;
GRANT SELECT, INSERT, UPDATE, DELETE ON app.tarefas TO app_orchestrator;
GRANT SELECT, INSERT, UPDATE, DELETE ON app.subtarefas TO app_orchestrator;
