package pocketDock.com.pocketDock.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Lob;
import lombok.Data;
import pocketDock.com.pocketDock.entity.OurUsers;
import pocketDock.com.pocketDock.entity.Product;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.sql.Blob;
import java.util.Date;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReqRes {
    private Integer id;
    private int statusCode;
    private String error;
    private String message;
    private String token;
    private String refreshToken;
    private String expirationTime;
    private String name;
    private String lastname;
    private String City;
    private String Country;
    private String rue;
    private String codePostal;
    private Date datenaissance ;
    private String aboutme;
    private String email;
    private String role;
    private String password;
    private Integer telephone;
    private Integer status;
    private String profileImage;
    private String profileImage1;

    private List<Product> products;
    private OurUsers ourUsers;

}
