package com.virtualmarathon.Authorization.controller;

import com.virtualmarathon.Authorization.model.CreateUserRequestModel;
import com.virtualmarathon.Authorization.model.CreateUserResponseModel;
import com.virtualmarathon.Authorization.service.EncryptionDecryption;
import com.virtualmarathon.Authorization.service.UserService;
import com.virtualmarathon.Authorization.service.UserServiceImpl;
import com.virtualmarathon.Authorization.shared.UserDto;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/user")
public class UserController {
    Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private Environment env;
    @Autowired
    private UserService userService;
    @Autowired
    EncryptionDecryption encryptionDecryption;


    @PostMapping("/register")
    public ResponseEntity<String> createUser(@Valid @RequestBody CreateUserRequestModel userDetails){
        ModelMapper modelMapper=new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserDto userDto=modelMapper.map(userDetails,UserDto.class);
        if (userService.getUserDetailsByEmail(userDto.getEmail())!=null){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(null);
        }
        else{
            String userInfo=String.join(",",userDetails.getEmail(),userDetails.getPassword(),"Virtual-Marathon-Sign");
            LOGGER.info("String before encryption:{}",userInfo);
            String encryptedUserInfo;
            try {
                encryptedUserInfo= encryptionDecryption.encrypt(userInfo,env.getProperty("token.secret"));
            }catch (Exception e){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.toString());
            }
            encryptedUserInfo=encryptedUserInfo.replace("+","-");
            encryptedUserInfo=encryptedUserInfo.replace("/","_");
            UserServiceImpl.sendVerificationMail(userDetails.getEmail(),"Click on this to verify your email: https://virtual-marathon-frontend-2-urtjok3rza-wl.a.run.app/auth-me?token="+encryptedUserInfo);
        }
        return ResponseEntity.status(HttpStatus.OK).body("OK");
    }

    @PostMapping("/register-with-verified-email/")
    public ResponseEntity<String> createUserWithVerifiedMail(@RequestParam("encrypted-text") String encryptedText) throws UnsupportedEncodingException {
        String decryptedMessage,email,password;
        encryptedText=encryptedText.replace("-","+");
        encryptedText=encryptedText.replace("_","/");
        LOGGER.info("encrypted text:{}",encryptedText);

        try {
            decryptedMessage=encryptionDecryption.decrypt(encryptedText,env.getProperty("token.secret"));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("url is incorrect");
        }
        String[] separatedDecryptedMessage = decryptedMessage.split(",", 3);
        LOGGER.info("decrypted message:{}", (Object) separatedDecryptedMessage);
        if(separatedDecryptedMessage.length != 3 || !separatedDecryptedMessage[2].equals("Virtual-Marathon-Sign")){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Code");
        }

        email=separatedDecryptedMessage[0];
        password=separatedDecryptedMessage[1];
        UserDto userDto = new UserDto();
        userDto.setEmail(email);
        userDto.setPassword(password);
        userDto.setMarathonsCompleted(0L);
        userDto.setCurrentOrganizerCount(0);
        userDto.setTotalPoints(0L);
        UserDto createdUser=userService.createUser(userDto);
        if(createdUser!=null){
            LOGGER.info(createdUser.toString());
            return ResponseEntity.status(HttpStatus.CREATED).body(email);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Account with this email already exists");


    }


}
