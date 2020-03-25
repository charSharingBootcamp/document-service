package com.charsharing.bootcamp.document.controller;

import com.charsharing.bootcamp.document.domain.Block;
import com.charsharing.bootcamp.document.domain.BlockUpdateRequest;
import com.charsharing.bootcamp.document.domain.Document;
import com.charsharing.bootcamp.document.domain.Tab;
import com.charsharing.bootcamp.document.service.DocumentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static java.util.Objects.isNull;

@RestController
@RequestMapping("/documents")
@Slf4j
public class DocumentController {

    public DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    /**
     * gets a list with all available documents
     * @return a Http response with the documents in its body
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Iterable<Document>> getDocuments() {
        Iterable<Document> documents = documentService.getAllDocuments();
        return ResponseEntity.ok(documents);
    }

    /**
     * gets the document with the given title
     * @param title the title of the document
     * @return a Http response with the document in its body.
     * If no Document with the title is found the response hat status 'not found'
     */
    @GetMapping(path = "/{title}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Document> getDocumentByTitle(@PathVariable(required = false) String title) {
        final Document document = documentService.findDocumentByTitle(title);
        if (isNull(document)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(document);
    }

    /**
     * gets the block of a document
     * @param documentTitle the title of the document which contains the block
     * @param tabIndex the index of the tab in which the block is
     * @param blockId the id of the block
     * @return a Http response with the found block in its body.
     * If no block with the title is found the response hat status 'not found'
     */
    @GetMapping(path = "/{title}/{tabIndex}/{blockId}")
    public ResponseEntity<Block> getBlock(
            @PathVariable("title") String documentTitle,
            @PathVariable("tabIndex") int tabIndex,
            @PathVariable("blockId") String blockId) {
        Block block = documentService.getBlock(documentTitle, tabIndex, blockId);
        if (isNull(block)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(block);
    }

    /**
     * Updates the content of a block
     * @param title the title of the document containing the block
     * @param tabIndex the index of the tab containing the block
     * @param blockId the id of the block
     * @param updateRequest the request containing the updated data
     * @return a Http response with the updated block in its body.
     */
    @PutMapping(path = "/{title}/{tabIndex}/{blockId}")
    public ResponseEntity<Block> updateBlock(
            @PathVariable("title") String title,
            @PathVariable("tabIndex") int tabIndex,
            @PathVariable("blockId") String blockId,
            @RequestBody BlockUpdateRequest updateRequest) {
        return documentService.updateBlock(title, blockId, tabIndex, updateRequest);
    }

    /**
     * Adds a textblock to a tab
     * @param newText the text of the new block
     * @param name the author of the new block
     * @param tabIndex the index of the tab in which the block will be created
     * @param code determines whether the block contains code
     * @param id the title of the document
     * @return a http response with the updated document in its body
     */
    @PutMapping("/{title}/{tabIndex}")
    public ResponseEntity<Document> updateDocument(
            @RequestBody String newText,
            @RequestParam("name") String name,
            @PathVariable("tabIndex") int tabIndex,
            @RequestParam("code") boolean code,
            @PathVariable("title") String id) {
        return ResponseEntity.ok(documentService.updateDocument(id, tabIndex, newText, name, code));
    }

    /**
     * adds a tab to a document
     * @param title the title of the document
     * @param tab the tab which will be added
     * @return a http response with the added tab in its body
     */
    @PostMapping("/{title}")
    public ResponseEntity<Tab> addTab(
            @PathVariable("title") String title,
            @RequestBody Tab tab) {
       return documentService.addTab(title, tab);

    }


    /**
     * creates a new document
     * @param document the new document which will be added
     * @return a http resonse with the added document in its body
     */
    @PostMapping
    public ResponseEntity<Document> createDocument(@RequestBody Document document) {
        return documentService.createDocument(document);
    }

    /**
     * Deletes documents
     * @param documentTitles a List of the titles of the documents which will be deleted
     * @return a http response with the amount of documents deleted
     */
    @DeleteMapping
    public ResponseEntity<Integer> deleteDocuments(@RequestBody List<String> documentTitles) {
        log.info("Delete request received");
        return documentService.deleteDocuments(documentTitles);
    }

}
