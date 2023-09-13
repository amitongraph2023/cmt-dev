package com.panera.cmt.service.sso;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.panera.cmt.dto.sso.ImpersonateAuthenticationTokenDTO;
import com.panera.cmt.dto.sso.SpoofButtonDTO;
import com.panera.cmt.dto.sso.ImpersonatorInDTO;
import com.panera.cmt.entity.ResponseHolder;
import com.panera.cmt.enums.ActionType;
import com.panera.cmt.enums.SSOEndpoints;
import com.panera.cmt.filter.AuthenticatedUserManager;
import com.panera.cmt.service.app_config.IAppConfigLocalService;
import com.panera.cmt.util.StopWatch;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.panera.cmt.config.Constants.*;

@Slf4j
@Service
public class SSOService extends BaseSSOService implements ISSOService {

    private IAppConfigLocalService appConfigService;


    public SSOService(IAppConfigLocalService appConfigService){
        this.appConfigService = appConfigService;
    }



    @Override
    public Optional<ResponseHolder<ImpersonateAuthenticationTokenDTO>> loginCustomerSession(Long customerId, String unit) {

        if (customerId == null || unit == null || !isValidUnit(unit)) {
            return Optional.empty();
        }

        StopWatch stopWatch = new StopWatch(log, "ssoCustomerSessionLogin", String.format("Creating customer session for customerId=%d", customerId));

        ImpersonatorInDTO impersonatorInDTO = ImpersonatorInDTO.fromAuthenticatedUser(AuthenticatedUserManager.getAuthenticatedUser());
        impersonatorInDTO.setSpoofUnit(unit);

        return Optional.ofNullable(
                doPost(ImpersonateAuthenticationTokenDTO.class, stopWatch, createAudit(ActionType.SPOOF, customerId
                        , "customerSession"), impersonatorInDTO, SSOEndpoints.LOGIN_ADDRESS, customerId));
    }

    @Override
    public Optional<ResponseHolder<String>> logoutCustomerSession(String ssoToken, Long customerId, String unit) {

        StopWatch stopWatch = new StopWatch(log, "ssoCustomerSessionLogout", String.format("Logging out of customer session with ssoToken=%s", ssoToken));

        ImpersonatorInDTO impersonatorInDTO = ImpersonatorInDTO.fromAuthenticatedUser(AuthenticatedUserManager.getAuthenticatedUser());
        impersonatorInDTO.setSpoofUnit(unit);

        return Optional.ofNullable(
                doPost(String.class, stopWatch, createAudit(ActionType.SPOOF, customerId
                                , "customerSession"), impersonatorInDTO, SSOEndpoints.LOGOUT_ADDRESS, ssoToken));
    }

    @Override
    public Optional<Set<SpoofButtonDTO>> getSpoofButtons(Boolean nonMyPanera) {
        if(nonMyPanera == null) {
            nonMyPanera = false;
        }
        Set<SpoofButtonDTO> spoofButtons = new HashSet<>();
        for(int i = 1; i<100; i++) {
            if (this.appConfigService.doesAppConfigExist( (nonMyPanera ? APP_CONFIG_UI_ANONYMOUS_ORDER_URL + '.' : "spoof.button.") + i)) {
                try {
                    String json = this.appConfigService.getAppConfigValueByCode((nonMyPanera ? APP_CONFIG_UI_ANONYMOUS_ORDER_URL + '.' : "spoof.button.") + i).get();
                    ObjectMapper m = new ObjectMapper();
                    spoofButtons.add(m.readValue(json, new TypeReference<SpoofButtonDTO>(){}));
                }catch (Exception e){
                    System.out.println("Exception: " + e);
                }
            } else {
                break;
            }
        }
        return Optional.of(spoofButtons);
    }

    @Override
    protected String getSubjectName() {
        return AUDIT_SUBJECT_SPOOFING;
    }

    private boolean isValidUnit(String unit){
        Optional<Set<SpoofButtonDTO>> spoofButtons = this.getSpoofButtons(null);
        if(spoofButtons.isPresent()) {
            for (SpoofButtonDTO spoofButton : spoofButtons.get()) {
                if (spoofButton.getUnit().equalsIgnoreCase(unit)) {
                    return true;
                }
            }
        }
        return false;
    }
}
