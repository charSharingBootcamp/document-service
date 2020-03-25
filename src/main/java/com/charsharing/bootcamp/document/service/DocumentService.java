package com.charsharing.bootcamp.document.service;

import com.charsharing.bootcamp.document.domain.*;
import com.charsharing.bootcamp.document.repository.DocumentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import static java.util.Objects.isNull;

@Service
@Slf4j
public class DocumentService {

    private DocumentRepository documentRepository;
    private RestTemplateBuilder templateBuilder;
    @Value("${charSharing.filter.service.url}")
    private String filterServiceURL;

    public DocumentService(DocumentRepository repository, RestTemplateBuilder templateBuilder) {
        this.documentRepository = repository;
        this.templateBuilder = templateBuilder;
    }

    public Document findDocumentByTitle(String title) {
        return documentRepository.findDocumentByTitle(title);
    }

    public Iterable<Document> getAllDocuments() {
        return documentRepository.findAll();
    }

    /**
     * creates a new document in the database.
     * The document is added with one tab with the same title as the document
     * @param document the document to add to the database
     * @return a http response containing the created document in its body
     */
    public ResponseEntity<Document> createDocument(Document document) {
        final Document repositoryDocument = documentRepository.findDocumentByTitle(document.getTitle());
        if (repositoryDocument != null) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        document.setContent(List.of(new Tab(document.getTitle())));
        final Document savedDocument = documentRepository.save(document);
        return new ResponseEntity<>(savedDocument, HttpStatus.CREATED);
    }

    public ResponseEntity<Integer> deleteDocuments(List<String> documentTitles) {
        log.info("Deleting Documents in Database");
        documentRepository.deleteByTitleIn(documentTitles);
        return ResponseEntity.ok(documentTitles.size());
    }



    public Document updateDocument(String title, int tabIndex, String newText, String name, boolean code) {
        String newContent = filterText(newText);
        Document document = documentRepository.findDocumentByTitle(title);
        Tab tab = document.getContent().get(tabIndex);
        Block newBlock = new Block();
        newBlock.setContent(newContent);
        newBlock.setCreatedAt(new Date());
        newBlock.setId(UUID.randomUUID().toString());
        newBlock.setCreator(name);
        newBlock.setCode(code);

        List<Block> tabContent = tab.getTextBlocks();
        if (isNull(tabContent)) {
            tabContent = List.of(newBlock);
            tab.setTextBlocks(tabContent);
        } else {
            tabContent.add(newBlock);
        }

        document.setUpdatedAt(new Date());

        return documentRepository.save(document);

    }

    public Block getBlock(final String documentTitle, int tabIndex, final String blockId) {
        final Document document = findDocumentByTitle(documentTitle);
        for (Block block : document.getContent().get(tabIndex).getTextBlocks()) {
            if (block.getId().equals(blockId)) {
                return block;
            }
        }
        return null;
    }

    public ResponseEntity<Block> updateBlock(String title, String blockId, int tabIndex, BlockUpdateRequest updateRequest) {
        Document repositoryDocument = documentRepository.findDocumentByTitle(title);
        //Das Dokument existiert nicht in der DB
        if (isNull(repositoryDocument)) {
            return ResponseEntity.noContent().build();
        }
        Block block = null;
        //Den Block aus dem Dokument raussuchen
        for (Block b : repositoryDocument.getContent().get(tabIndex).getTextBlocks()) {
            if (blockId.equals(b.getId())) {
                block = b;
                break;
            }
        }
        //Der Block existiert nicht in dem Dokument
        if (isNull(block)) {
            return ResponseEntity.noContent().build();
        }

        //neuen inhalt filtern
        String newContent = filterText(updateRequest.getContent());

        //Inhalt ist unverändert oder es wurde kein Benutzername angegeben
        if (newContent.equals(block.getContent()) || updateRequest.getUsername().isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        //update block content
        block.setContent(newContent);
        block.setUpdatedAt(new Date());
        block.setUpdatedBy(updateRequest.getUsername());

        //save changes
        documentRepository.save(repositoryDocument);

        return ResponseEntity.ok(block);
    }

    private String filterText(String text) {
        RestTemplate restTemplate = templateBuilder.build();
        FilterRequest filterRequest = new FilterRequest();
        filterRequest.setText(text);
        HttpEntity<FilterRequest> request = new HttpEntity<>(filterRequest);
        ResponseEntity<FilterResponse> exchangeAnswer = restTemplate.exchange(filterServiceURL + "/filter", HttpMethod.POST, request, FilterResponse.class);
        return isNull(exchangeAnswer.getBody()) ? null : exchangeAnswer.getBody().getFilteredText();
    }

    public ResponseEntity<Tab> addTab(String title, Tab tab) {
        Document document = findDocumentByTitle(title);
        if (isNull(document)) {
            return ResponseEntity.badRequest().build();
        }
        List<Tab> tabs = document.getContent();
        tabs.add(tab);
        if (tab.getTitle().isBlank()) {
            tab.setTitle(tabs.size() + "");
        }
        documentRepository.save(document);
        return ResponseEntity.ok(tab);
    }
}
