package com.intera.adegaunderground.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intera.adegaunderground.dto.NotaFiscalResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Set;

@Service
public class NotaFiscalService {

    private static final Logger logger = LoggerFactory.getLogger(NotaFiscalService.class);
    private static final Set<String> CONTENT_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "application/pdf"
    );

    private final String ocrApiKey;
    private final String ocrUrl;
    private final long maxFileSizeBytes;

    public NotaFiscalService(
            @Value("${app.ocr.api-key}") String ocrApiKey,
            @Value("${app.ocr.url:https://api.ocr.space/parse/image}") String ocrUrl,
            @Value("${app.ocr.max-file-size-bytes:5242880}") long maxFileSizeBytes
    ) {
        if (ocrApiKey == null || ocrApiKey.isBlank()) {
            throw new IllegalStateException("A chave da API OCR deve estar configurada.");
        }

        this.ocrApiKey = ocrApiKey;
        this.ocrUrl = ocrUrl;
        this.maxFileSizeBytes = maxFileSizeBytes;
    }

    public NotaFiscalResponseDTO lerNotaFiscal(MultipartFile arquivo) {
        validarArquivo(arquivo);

        try {
            RestTemplate restTemplate = new RestTemplate();

            ByteArrayResource arquivoResource = new ByteArrayResource(arquivo.getBytes()) {
                @Override
                public String getFilename() {
                    return arquivo.getOriginalFilename();
                }
            };

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("apikey", ocrApiKey);
            body.add("language", "por");
            body.add("OCREngine", "2");
            body.add("isOverlayRequired", "false");
            body.add("file", arquivoResource);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            HttpEntity<MultiValueMap<String, Object>> request =
                    new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    ocrUrl,
                    request,
                    String.class
            );

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());

            boolean erro = root.path("IsErroredOnProcessing").asBoolean();

            if (erro) {
                throw new IllegalStateException("O OCR retornou erro ao processar a nota fiscal.");
            }

            String textoExtraido = root
                    .path("ParsedResults")
                    .get(0)
                    .path("ParsedText")
                    .asText();

            if (textoExtraido == null || textoExtraido.isBlank()) {
                throw new IllegalStateException("Não foi possível extrair texto da nota fiscal.");
            }

            return new NotaFiscalResponseDTO(textoExtraido);

        } catch (IOException e) {
            logger.warn("Falha de leitura do arquivo de nota fiscal.", e);
            throw new IllegalArgumentException("Arquivo de nota fiscal inválido.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw e;
        } catch (RuntimeException e) {
            logger.error("Erro interno ao processar OCR da nota fiscal.", e);
            throw new IllegalStateException("Erro interno no processamento da nota fiscal.");
        }
    }

    private void validarArquivo(MultipartFile arquivo) {
        if (arquivo == null || arquivo.isEmpty()) {
            throw new IllegalArgumentException("Envie um arquivo para leitura da nota fiscal.");
        }

        if (arquivo.getSize() > maxFileSizeBytes) {
            throw new IllegalArgumentException("Arquivo excede o tamanho permitido para OCR.");
        }

        String contentType = arquivo.getContentType();
        if (contentType == null || !CONTENT_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("Tipo de arquivo não suportado para OCR.");
        }
    }
}