package devnoh.demoapp.domain;

import lombok.Data;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "partner")
@Data
public class Partner extends AbstractEntity {

    @Id
    @GeneratedValue
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "api_key")
    private String apiKey;

    @Column(name = "active")
    private boolean active;

    @Column(name = "service_start_date")
    private Date serviceStartDate;

    @Column(name = "service_end_date")
    private Date serviceEndDate;

    @Column(name = "decision_endpoint")
    private String decisionEndpoint;

}
