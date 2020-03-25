package com.charsharing.bootcamp.document.service;

import com.charsharing.bootcamp.document.domain.Block;
import com.charsharing.bootcamp.document.domain.Document;
import com.charsharing.bootcamp.document.domain.Tab;
import com.charsharing.bootcamp.document.repository.DocumentRepository;
import org.mockito.Mock;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Date;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class DocumentServiceTest {

    @Mock
    private DocumentRepository documentRepository;

    private DocumentService documentService;
    @Mock
    private RestTemplateBuilder templateBuilder;

    @BeforeMethod
    public void setUp() {
        initMocks(this);
        documentService = new DocumentService(documentRepository, templateBuilder);
    }

    @org.testng.annotations.Test
    public void shouldCreateDocument() {
        //given
        Document input = new Document("test title", "test", null, null, null, false);
        when(documentRepository.findDocumentByTitle(anyString())).thenReturn(null);
        when(documentRepository.save(any())).thenReturn(input);

        //when
        ResponseEntity<Document> actual = documentService.createDocument(input);

        //then
        assertThat(actual.getStatusCode(), is(HttpStatus.CREATED));
        assertThat(actual.getBody(), is(input));
        verify(documentRepository, times(1)).findDocumentByTitle(anyString());
        verify(documentRepository, times(1)).save(any());
        verifyNoMoreInteractions(documentRepository);


    }

    @org.testng.annotations.Test
    public void shouldReturnConflictStatus() {
        //given
        Document input = new Document("test title", "test", null, null, null, false);
        when(documentRepository.findDocumentByTitle(anyString())).thenReturn(input);


        //when
        ResponseEntity<Document> actual = documentService.createDocument(input);

        //then
        assertThat(actual.getStatusCode(), is(HttpStatus.CONFLICT));
        verify(documentRepository, times(1)).findDocumentByTitle(anyString());
        verifyNoMoreInteractions(documentRepository);
    }

    @Test
    public void shouldGetBlock() {
        //given
        String blockId = "myTestID";
        String title = "myTestTitle";

        Block block1 = new Block();
        Block block2 = new Block();

        block1.setId("notMyTestTitle");

        block2.setId("myTestID");
        block2.setCreator("test");
        block2.setContent("test content");

        Tab tab = new Tab();
        tab.setTextBlocks(List.of(block1, block2));
        Document document = new Document("myTestTitle", "test", new Date(), new Date(), List.of(tab), false);

        when(documentRepository.findDocumentByTitle(anyString())).thenReturn(document);

        //when
        Block actual = documentService.getBlock(title, 0, blockId);

        //then

        assertThat(actual.getId(), is("myTestID"));
        assertThat(actual.getCreator(), is("test"));
        assertThat(actual.getContent(), is("test content"));

        verify(documentRepository, times(1)).findDocumentByTitle(eq("myTestTitle"));
        verifyNoMoreInteractions(documentRepository);
    }
}