package devnoh.demoapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class AbstractEntity {

    @JsonIgnore
    @Getter
    @Setter
    private Date createdAt;

    @JsonIgnore
    @Getter
    @Setter
    private Long createdBy;

    @JsonIgnore
    @Getter
    @Setter
    private Date updatedAt;

    @JsonIgnore
    @Getter
    @Setter
    private Long updatedBy;

    public abstract boolean equals(Object o);

    public abstract int hashCode();

    public abstract String toString();
}
