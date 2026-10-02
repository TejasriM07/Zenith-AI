package com.zenith.ai.service;

import com.zenith.ai.model.Ticket;
import com.zenith.ai.model.TicketAnalysis;
import com.zenith.ai.repository.TicketRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TicketService {

    private final ChatClient chatClient;
    private final TicketRepository ticketRepository;

    // Spring AI automatically injects the ChatClient builder and our repository
    public TicketService(ChatClient.Builder chatClientBuilder, TicketRepository ticketRepository) {
        this.chatClient = chatClientBuilder.build();
        this.ticketRepository = ticketRepository;
    }

    public Ticket analyzeAndSaveTicket(String customerEmail, String rawMessage) {
        
        // Prompt the AI to analyze the raw complaint and map it to our TicketAnalysis record
        String prompt = """
            Analyze the following customer support ticket message:
            "%s"
            
            Classify the category (e.g., BILLING, TECHNICAL, SHIPPING, GENERAL), 
            priority (LOW, MEDIUM, URGENT), 
            the best routing department (Finance Team, DevOps Support, Customer Success, Logistics), 
            customer sentiment (ANGRY, NEUTRAL, HAPPY), 
            and provide a concise 1-sentence summary.
            """.formatted(rawMessage);

        TicketAnalysis analysis = chatClient.prompt()
                .user(prompt)
                .call()
                .entity(TicketAnalysis.class);

        // Map the AI findings and original data to our MySQL Entity
        Ticket ticket = new Ticket();
        ticket.setCustomerEmail(customerEmail);
        ticket.setRawMessage(rawMessage);
        
        if (analysis != null) {
            ticket.setCategory(analysis.category());
            ticket.setPriority(analysis.priority());
            ticket.setAssignedDepartment(analysis.department());
            ticket.setSentiment(analysis.sentiment());
            ticket.setAiSummary(analysis.summary());
        }

        // Save into MySQL database
        return ticketRepository.save(ticket);
    }

    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }
}