package com.panera.cmt.service.chub;

import com.panera.cmt.dto.chub.PasswordDTO;
import com.panera.cmt.dto.chub.WotdDTO;
import com.panera.cmt.entity.ResponseHolder;
import com.panera.cmt.enums.ActionType;
import com.panera.cmt.enums.ChubEndpoints;
import com.panera.cmt.mongo.repository.IAuditRepository;
import com.panera.cmt.service.app_config.IAppConfigLocalService;
import com.panera.cmt.util.StopWatch;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static com.panera.cmt.config.Constants.AUDIT_SUBJECT_PASSWORD;
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;

@Service
@Slf4j
public class CustomerPasswordService extends BaseCustomerHubService implements ICustomerPasswordService {

    @Autowired
    public CustomerPasswordService(IAppConfigLocalService appConfigService, IAuditRepository auditRepository){
        setAppConfigService(appConfigService);
        setAuditRepository(auditRepository);
    }

    @Override
    public Optional<ResponseHolder<String>> adminSetPassword(Long customerId, String password) {
        if (customerId == null || password == null ) {
            createAudit(ActionType.UPDATE, customerId, "adminSetPassword failed");
            return Optional.empty();
        }

        PasswordDTO dto = new PasswordDTO();
        dto.setPassword(password);

        StopWatch stopWatch = new StopWatch(log, "adminSetPassword", String.format("Setting password to admin supplied password for customerId=%d", customerId));
        ResponseHolder<String> response = doPost(String.class, stopWatch, createAudit(ActionType.UPDATE, customerId, "adminSetPassword"), dto, ChubEndpoints.PASSWORD_SET, customerId);

        return Optional.of(response);
    }

    @Override
    public Optional<ResponseHolder<String>> generatePassword(Long customerId) {
        if (customerId == null) {
            return Optional.empty();
        }

        String newPassword;
        StopWatch stopWatch = new StopWatch(log, "generatePassword", String.format("Resetting password to a generated password for customerId=%d", customerId));

        try {
            newPassword = getRandomAdjective().toLowerCase() +
                    getRandomNoun().toLowerCase() +
                    randomNumeric(2);
        } catch (NullPointerException | IOException e) {
            stopWatch.checkPoint(String.format("Generate Password encountered an error while generating password for customerId=%d when opening file e=%s", customerId, e));
            return Optional.empty();
        }

        PasswordDTO dto = new PasswordDTO();
        dto.setPassword(newPassword);

        stopWatch.checkPoint(String.format("Calling to custhub to set generated password for customerId=%d", customerId));
        ResponseHolder<String> response = doPost(String.class, stopWatch, createAudit(ActionType.UPDATE, customerId, "generatePassword"), dto, ChubEndpoints.PASSWORD_SET, customerId);

        response.setEntity(newPassword);

        return Optional.of(response);
    }

    @Override
    public Optional<WotdDTO> getWordOfTheDay(){
        if (appConfigService.doesAppConfigExist("password.word_of_the_day")
                && appConfigService.getAppConfigValueByCode("password.word_of_the_day").isPresent()) {
            String wotd = "";
            try {
                String json = appConfigService.getAppConfigValueByCode("password.word_of_the_day").get();
                String[] words = json.split(",");
                Calendar c = Calendar.getInstance();
                c.setTime(new Date());
                int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
                if(dayOfWeek <= words.length) {
                    wotd = words[dayOfWeek - 1];
                }
            }catch (Exception e){
                System.out.println("Exception: " + e);
            }
            return Optional.of(new WotdDTO(wotd));
        }

        return Optional.empty();
    }

    @Override
    public void sendResetPasswordEmail(Long customerId) {
        if (customerId != null) {
            StopWatch stopWatch = new StopWatch(log, "sendResetPasswordEmail", String.format("Sending password reset email to customerId=%d", customerId));
            doPost(String.class, stopWatch, createAudit(ActionType.UPDATE, customerId, "sendPasswordReset"), "{}", ChubEndpoints.PASSWORD_SEND_RESET, customerId);
        }
    }

    @Override
    protected String getSubjectName() {
        return AUDIT_SUBJECT_PASSWORD;
    }

    private String getRandomAdjective() throws IOException {
        return getRandomLineFromFile("/adjectives.txt", 433);
    }
    private String getRandomNoun() throws IOException {
        return getRandomLineFromFile("/nouns.txt", 1051);
    }
    private String getRandomLineFromFile(String fileName, int lineCount) throws IOException {
        ClassPathResource file = new ClassPathResource(fileName);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
        int wantedLine = new Random().nextInt(lineCount);
        String result = "";
        int lineCounter = 0;
        while ((result = bufferedReader.readLine()) != null)   {
            if (lineCounter == wantedLine) {
                break;
            }
            lineCounter++;
        }
        return result;
    }
}
