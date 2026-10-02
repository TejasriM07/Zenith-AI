package com.zenith.ai.model;

import jakarta.persistence.*;

@Entity
@Table(name = "support_tickets")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String customerEmail;
    
    @Column(length = 1000)
    private String rawMessage;
    
    private String category;
    private String priority;
    private String assignedDepartment;
    private String sentiment;
    
    @Column(length = 2000)
    private String aiSummary;
    private String resolvedBy; // Stores the name/email of the agent who resolved it

 // Getter and Setter
 public String getResolvedBy() { return resolvedBy; }
 public void setResolvedBy(String resolvedBy) { this.resolvedBy = resolvedBy; }
    private String status = "PENDING"; // Default status for tickets

    public Ticket() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public String getRawMessage() { return rawMessage; }
    public void setRawMessage(String rawMessage) { this.rawMessage = rawMessage; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getAssignedDepartment() { return assignedDepartment; }
    public void setAssignedDepartment(String assignedDepartment) { this.assignedDepartment = assignedDepartment; }

    public String getSentiment() { return sentiment; }
    public void setSentiment(String sentiment) { this.sentiment = sentiment; }

    public String getAiSummary() { return aiSummary; }
    public void setAiSummary(String aiSummary) { this.aiSummary = aiSummary; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}