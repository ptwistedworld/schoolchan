package com.schoolchan.board;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "quotes")
public class Quote {

    @Id
    private UUID id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String body;

    @Column(length = 255)
    private String attribution;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
    public String getAttribution() { return attribution; }
    public void setAttribution(String attribution) { this.attribution = attribution; }
}
