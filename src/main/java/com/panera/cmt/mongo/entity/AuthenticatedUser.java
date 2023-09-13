package com.panera.cmt.mongo.entity;

import com.panera.cmt.enums.Role;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(collection = "cmtSession")
public class AuthenticatedUser {

    @Id
    private String id;
    @Indexed(unique = true)
    private String username;
    @Indexed(unique = true)
    private String accessToken;
    @Indexed
    private Role role;
    private String firstName;
    private String lastName;
    private String displayName;
    private String knownAs;
    private String emailAddress;
    private Date loginDate;
    @Indexed(expireAfterSeconds = 0)
    private Date expirationDate;

    public String toSsoString(){
        return String.format(
                "{\"username\":\"%s\",\"emailAddress\":\"%s\",\"firstName\":\"%s\",\"lastName\":\"%s\"}"
                ,(username != null)? username: "null"
                ,(emailAddress != null)? emailAddress: "null"
                ,(firstName != null)? firstName: "null"
                ,(lastName != null)? lastName: "null"
        );
    }
}
