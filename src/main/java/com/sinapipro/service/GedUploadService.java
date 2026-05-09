package com.sinapipro.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.sinapipro.model.DocumentoGed;
import com.sinapipro.repository.DocumentosGedRepository;

/**
 * Serviço de upload real de arquivos para o GED.
 *
 * REGRAS DE SEGURANÇA (OWASP File Upload):
 * 1. Validação de MIME type (não confiar apenas na extensão)
 * 2. Limite de tamanho (padrão: 50 MB)
 * 3. Nome do arquivo sanitizado (UUID + extensão original)
 * 4. Armazenamento fora do webroot
 * 5. Tipos permitidos: PDF, imagens, documentos Office, ZIP
 *
 * Referência: OWASP File Upload Cheat Sheet.
 */
@Service
public class GedUploadService {

    private static final long MAX_SIZE_BYTES = 50 * 1024 * 1024L; // 50 MB

    private static final List<String> TIPOS_PERMITIDOS = Arrays.asList(
            "application/pdf",
            "image/jpeg",
            "image/png",
            "image/gif",
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "text/plain",
            "application/zip",
            "application/x-zip-compressed"
    );

    @Value("${ged.upload.dir:${user.home}/sinapiPRO/ged}")
    private String uploadDir;

    private final DocumentosGedRepository repository;

    public GedUploadService(DocumentosGedRepository repository) {
        this.repository = repository;
    }

    /**
     * Faz upload de um arquivo e salva o documento no GED.
     *
     * @param arquivo    arquivo enviado via MultipartFile
     * @param documento  metadados do documento (nome, descrição, obra, cliente)
     * @return documento salvo com caminho do arquivo
     */
    @Transactional
    public DocumentoGed upload(MultipartFile arquivo, DocumentoGed documento) throws IOException {
        validarArquivo(arquivo);

        // Cria diretório de upload se não existir
        Path uploadPath = Paths.get(uploadDir);
        Files.createDirectories(uploadPath);

        // Gera nome único para o arquivo (evita colisões e path traversal)
        String extensao = obterExtensao(arquivo.getOriginalFilename());
        String nomeArquivo = UUID.randomUUID().toString() + extensao;

        // Organiza por ano/mês
        String subDir = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM"));
        Path dirDestino = uploadPath.resolve(subDir);
        Files.createDirectories(dirDestino);

        Path caminhoArquivo = dirDestino.resolve(nomeArquivo);
        Files.copy(arquivo.getInputStream(), caminhoArquivo, StandardCopyOption.REPLACE_EXISTING);

        // Salva metadados
        documento.setNome(documento.getNome() != null && !documento.getNome().isBlank()
                ? documento.getNome()
                : arquivo.getOriginalFilename());
        documento.setTipoArquivo(arquivo.getContentType());
        documento.setTamanho(arquivo.getSize());
        documento.setCaminho(subDir + "/" + nomeArquivo);
        documento.setDataUpload(LocalDateTime.now());

        return repository.saveAndFlush(documento);
    }

    /**
     * Retorna o Path físico de um documento para download.
     */
    public Path getCaminhoFisico(DocumentoGed documento) {
        return Paths.get(uploadDir).resolve(documento.getCaminho());
    }

    /**
     * Exclui o arquivo físico e o registro do banco.
     */
    @Transactional
    public void excluir(Long codigo) throws IOException {
        DocumentoGed documento = repository.findById(codigo)
                .orElseThrow(() -> new RuntimeException("Documento não encontrado"));

        // Remove arquivo físico
        if (documento.getCaminho() != null) {
            Path arquivo = getCaminhoFisico(documento);
            Files.deleteIfExists(arquivo);
        }

        repository.deleteById(codigo);
        repository.flush();
    }

    private void validarArquivo(MultipartFile arquivo) {
        if (arquivo == null || arquivo.isEmpty()) {
            throw new RuntimeException("Nenhum arquivo selecionado.");
        }

        if (arquivo.getSize() > MAX_SIZE_BYTES) {
            throw new RuntimeException(String.format(
                    "Arquivo muito grande: %.1f MB. Limite: 50 MB.",
                    arquivo.getSize() / (1024.0 * 1024.0)));
        }

        String contentType = arquivo.getContentType();
        if (contentType == null || !TIPOS_PERMITIDOS.contains(contentType)) {
            throw new RuntimeException(
                    "Tipo de arquivo não permitido: " + contentType
                    + ". Tipos aceitos: PDF, imagens, documentos Office, ZIP.");
        }

        // Verifica nome do arquivo (evita path traversal)
        String nomeOriginal = arquivo.getOriginalFilename();
        if (nomeOriginal != null && (nomeOriginal.contains("..") || nomeOriginal.contains("/"))) {
            throw new RuntimeException("Nome de arquivo inválido.");
        }
    }

    private String obterExtensao(String nomeArquivo) {
        if (nomeArquivo == null || !nomeArquivo.contains(".")) return "";
        return nomeArquivo.substring(nomeArquivo.lastIndexOf(".")).toLowerCase();
    }
}
