package com.zenith.ai.model;

public record TicketAnalysis(
    String category,      
    String priority,     
    String department,    
    String sentiment,    
    String summary       
) {}