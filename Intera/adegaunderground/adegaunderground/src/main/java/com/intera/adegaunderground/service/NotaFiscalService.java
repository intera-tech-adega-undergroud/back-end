package com.intera.adegaunderground.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intera.adegaunderground.dto.NotaFiscalResponseDTO;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Service
public class NotaFiscalService {

    private final String OCR_API_KEY = "helloworld"; 
    private final String OCR_URL = "https://api.ocr.space/parse/image";

    public NotaFiscalResponseDTO lerNotaFiscal(MultipartFile arquivo) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            ByteArrayResource arquivoResource = new ByteArrayResource(arquivo.getBytes()) {
                @Override
                public String getFilename() {
                    return arquivo.getOriginalFilename();
                }
            };

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("apikey", OCR_API_KEY);
            body.add("language", "por");
            body.add("OCREngine", "2");
            body.add("isOverlayRequired", "false");
            body.add("file", arquivoResource);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            HttpEntity<MultiValueMap<String, Object>> request =
                    new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    OCR_URL,
                    request,
                    String.class
            );

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());

            boolean erro = root.path("IsErroredOnProcessing").asBoolean();

            if (erro) {
                String mensagemErro = root.path("ErrorMessage").toString();
                throw new RuntimeException("Erro no OCR: " + mensagemErro);
            }

            String textoExtraido = root
                    .path("ParsedResults")
                    .get(0)
                    .path("ParsedText")
                    .asText();

            return new NotaFiscalResponseDTO(textoExtraido);

        } catch (Exception e) {
            throw new RuntimeException("Erro ao processar nota fiscal: " + e.getMessage());
        }
    }
}