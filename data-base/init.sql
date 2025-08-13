-- Script de inicialização do banco de dados
CREATE DATABASE to_do_list_db;

-- Criar usuário da aplicação
CREATE USER to_do_list_app WITH PASSWORD '1234';

-- Conceder privilégios ao usuário da aplicação
GRANT ALL PRIVILEGES ON DATABASE to_do_list_db TO to_do_list_app;

-- Conectar ao banco to_do_list_db para conceder privilégios no schema
\c to_do_list_db;

-- Conceder privilégios no schema public
GRANT ALL ON SCHEMA public TO to_do_list_app;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO to_do_list_app;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO to_do_list_app;

-- Configurar privilégios padrão para futuras tabelas
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO to_do_list_app;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO to_do_list_app;