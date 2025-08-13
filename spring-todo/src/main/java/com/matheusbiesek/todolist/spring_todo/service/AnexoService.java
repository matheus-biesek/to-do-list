package com.matheusbiesek.todolist.spring_todo.service;

import com.matheusbiesek.todolist.spring_todo.entity.Anexo;
import com.matheusbiesek.todolist.spring_todo.entity.Tarefa;
import com.matheusbiesek.todolist.spring_todo.exception.anexo.AnexoNaoEncontradoException;
import com.matheusbiesek.todolist.spring_todo.repository.AnexoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AnexoService {

    private final AnexoRepository anexoRepository;

    @Value("${app.anexos.diretorio:/home/biesek/projetos/to-do-list/anexos}")
    private String diretorioAnexos;

    @Transactional(readOnly = true)
    public List<Anexo> findByTarefa(Tarefa tarefa) {
        try {
            return anexoRepository.findByTarefa(tarefa);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar anexos da tarefa: " + e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public Optional<Anexo> findByIdAndTarefa(Long anexoId, Tarefa tarefa) {
        try {
            return anexoRepository.findByAnexoIdAndTarefa(anexoId, tarefa);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar anexo: " + e.getMessage(), e);
        }
    }

    @Transactional
    public Anexo salvarAnexo(MultipartFile arquivo, Tarefa tarefa) {
        try {
            if (arquivo.isEmpty()) {
                throw new IllegalArgumentException("Arquivo não pode estar vazio");
            }

            String nomeOriginal = arquivo.getOriginalFilename();
            if (nomeOriginal == null || nomeOriginal.trim().isEmpty()) {
                throw new IllegalArgumentException("Nome do arquivo é obrigatório");
            }

            String nomeArquivo = UUID.randomUUID().toString() + "_" + nomeOriginal;
            Path caminhoCompleto = criarDiretorioSeNaoExistir().resolve(nomeArquivo);

            Files.copy(arquivo.getInputStream(), caminhoCompleto, StandardCopyOption.REPLACE_EXISTING);

            Anexo anexo = new Anexo();
            anexo.setTarefa(tarefa);
            anexo.setNomeOriginal(nomeOriginal);
            anexo.setNomeArquivo(nomeArquivo);
            anexo.setTipoMime(arquivo.getContentType());
            anexo.setTamanho(arquivo.getSize());
            anexo.setCaminhoArquivo(caminhoCompleto.toString());

            return anexoRepository.save(anexo);
        } catch (IOException e) {
            System.err.println("Erro de IO ao salvar arquivo: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erro ao salvar arquivo: " + e.getMessage(), e);
        } catch (Exception e) {
            System.err.println("Erro geral ao salvar anexo: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erro ao salvar anexo: " + e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public Resource carregarAnexo(Long anexoId, Tarefa tarefa) {
        try {
            Anexo anexo = anexoRepository.findByAnexoIdAndTarefa(anexoId, tarefa)
                    .orElseThrow(() -> new AnexoNaoEncontradoException("Anexo não encontrado"));

            Path caminhoArquivo = Paths.get(anexo.getCaminhoArquivo());
            Resource resource = new UrlResource(caminhoArquivo.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Não foi possível ler o arquivo");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Erro ao carregar arquivo: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao carregar anexo: " + e.getMessage(), e);
        }
    }

    @Transactional
    public void deletarAnexo(Long anexoId, Tarefa tarefa) {
        try {
            Anexo anexo = anexoRepository.findByAnexoIdAndTarefa(anexoId, tarefa)
                    .orElseThrow(() -> new AnexoNaoEncontradoException("Anexo não encontrado"));

            Path caminhoArquivo = Paths.get(anexo.getCaminhoArquivo());
            
            try {
                Files.deleteIfExists(caminhoArquivo);
            } catch (IOException e) {
                // Log do erro mas não interrompe a operação
                System.err.println("Erro ao deletar arquivo físico: " + e.getMessage());
            }

            anexoRepository.deleteByAnexoIdAndTarefa(anexoId, tarefa);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao deletar anexo: " + e.getMessage(), e);
        }
    }

    private Path criarDiretorioSeNaoExistir() throws IOException {
        Path diretorio = Paths.get(diretorioAnexos);
        if (!Files.exists(diretorio)) {
            Files.createDirectories(diretorio);
        }
        return diretorio;
    }
}