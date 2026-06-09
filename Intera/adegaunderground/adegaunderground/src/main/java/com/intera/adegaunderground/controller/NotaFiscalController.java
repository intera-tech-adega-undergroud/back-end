package com.intera.adegaunderground.controller;

import com.intera.adegaunderground.dto.NotaFiscalResponseDTO;
import com.intera.adegaunderground.service.NotaFiscalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/notas-fiscais")
@CrossOrigin(origins = "*")
public class NotaFiscalController {

    @Autowired
    private NotaFiscalService notaFiscalService;

    @PostMapping("/ocr")
    public ResponseEntity<NotaFiscalResponseDTO> lerNotaFiscal(
            @RequestParam("arquivo") MultipartFile arquivo
    ) {
        NotaFiscalResponseDTO resposta = notaFiscalService.lerNotaFiscal(arquivo);
        return ResponseEntity.ok(resposta);
    }
}