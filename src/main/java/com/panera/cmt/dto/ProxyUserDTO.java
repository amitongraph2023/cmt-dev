package com.panera.cmt.dto;

import com.panera.cmt.mongo.entity.AuthenticatedUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProxyUserDTO {
    private String username;
    private String emailAddress;
    private String firstName;
    private String lastName;

    public static ProxyUserDTO fromAuthenticatedUser(AuthenticatedUser authenticatedUser){
        ProxyUserDTO proxyUserDTO = new ProxyUserDTO();
        if(authenticatedUser != null) {
            if (authenticatedUser.getUsername() != null) {
                proxyUserDTO.setUsername(authenticatedUser.getUsername());
            }
            if (authenticatedUser.getEmailAddress() != null) {
                proxyUserDTO.setEmailAddress(authenticatedUser.getEmailAddress());
            }
            if (authenticatedUser.getFirstName() != null) {
                proxyUserDTO.setFirstName(authenticatedUser.getFirstName());
            }
            if (authenticatedUser.getLastName() != null) {
                proxyUserDTO.setLastName(authenticatedUser.getLastName());
            }
        }
        return proxyUserDTO;
    }

    @Override
    public String toString(){
        return String.format(
                "{\"username\":\"%s\",\"emailAddress\":\"%s\",\"firstName\":\"%s\",\"lastName\":\"%s\"}"
                ,(username != null)? username: "null"
                ,(emailAddress != null)? emailAddress: "null"
                ,(firstName != null)? firstName: "null"
                ,(lastName != null)? lastName: "null"
        );
    }
}
