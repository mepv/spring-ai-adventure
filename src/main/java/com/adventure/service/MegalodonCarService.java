package com.adventure.service;

import jakarta.annotation.PostConstruct;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MegalodonCarService {

    private final VectorStore vectorStore;

    public MegalodonCarService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @PostConstruct
    public void init() {
        TextReader textReader = new TextReader(new ClassPathResource("megalodon_features.txt"));
        TokenTextSplitter tokenTextSplitter = new TokenTextSplitter(100, 100, 5, 1000, true);
        List<Document> documents = tokenTextSplitter.split(textReader.get());
        vectorStore.add(documents);
    }

    public List<String> getRelevantFeatures(String storyContext) {
        SearchRequest request = SearchRequest.builder().query(storyContext)
                .similarityThreshold(0.7)
                .topK(2)
                .build();

        return vectorStore.similaritySearch(request)
                .stream()
                .map(Document::getText)
                .toList();
    }
}
