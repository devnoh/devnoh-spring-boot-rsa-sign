package devnoh.demoapp.domain;

import lombok.Data;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "partner_certificate")
@Data
public class PartnerCertificate extends AbstractEntity {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "partner_id", nullable = false)
    private Partner partner;

    @Lob
    @Column(name = "certificate", nullable = false)
    private byte[] certificate;

    @Column(name = "version", length = 10)
    private String version;

    @Column(name = "serial_number", length = 50)
    private String serialNumber;

    @Column(name = "subject", length = 255)
    private String subject;

    @Column(name = "issuer", length = 255)
    private String issuer;

    @Column(name = "valid_from")
    private Date validFrom;

    @Column(name = "valid_until")
    private Date validUntil;

    @Column(name = "public_key", length = 2000)
    private String publicKey;

    @Column(name = "sig_algorithm", length = 50)
    private String sigAlgorithm;
}
