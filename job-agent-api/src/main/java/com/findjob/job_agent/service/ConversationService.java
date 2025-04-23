package com.findjob.job_agent.service;

import org.springframework.stereotype.Service;

import com.findjob.job_agent.model.entity.Conversation;
import com.findjob.job_agent.repository.ConversationRepository;

@Service
public class ConversationService {
    private final ConversationRepository conversationRepository;

    public ConversationService(ConversationRepository conversationRepository) {
        this.conversationRepository = conversationRepository;
    }

    public Conversation create(Conversation conversation) {
        return conversationRepository.save(conversation);
    }

    public Conversation getByUserId(String userId) {
        return conversationRepository.findByUserId(userId)
                .orElseGet(() -> create(new Conversation(userId)));
    }
}
