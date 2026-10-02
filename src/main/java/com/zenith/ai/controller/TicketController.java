package com.zenith.ai.controller;

import com.zenith.ai.model.Ticket;
import com.zenith.ai.repository.TicketRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tickets")
@CrossOrigin(origins = "*")
public class TicketController {

    private final TicketRepository ticketRepository;

    public TicketController(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public record TicketRequest(String customerEmail, String rawMessage) {}

    // POST Endpoint to submit a new complaint
    @PostMapping
    public ResponseEntity<Ticket> createTicket(@RequestBody TicketRequest request) {
        Ticket ticket = new Ticket();
        ticket.setCustomerEmail(request.customerEmail());
        ticket.setRawMessage(request.rawMessage());
        ticket.setStatus("PENDING");

        // Intelligent categorization based on keywords in the message
        String msg = request.rawMessage().toLowerCase();
        if (msg.contains("pay") || msg.contains("refund") || msg.contains("charge") || msg.contains("invoice") || msg.contains("billing")) {
            ticket.setCategory("BILLING");
            ticket.setAssignedDepartment("Finance Team");
            ticket.setPriority(msg.contains("urgent") || msg.contains("twice") ? "URGENT" : "MEDIUM");
            ticket.setSentiment("ANGRY");
        } else if (msg.contains("api") || msg.contains("error") || msg.contains("server") || msg.contains("password") || msg.contains("login")) {
            ticket.setCategory("TECHNICAL");
            ticket.setAssignedDepartment("Tech Ops");
            ticket.setPriority("HIGH");
            ticket.setSentiment("NEUTRAL");
        } else if (msg.contains("ship") || msg.contains("track") || msg.contains("package") || msg.contains("delivery") || msg.contains("damaged")) {
            ticket.setCategory("SHIPPING");
            ticket.setAssignedDepartment("Customer Success");
            ticket.setPriority("HIGH");
            ticket.setSentiment("FRUSTRATED");
        } else {
            ticket.setCategory("GENERAL");
            ticket.setAssignedDepartment("General Support");
            ticket.setPriority("LOW");
            ticket.setSentiment("POSITIVE");
        }

        ticket.setAiSummary(request.rawMessage());

        Ticket saved = ticketRepository.save(ticket);
        return ResponseEntity.ok(saved);
    }

    // GET Endpoint to fetch all support tickets for the dashboard
    @GetMapping
    public ResponseEntity<List<Ticket>> getAllTickets() {
        List<Ticket> tickets = ticketRepository.findAll();
        return ResponseEntity.ok(tickets);
    }

    // POST Endpoint to update ticket status and record which agent resolved it
    @PostMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long id, 
            @RequestParam String status, 
            @RequestParam(required = false) String agentName) {
        
        Optional<Ticket> ticketOpt = ticketRepository.findById(id);
        if (ticketOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Ticket ticket = ticketOpt.get();
        ticket.setStatus(status);
        
        if ("RESOLVED".equals(status) && agentName != null && !agentName.isEmpty()) {
            ticket.setResolvedBy(agentName);
        } else if ("PENDING".equals(status)) {
            ticket.setResolvedBy(null);
        }
        
        ticketRepository.save(ticket);
        return ResponseEntity.ok(ticket);
    }
}