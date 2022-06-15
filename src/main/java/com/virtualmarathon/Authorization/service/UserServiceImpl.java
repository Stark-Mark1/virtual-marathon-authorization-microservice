package com.virtualmarathon.Authorization.service;

import com.virtualmarathon.Authorization.data.UserEntity;
import com.virtualmarathon.Authorization.repository.UserRepository;
import com.virtualmarathon.Authorization.shared.UserDto;
import org.jetbrains.annotations.NotNull;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.ArrayList;


@Service
public class UserServiceImpl implements UserService{
    UserRepository userRepository;
    BCryptPasswordEncoder bCryptPasswordEncoder;
    private static JavaMailSender mailSender;
    static Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    public  UserServiceImpl(UserRepository userRepository,BCryptPasswordEncoder bCryptPasswordEncoder, JavaMailSender mailSender){
        this.userRepository=userRepository;
        this.bCryptPasswordEncoder=bCryptPasswordEncoder;
        UserServiceImpl.mailSender =mailSender;
    }
    @Override
    public UserDto createUser(@NotNull UserDto userDetails) {
        if (userDetails.getEmail()!=null &&
                getUserDetailsByEmail(userDetails.getEmail())==null){
            userDetails.setPassword(bCryptPasswordEncoder.encode(userDetails.getPassword()));
            ModelMapper modelMapper=new ModelMapper();
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            UserEntity userEntity=modelMapper.map(userDetails, UserEntity.class);
            userRepository.save(userEntity);
            return modelMapper.map(userEntity,UserDto.class);
        }
        return null;
    }

    @Override
    public UserDto getUserDetailsByEmail(String email) {
        UserEntity userEntity=userRepository.findByEmail(email);
        if(userEntity!=null){
            return new ModelMapper().map(userEntity,UserDto.class);
        }
        return null;
    }

    @Override
    public UserDetails loadUserByUsername(String username){
        UserEntity userEntity=userRepository.findByEmail(username);
        if(userEntity==null)return null;
        return new User(userEntity.getEmail(),userEntity.getPassword(),true,true,true,true,new ArrayList<>());
    }

    public static void sendVerificationMail(String toEmail,
                                     String mailBody){
        SimpleMailMessage mailMessage=new SimpleMailMessage();

        mailMessage.setSubject("Virtual Marathon Email Verification");
        mailMessage.setFrom("hu.vurtual.marathon@gmail.com");
        mailMessage.setTo(toEmail);
        mailMessage.setText(mailBody);

        LOGGER.info(mailMessage.toString());
        mailSender.send(mailMessage);
    }
}
