package no.entur.mummu.resources;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Controller
public class IndexController {

    @GetMapping("/")
    public ResponseEntity<String> index() throws IOException {
        var resource = new ClassPathResource("public/index.html");
        var content = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

        var headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_HTML);
        headers.set(HttpHeaders.CONTENT_TYPE, "text/html; charset=UTF-8");

        return new ResponseEntity<>(content, headers, HttpStatus.OK);
    }
}

