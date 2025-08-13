package com.matheusbiesek.todolist.spring_todo.dto.anexo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request para upload de anexo")
public class AnexoUploadRequest {

    @Schema(description = "Arquivo a ser anexado")
    private MultipartFile arquivo;
}