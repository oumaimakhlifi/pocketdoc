package pocketDock.com.pocketDock.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Data
@Table(name = "files")
@Getter
@Setter
public class FileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_name")
    private String filename;

    @Column(name = "content_type")
    private String contentType;


    @Lob
    @Column(name = "file")
    private byte[] data;

    @JsonIgnore
    @ManyToOne
    private OurUsers doc;

}
