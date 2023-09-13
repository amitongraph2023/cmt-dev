package com.panera.cmt.service.sso;


import com.panera.cmt.dto.sso.ImpersonateAuthenticationTokenDTO;
import com.panera.cmt.dto.sso.SpoofButtonDTO;
import com.panera.cmt.entity.ResponseHolder;
import com.panera.cmt.mongo.entity.AuthenticatedUser;

import java.util.Optional;
import java.util.Set;

public interface ISSOService {

     Optional<ResponseHolder<ImpersonateAuthenticationTokenDTO>> loginCustomerSession(Long customerId, String unit);

     Optional<ResponseHolder<String>> logoutCustomerSession(String ssoToken, Long customerId, String unit);

     Optional<Set<SpoofButtonDTO>> getSpoofButtons(Boolean nonMyPanera);


}
