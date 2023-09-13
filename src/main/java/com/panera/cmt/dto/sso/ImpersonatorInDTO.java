package com.panera.cmt.dto.sso;

import com.panera.cmt.dto.ProxyUserDTO;
import com.panera.cmt.mongo.entity.AuthenticatedUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImpersonatorInDTO {
    private ProxyUserDTO proxyUserDTO;
    private String spoofUnit;

    public static ImpersonatorInDTO fromAuthenticatedUser(AuthenticatedUser authenticatedUser){
        ImpersonatorInDTO impersonatorInDTO = new ImpersonatorInDTO();
        impersonatorInDTO.setProxyUserDTO(ProxyUserDTO.fromAuthenticatedUser(authenticatedUser));
        return impersonatorInDTO;
    }

    @Override
    public String toString(){
        return getProxyUserDTO().toString();
    }
}
