package org.springframework.samples.petclinic.service;

import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.model.AuthToken;
import org.springframework.samples.petclinic.model.User;
import org.springframework.samples.petclinic.repository.AuthTokenRepository;
import org.springframework.samples.petclinic.util.HashUtil;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class AuthTokenServiceImpl implements AuthTokenService {

    @Autowired
    private AuthTokenRepository repository;

    @Override
    @Transactional
    public AuthToken putToken(Integer userId, String token, LocalDateTime expiryDate) {
        AuthToken authToken = new AuthToken();
        authToken.setUser(new User(userId));
        authToken.setExpiryDate(expiryDate.toDate());
        authToken.setToken(HashUtil.hashString(token));
        
        repository.deleteByUserAndExpiryDateBefore(new User(userId), LocalDateTime.now().toDate());
        return repository.save(authToken);
    }

    @Override
    @Transactional
    public void removeTokenByUserId(Integer userId) {
        //repository.delete(userId);
    	repository.deleteByUserAndExpiryDateBefore(new User(userId), LocalDateTime.now().toDate());
    }

    @Override
    public boolean existsToken(Integer userId, String token) {
        AuthToken authToken = repository.findByUserAndToken(new User(userId), HashUtil.hashString(token));
        return authToken != null;
    }

}
