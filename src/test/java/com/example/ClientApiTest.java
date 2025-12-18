package com.exemplo;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import com.exemplo.utils.CnpjUtils;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class ClientApiTest {

    static {
        RestAssured.baseURI = "http://localhost:8080";
    }

    @Test
    public void shouldReturnAllClients() {
        given()
            .contentType(ContentType.JSON)
        .when()
            .get("/api/v1/clients/")
        .then()
            .statusCode(200)
            .body("$", not(empty()))
            .body("id_client", hasItems(1, 2, 3))
            .body("name", hasItem("Empresa X"));
    }

    @Test
    public void shouldReturnClientById() {
        int clientId = 1;

        given()
            .contentType(ContentType.JSON)
        .when()
            .get("/api/v1/clients/{id}", clientId)
        .then()
            .statusCode(200)
            .body("id_client", equalTo(clientId))
            .body("name", equalTo("Empresa X"))
            .body("cnpj", equalTo("12345678900001")) 
            .body("email", equalTo("contato@empresa.com")) 
            .body("phone", equalTo("41999999999")) 
            .body("address", equalTo("Rua A, 123")); 
    }
    @Test
    public void shouldReturnErrorClientDuplicated(){
        given()
            .contentType(ContentType.JSON)
            .body("{ \"name\": \"Empresa A\", " +
                  "\"cnpj\": \"12345678900009\", " +
                  "\"email\": \"contato@empresa.com\", " +
                  "\"phone\": \"41999999999\", " +
                  "\"address\": \"Rua J, 732\" }")
        .when()
            .post("/api/v1/clients/")
        .then()
            .statusCode(400)
            .body("detail", equalTo("Client with this CNPJ already exists"));
    }
    @Test
    public void shouldCreateClientSuccessfully() {
    String cnpj = CnpjUtils.gerarCnpjAleatorio();

        given()
            .contentType(ContentType.JSON)
            .body("{ \"name\": \"Empresa J Unit\", " +
                  "\"cnpj\": \"" + cnpj + "\", " +
                  "\"email\": \"contato@empresa.com\", " +
                  "\"phone\": \"41999999999\", " +
                  "\"address\": \"Rua R, 732\" }")
        .when()
            .post("/api/v1/clients/")
        .then()
            .statusCode(200)
            .body("id_client", notNullValue())
            .body("name", equalTo("Empresa J Unit"))
            .body("cnpj", equalTo(cnpj));
    }
    @Test
    public void shouldDeleteClientSuccessfully() {
        String cnpj = CnpjUtils.gerarCnpjAleatorio();
        // 1. Criar cliente 
        int idCliente = 
            given() 
                .contentType(ContentType.JSON)
                .body("{ \"name\": \"Empresa Delete\", " +
                      "\"cnpj\": \"" + cnpj + "\", " +
                      "\"email\": \"delete@empresa.com\", " +
                      "\"phone\": \"41999999999\", " +
                      "\"address\": \"Rua Delete, 456\" }") 
            .when() 
                .post("/api/v1/clients/") 
            .then()
                .log().all() // loga toda a requisição (headers, body, etc)
                .statusCode(200) 
                .extract() 
                .path("id_client");


        // 2. Deletar cliente 
            given()
                .log().all() // loga toda a requisição (headers, body, etc)
                .contentType(ContentType.JSON) 
            .when() 
                .delete("/api/v1/clients/" + idCliente)
            .then()
                .log().all() // loga toda a requisição (headers, body, etc)
                .statusCode(200); // espera sucesso no delete 

        // 3. Validar que não existe mais 
            given() 
                .contentType(ContentType.JSON) 
            .when() 
                .get("/api/v1/clients/" + idCliente) 
            .then() .statusCode(404); // espera não encontrado }
    }
}
