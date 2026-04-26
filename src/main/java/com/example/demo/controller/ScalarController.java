package com.example.demo.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
public class ScalarController {

    // Redirect root "/" to "/docs" so localhost:8080 doesn't 404
    @GetMapping("/")
    public RedirectView root() {
        return new RedirectView("/docs");
    }

    // Serves the Scalar API UI at: http://localhost:8080/docs
    @GetMapping(value = "/docs", produces = MediaType.TEXT_HTML_VALUE)
    public String scalarUi() {
        return """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="utf-8" />
                    <meta name="viewport" content="width=device-width, initial-scale=1" />
                    <title>Coffee Shop MiniPOS \u2014 API Docs</title>
                    <style>
                        body { margin: 0; padding: 0; }
                    </style>
                </head>
                <body>
                    <!-- Scalar reads the OpenAPI spec from springdoc's /v3/api-docs endpoint -->
                    <script
                        id="api-reference"
                        data-url="/v3/api-docs"
                        data-configuration='{
                            "theme": "purple",
                            "layout": "modern",
                            "defaultHttpClient": {
                                "targetKey": "java",
                                "clientKey": "unirest"
                            },
                            "metaData": {
                                "title": "Coffee Shop MiniPOS API"
                            }
                        }'
                    ></script>
                    <!-- Use the explicit standalone browser bundle path on jsDelivr -->
                    <script src="https://cdn.jsdelivr.net/npm/@scalar/api-reference@latest/dist/browser/standalone.js"></script>
                </body>
                </html>
                """;
    }
}
