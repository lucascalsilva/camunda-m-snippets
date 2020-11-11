package com.example.workflow.loader;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.impl.persistence.entity.GroupEntity;
import org.camunda.bpm.engine.impl.persistence.entity.MembershipEntity;
import org.camunda.bpm.engine.impl.persistence.entity.UserEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserLoader implements CommandLineRunner {

    private final IdentityService identityService;
    private final ObjectMapper mapper;

    @Value("${custom.users.load-file-dir}")
    private String loadFileDir;

    @Override
    public void run(String... args) throws Exception {
        Long userCount = identityService.createUserQuery().count();

        if (userCount == 0) {
            try {
                loadGroups();
                loadUsers();
                loadMemberships();
            }
            catch (Exception ex){
                log.error("Issue when loading files ", ex);
            }
        }
    }

    private void loadGroups() throws IOException {
        File file = loadFile("groups.json");
        if(file.canRead()) {
            GroupEntity[] groups = mapper.readValue(file, GroupEntity[].class);

            Arrays.stream(groups).forEach(identityService::saveGroup);
        }
        else{
            log.warn("File for groups not found.");
        }
    }

    private void loadUsers() throws IOException {
        File file = loadFile("users.json");
        if(file.canRead()) {
            UserEntity[] users = mapper.readValue(file, UserEntity[].class);

            Arrays.stream(users).forEach(identityService::saveUser);
        }
        else{
            log.warn("File for users not found.");
        }
    }

    private void loadMemberships() throws IOException {
        File file = loadFile("memberships.json");
        if(file.canRead()) {
            MembershipEntity[] memberships = mapper.readValue(file, MembershipEntity[].class);

            Arrays.stream(memberships).forEach(membershipEntity -> identityService.createMembership(membershipEntity.getUserId(), membershipEntity.getGroupId()));
        }
        else{
            log.warn("File for memberships not found.");
        }
    }

    private File loadFile(String fileName) {
        String fullFileName = fileName;

        if (loadFileDir != null && !loadFileDir.equalsIgnoreCase("")) {
            fullFileName = loadFileDir + "/" + fileName;
        }
        return new File(fullFileName);
    }
}
