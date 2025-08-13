-- =====================================================
-- CONFIGURAÇÃO INICIAL DO SISTEMA DE GERENCIAMENTO DE BANCO
-- =====================================================

-- Criação do banco de dados principal
CREATE DATABASE to_do_list_db;

-- Criação do usuário da aplicação com permissões de login
CREATE ROLE to_do_list_app WITH LOGIN PASSWORD '1234';
GRANT CONNECT ON DATABASE to_do_list_db TO to_do_list_app;

-- Criação do role orchestrator para gerenciar permissões
CREATE ROLE app_orchestrator;
GRANT app_orchestrator TO to_do_list_app;

-- =====================================================
-- CONFIGURAÇÃO DE SEGURANÇA - REVOGAÇÃO DE PERMISSÕES PÚBLICAS
-- =====================================================

-- Revoga todas as permissões do schema público para usuários públicos
REVOKE ALL ON SCHEMA public FROM PUBLIC;

-- Revoga permissões padrão para tabelas, sequências, funções e schemas
ALTER DEFAULT PRIVILEGES REVOKE ALL ON TABLES FROM PUBLIC;
ALTER DEFAULT PRIVILEGES REVOKE ALL ON SEQUENCES FROM PUBLIC;
ALTER DEFAULT PRIVILEGES REVOKE ALL ON FUNCTIONS FROM PUBLIC;
ALTER DEFAULT PRIVILEGES REVOKE ALL ON SCHEMAS FROM PUBLIC;
